package org.junit.platform.suite.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.junit.platform.engine.CancellationToken;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.engine.support.store.Namespace;
import org.junit.platform.engine.support.store.NamespacedHierarchicalStore;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.concurrent.handler.AlwaysUncaughtExecutorExceptionHandler;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateNanos;
import de.invesdwin.util.time.duration.Duration;

// @Immutable
public final class ParallelSuiteTestEngine implements TestEngine {

    /**
     * A child that has been unfinished this long gets named in the build log. Sized from the failure actually
     * measured: a suite that wedged was killed by the runner's fork ceiling (1800 s) with no name for the unfinished
     * work, so the first report lands with room to spare, and above a long-but-healthy suite child so a normal run
     * stays quiet.
     */
    private static final Duration STALL_REPORT_AFTER = new Duration(10, FTimeUnit.MINUTES);
    private static final Duration STALL_REPORT_EVERY = new Duration(5, FTimeUnit.MINUTES);
    /**
     * The fan-out join is polled rather than waited on once. That is also what {@link Futures}' unbounded propagating
     * wait does internally (a task can hold its value without waking the waiter), and the bounded variant of the same
     * family rethrows the child's original cause, so the join semantics of the trunk list are kept.
     */
    private static final Duration JOIN_POLL_SLICE = new Duration(1, FTimeUnit.SECONDS);
    /** How often the post-cancel join re-reads a child's state (the interval {@link Futures} uses for the same wait). */
    private static final long CANCEL_JOIN_POLL_MILLIS = 100L;

    private static final INestedExecutor EXECUTOR = new ANestedExecutor(ParallelSuiteTestEngine.class.getSimpleName()) {
        @Override
        protected WrappedExecutorService newNestedExecutor(final String nestedName) {
            return Executors.newFixedThreadPool(nestedName, Executors.getCpuThreadPoolCount())
                    .setExecutorExceptionHandler(AlwaysUncaughtExecutorExceptionHandler.INSTANCE);
        }
    };

    @Override
    public String getId() {
        return ParallelSuiteEngineDescriptor.ENGINE_ID;
    }

    /**
     * Returns {@code org.junit.platform} as the group ID.
     */
    @Override
    public Optional<String> getGroupId() {
        return Optional.of("de.invesdwin");
    }

    /**
     * Returns {@code junit-platform-suite-engine} as the artifact ID.
     */
    @Override
    public Optional<String> getArtifactId() {
        return Optional.of("invesdwin-parallel-suite-engine");
    }

    @Override
    public TestDescriptor discover(final EngineDiscoveryRequest discoveryRequest, final UniqueId uniqueId) {
        final DiscoveryIssueReporter issueReporter = DiscoveryIssueReporter
                .deduplicating(DiscoveryIssueReporter.forwarding(discoveryRequest.getDiscoveryListener(), uniqueId));
        final ParallelSuiteEngineDescriptor engineDescriptor = new ParallelSuiteEngineDescriptor(uniqueId,
                issueReporter, null);
        new ParallelDiscoverySelectorResolver().resolveSelectors(discoveryRequest, engineDescriptor);
        return engineDescriptor;
    }

    @Override
    public void execute(final ExecutionRequest request) {
        final ParallelSuiteEngineDescriptor suiteEngineDescriptor = (ParallelSuiteEngineDescriptor) request
                .getRootTestDescriptor();
        final EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
        final NamespacedHierarchicalStore<Namespace> requestLevelStore = request.getStore();
        final CancellationToken cancellationToken = request.getCancellationToken();

        engineExecutionListener.executionStarted(suiteEngineDescriptor);

        final List<ChildAwait> awaits = new ArrayList<>();
        final Set<? extends TestDescriptor> children = suiteEngineDescriptor.getChildren();
        for (final TestDescriptor child : children) {
            final ParallelSuiteTestDescriptor cChild = (ParallelSuiteTestDescriptor) child;
            //first run parallel suites in parallel
            if (cChild.isParallelSuites()) {
                final Future<?> future = EXECUTOR.getNestedExecutor().submit(() -> {
                    cChild.execute(engineExecutionListener, requestLevelStore, cancellationToken);
                });
                awaits.add(new ChildAwait(child, future));
            }
        }
        awaitParallelChildren(engineExecutionListener, suiteEngineDescriptor, awaits);
        for (final TestDescriptor child : children) {
            final ParallelSuiteTestDescriptor cChild = (ParallelSuiteTestDescriptor) child;
            //then execute sequential suites afterwards
            if (!cChild.isParallelSuites()) {
                cChild.execute(engineExecutionListener, requestLevelStore, cancellationToken);
            }
        }
        engineExecutionListener.executionFinished(suiteEngineDescriptor, TestExecutionResult.successful());
    }

    /**
     * Awaits every parallel child, and while waiting publishes a stall report through the engine's own listener.
     *
     * The join semantics are the ones the trunk list had: nothing times out and nothing is refused - the run's ceiling
     * stays with the runner (surefire's forkedProcessTimeoutInSeconds), and inventing a second ceiling here would make
     * a suite fail for a reason the runner no longer controls. What the trunk join lost was VISIBILITY: a fan-out wait
     * carries no record of which child it is waiting for, so a stuck suite produced a fork kill at 30 minutes, a thread
     * dump nobody had asked for, and no name for the unfinished work. Publishing a report entry per stalled child is
     * what makes the next such hang readable from the build log.
     */
    private static void awaitParallelChildren(final EngineExecutionListener engineExecutionListener,
            final TestDescriptor suiteEngineDescriptor, final List<ChildAwait> awaits) {
        awaitParallelChildren(engineExecutionListener, suiteEngineDescriptor, awaits, JOIN_POLL_SLICE,
                STALL_REPORT_AFTER, STALL_REPORT_EVERY);
    }

    /**
     * The timings are parameters so that the reporting path can be pinned without hanging a suite for ten minutes; the
     * overload above is the one the engine calls.
     */
    static void awaitParallelChildren(final EngineExecutionListener engineExecutionListener,
            final TestDescriptor suiteEngineDescriptor, final List<ChildAwait> awaits, final Duration pollSlice,
            final Duration reportAfter, final Duration reportEvery) {
        try {
            for (final ChildAwait await : awaits) {
                final long startedNanos = FDateNanos.elapsedNanos();
                long nextReportNanos = startedNanos + reportAfter.nanosValue();
                while (true) {
                    try {
                        Futures.waitPropagatingNoInterrupt(await.future, pollSlice);
                        //this child is done and its own result is clean; the next one is awaited in turn
                        break;
                    } catch (final TimeoutException e) {
                        //the poll slice expired - the child is still running, which is what the report below names
                        final long nowNanos = FDateNanos.elapsedNanos();
                        if (nowNanos >= nextReportNanos) {
                            engineExecutionListener.reportingEntryPublished(suiteEngineDescriptor, ReportEntry
                                    .from(stallReport(describe(await.descriptor), pendingBehind(awaits, await),
                                            nowNanos - startedNanos)));
                            nextReportNanos = nowNanos + reportEvery.nanosValue();
                        }
                    }
                }
            }
        } catch (final Throwable t) {
            //a failing child or an interrupted engine thread still takes the other children down with it: the trunk
            //list join did that internally, and dropping it would leave suite threads writing into the listener after
            //the engine already reported the suite finished.
            cancelAndJoinChildren(awaits, t);
            throw Throwables.propagate(t);
        }
    }

    /**
     * Cancel best effort and then wait for each task body to unwind — the same contract {@link Futures}' own iterable
     * join applies internally when one awaited future fails: {@code cancel(true)} marks a future done before its
     * {@code run()} has returned, so proceeding without the join lets suite threads keep reporting after the engine
     * left.
     */
    private static void cancelAndJoinChildren(final List<ChildAwait> awaits, final Throwable original) {
        for (final ChildAwait await : awaits) {
            try {
                await.future.cancel(true);
            } catch (final Throwable t) {
                Throwables.addSuppressed(original, t);
            }
        }
        boolean interrupted = false;
        for (final ChildAwait await : awaits) {
            while (!await.future.isDone()) {
                try {
                    FTimeUnit.MILLISECONDS.sleep(CANCEL_JOIN_POLL_MILLIS);
                } catch (final InterruptedException e) {
                    //sleep clears the flag, so this stays a wait rather than a spin; it is restored below
                    interrupted = true;
                }
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Pure over names, so the content of a stall report is testable without hanging a suite or building a
     * TestDescriptor: which child is stuck, which ones are still pending behind it, and how long the fan-out has been
     * waiting.
     */
    static Map<String, String> stallReport(final String stalledName, final List<String> stillPending,
            final long waitedNanos) {
        final Map<String, String> report = ILockCollectionFactory.getInstance(false).newLinkedMap();
        report.put("invesdwin.parallel-suite.stall", "true");
        report.put("stalled", stalledName);
        report.put("waitedMillis", Long.valueOf(waitedNanos / 1000000L).toString());
        report.put("stillPendingBehindIt", stillPending.isEmpty() ? "none" : String.join(", ", stillPending));
        report.put("hint", "a stuck fan-out here parks the engine thread with no monitor cycle for jstack to report"
                + " - inspect the holder of the lock the stalled child is waiting for");
        return report;
    }

    private static List<String> pendingBehind(final List<ChildAwait> awaits, final ChildAwait stalled) {
        final List<String> pending = new ArrayList<String>();
        for (final ChildAwait await : awaits) {
            if (await != stalled && !await.future.isDone()) {
                pending.add(describe(await.descriptor));
            }
        }
        return pending;
    }

    private static String describe(final TestDescriptor descriptor) {
        final String displayName = descriptor.getDisplayName();
        return displayName != null && !displayName.isEmpty() ? displayName : String.valueOf(descriptor.getUniqueId());
    }

    /**
     * A parallel child plus its future, so that a wait can be attributed to a name. Package-visible (with its
     * constructor) so the timing overload above can be pinned from a test without a hung suite.
     */
    static final class ChildAwait {

        private final TestDescriptor descriptor;
        private final Future<?> future;

        ChildAwait(final TestDescriptor descriptor, final Future<?> future) {
            this.descriptor = descriptor;
            this.future = future;
        }
    }

}
