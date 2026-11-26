package org.junit.platform.suite.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.duration.Duration;

/**
 * The fan-out join of the parallel suite engine: what it reports, and what it does NOT change.
 * <p>
 * The join used to be one untimed {@code Futures.waitPropagatingNoInterrupt(list)}: no record of WHICH child the engine
 * thread was waiting for. That is why a 30-minute hang in a production suite ended as a surefire fork timeout with a
 * thread dump nobody had asked for and no name attached - the dump showed the wait, not the work. The report below puts
 * the name in the build log, and these cells pin both halves: the content of the report and the join semantics that had
 * to survive getting it (a finished child stays silent, a stalled one is named with its pending siblings, and a failing
 * child still takes the others down with it).
 */
@NotThreadSafe
public class ParallelSuiteStallReportTest {

    private static final Duration POLL_SLICE = new Duration(10, FTimeUnit.MILLISECONDS);
    //sized for a test: the shipped ceiling is minutes, and a cell may not wait that long for its own stall
    private static final Duration REPORT_AFTER = new Duration(100, FTimeUnit.MILLISECONDS);
    private static final Duration REPORT_EVERY = new Duration(50, FTimeUnit.MILLISECONDS);

    @Test
    public void testTheReportNamesTheStalledChildAndItsWait() {
        final Map<String, String> report = ParallelSuiteTestEngine.stallReport("ReferenceFeedsOuterStrategyTest",
                new ArrayList<String>(), 180000000000L);

        Assertions.assertThat(report.get("stalled")).as("the stalled child must be named").isEqualTo(
                "ReferenceFeedsOuterStrategyTest");
        Assertions.assertThat(report.get("waitedMillis")).as("the wait must be reported in a comparable unit")
                .isEqualTo("180000");
        Assertions.assertThat(report.get("stillPendingBehindIt")).as("no siblings pending must be explicit")
                .isEqualTo("none");
        Assertions.checkTrue(report.containsKey("hint"), "the report must say what to look at next");
    }

    @Test
    public void testTheReportListsTheChildrenStillPendingBehindTheStall() {
        final List<String> pending = Arrays.asList("BacktestTestSuite", "ExpressionIndicatorTest");
        final Map<String, String> report = ParallelSuiteTestEngine.stallReport("StatisticsReferenceTestSuite", pending,
                60000000000L);
        final String joined = report.get("stillPendingBehindIt");
        Assertions.checkTrue(joined.contains("BacktestTestSuite"), "every unfinished child must be listed: " + joined);
        Assertions.checkTrue(joined.contains("ExpressionIndicatorTest"),
                "every unfinished child must be listed: " + joined);
        Assertions.checkTrue(!joined.contains("StatisticsReferenceTestSuite"),
                "the stalled child must not be repeated as pending: " + joined);
    }

    @Test
    public void testAStalledChildIsReportedWithItsPendingSiblings() throws Exception {
        final CompletableFuture<Void> stalledChild = new CompletableFuture<Void>();
        final CompletableFuture<Void> sibling = new CompletableFuture<Void>();
        final CapturingListener listener = new CapturingListener();
        final List<ParallelSuiteTestEngine.ChildAwait> awaits = Arrays.asList(childAwait("StalledSuite", stalledChild),
                childAwait("SiblingSuite", sibling));

        final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>();
        final Thread awaiting = new Thread(() -> {
            try {
                ParallelSuiteTestEngine.awaitParallelChildren(listener, suiteDescriptor(), awaits, POLL_SLICE,
                        REPORT_AFTER, REPORT_EVERY);
            } catch (final Throwable t) {
                thrown.set(t);
            }
        }, "ParallelSuiteStallReportTest-awaiting");
        awaiting.start();
        try {
            Assertions.checkTrue(listener.awaitReports(1, 15000L),
                    "a child that never finishes must be named in a report entry, not only in a thread dump");
            final Map<String, String> report = listener.reports.get(0);
            Assertions.assertThat(report.get("stalled")).as("the report must name the child being awaited")
                    .isEqualTo("StalledSuite");
            Assertions.checkTrue(report.get("stillPendingBehindIt").contains("SiblingSuite"),
                    "the children still running behind it must be listed: " + report.get("stillPendingBehindIt"));
            Assertions.checkTrue(!report.get("stillPendingBehindIt").contains("StalledSuite"),
                    "the stalled child must not be listed as pending behind itself");
            //a hang that outlives one report keeps being reported, so the still-pending set stays current
            Assertions.checkTrue(listener.awaitReports(2, 15000L),
                    "the repeat interval must produce a second report while the child is still unfinished");
            Assertions.checkTrue(Long.parseLong(report.get("waitedMillis")) < Long.parseLong(
                    listener.reports.get(1).get("waitedMillis")), "each report must carry the wait so far");
        } finally {
            stalledChild.complete(null);
            sibling.complete(null);
            awaiting.join(15000L);
        }
        Assertions.checkTrue(!awaiting.isAlive(), "the join must return once every child is done");
        Assertions.checkNull(thrown.get(), "a fan-out that completes must not fail: " + thrown.get());
        Assertions.checkTrue(listener.reports.size() > 1,
                "a stall that keeps stalling keeps being reported, so the still-pending set stays current");
    }

    @Test
    public void testAChildThatFinishesIsNeverReported() {
        final CapturingListener listener = new CapturingListener();
        ParallelSuiteTestEngine.awaitParallelChildren(listener, suiteDescriptor(),
                Arrays.asList(childAwait("QuickSuite", CompletableFuture.<Void> completedFuture(null))), POLL_SLICE,
                REPORT_AFTER, REPORT_EVERY);

        Assertions.checkTrue(listener.reports.isEmpty(),
                "a child that finishes must not produce a stall report - the report is the exception, not the rhythm");
    }

    @Test
    public void testAFailedChildCancelsItsSiblingsAndPropagatesItsOwnCause() throws Exception {
        final IllegalStateException childFailure = new IllegalStateException("the suite child blew up");
        final CompletableFuture<Void> failedChild = new CompletableFuture<Void>();
        failedChild.completeExceptionally(childFailure);
        final CompletableFuture<Void> sibling = new CompletableFuture<Void>();
        final List<ParallelSuiteTestEngine.ChildAwait> awaits = Arrays.asList(childAwait("FailingSuite", failedChild),
                childAwait("SiblingSuite", sibling));

        final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>();
        final Thread awaiting = new Thread(() -> {
            try {
                ParallelSuiteTestEngine.awaitParallelChildren(new CapturingListener(), suiteDescriptor(), awaits,
                        POLL_SLICE, REPORT_AFTER, REPORT_EVERY);
            } catch (final Throwable t) {
                thrown.set(t);
            }
        }, "ParallelSuiteStallReportTest-failing");
        awaiting.start();
        awaiting.join(15000L);
        Assertions.checkTrue(!awaiting.isAlive(), "the fan-out join must not stay parked behind a failed child");

        Assertions.checkNotNull(thrown.get(), "a failed child must never be swallowed by the fan-out join");
        //the trunk join propagated the cause itself rather than a wrapper, so the failure a reader sees keeps its type
        Assertions.checkTrue(thrown.get() == childFailure,
                "the child's own exception must be propagated unchanged, but was: " + thrown.get());
        Assertions.checkTrue(sibling.isCancelled(),
                "the siblings of a failed child must be cancelled - the trunk list join did that internally");
        Assertions.checkTrue(sibling.isDone(),
                "a cancelled sibling must also be joined: cancel marks a future done before its body has unwound");
    }

    private static ParallelSuiteTestEngine.ChildAwait childAwait(final String name,
            final CompletableFuture<Void> future) {
        return new ParallelSuiteTestEngine.ChildAwait(descriptor(name), future);
    }

    private static TestDescriptor suiteDescriptor() {
        return descriptor("ParallelSuiteEngine");
    }

    private static TestDescriptor descriptor(final String name) {
        return new AbstractTestDescriptor(UniqueId.forEngine(ParallelSuiteEngineDescriptor.ENGINE_ID).append(
                "suite", name), name) {

            @Override
            public Type getType() {
                return Type.CONTAINER;
            }

        };
    }

    /** Collects the report entries the engine publishes, so a stall becomes an assertion instead of a log line. */
    @NotThreadSafe
    private static final class CapturingListener implements EngineExecutionListener {

        private final List<Map<String, String>> reports = new ArrayList<Map<String, String>>();

        @Override
        public synchronized void reportingEntryPublished(final TestDescriptor descriptor, final ReportEntry entry) {
            reports.add(entry.getKeyValuePairs());
        }

        private synchronized int reportCount() {
            return reports.size();
        }

        /** Polls until at least {@code expectedCount} reports have landed, so the cell never needs a fixed sleep. */
        private boolean awaitReports(final int expectedCount, final long timeoutMillis) throws InterruptedException {
            final long deadlineMillis = FDateMillis.nowMillis() + timeoutMillis;
            while (reportCount() < expectedCount && FDateMillis.nowMillis() < deadlineMillis) {
                FTimeUnit.MILLISECONDS.sleep(10L);
            }
            return reportCount() >= expectedCount;
        }

    }

}
