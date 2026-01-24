package org.junit.platform.suite.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.engine.support.store.Namespace;
import org.junit.platform.engine.support.store.NamespacedHierarchicalStore;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;

// @Immutable
public final class ParallelSuiteTestEngine implements TestEngine {

    private static final INestedExecutor EXECUTOR = new ANestedExecutor(ParallelSuiteTestEngine.class.getSimpleName()) {
        @Override
        protected WrappedExecutorService newNestedExecutor(final String nestedName) {
            return Executors.newFixedThreadPool(nestedName, Executors.getCpuThreadPoolCount());
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

        engineExecutionListener.executionStarted(suiteEngineDescriptor);

        final List<Future<?>> futures = new ArrayList<>();
        final Set<? extends TestDescriptor> children = suiteEngineDescriptor.getChildren();
        for (final TestDescriptor child : children) {
            final ParallelSuiteTestDescriptor cChild = (ParallelSuiteTestDescriptor) child;
            //first run parallel suites in parallel
            if (cChild.isParallelSuites()) {
                futures.add(EXECUTOR.getNestedExecutor().submit(() -> {
                    cChild.execute(engineExecutionListener, requestLevelStore);
                }));
            }
        }
        Futures.waitPropagatingNoInterrupt(futures);
        for (final TestDescriptor child : children) {
            final ParallelSuiteTestDescriptor cChild = (ParallelSuiteTestDescriptor) child;
            //then execute sequential suites afterwards
            if (!cChild.isParallelSuites()) {
                cChild.execute(engineExecutionListener, requestLevelStore);
            }
        }
        engineExecutionListener.executionFinished(suiteEngineDescriptor, TestExecutionResult.successful());
    }

}
