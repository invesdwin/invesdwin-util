package org.junit.platform.suite.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;

// @Immutable
final class ParallelDiscoverySelectorResolver {

    // @formatter:off
    private static final EngineDiscoveryRequestResolver<ParallelSuiteEngineDescriptor> RESOLVER = EngineDiscoveryRequestResolver.<ParallelSuiteEngineDescriptor>builder()
            .addClassContainerSelectorResolverWithContext(context -> new IsParallelSuiteClass(context.getIssueReporter()))
            .addSelectorResolver(context -> new ParallelClassSelectorResolver(
                    context.getClassNameFilter(),
                    context.getEngineDescriptor(),
                    context.getDiscoveryRequest().getConfigurationParameters(),
                    context.getDiscoveryRequest().getOutputDirectoryCreator(),
                    context.getDiscoveryRequest().getDiscoveryListener(),
                    context.getIssueReporter()))
            .build();
    // @formatter:on

    private static void discoverSuites(final ParallelSuiteEngineDescriptor engineDescriptor) {
        // @formatter:off
        engineDescriptor.getChildren().stream()
                .map(ParallelSuiteTestDescriptor.class::cast)
                .forEach(ParallelSuiteTestDescriptor::discover);
        // @formatter:on
    }

    void resolveSelectors(final EngineDiscoveryRequest request, final ParallelSuiteEngineDescriptor engineDescriptor) {
        final DiscoveryIssueReporter issueReporter = DiscoveryIssueReporter.deduplicating(
                DiscoveryIssueReporter.forwarding(request.getDiscoveryListener(), engineDescriptor.getUniqueId()));
        RESOLVER.resolve(request, engineDescriptor, issueReporter);
        discoverSuites(engineDescriptor);
        engineDescriptor.accept(TestDescriptor::prune);
    }

}
