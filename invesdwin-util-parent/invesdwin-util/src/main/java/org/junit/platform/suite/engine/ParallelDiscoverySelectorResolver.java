package org.junit.platform.suite.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;

// @Immutable
final class ParallelDiscoverySelectorResolver {

    private static final EngineDiscoveryRequestResolver<ParallelSuiteEngineDescriptor> RESOLVER = EngineDiscoveryRequestResolver
            .<ParallelSuiteEngineDescriptor> builder()
            .addClassContainerSelectorResolver(new IsSuiteClass())
            .addSelectorResolver(context -> new ParallelClassSelectorResolver(context.getClassNameFilter(),
                    context.getEngineDescriptor(), context.getDiscoveryRequest().getConfigurationParameters()))
            .build();

    private static void discoverSuites(final ParallelSuiteEngineDescriptor engineDescriptor) {
        engineDescriptor.getChildren()
                .stream()
                .map(ParallelSuiteTestDescriptor.class::cast)
                .forEach(ParallelSuiteTestDescriptor::discover);
    }

    void resolveSelectors(final EngineDiscoveryRequest request, final ParallelSuiteEngineDescriptor engineDescriptor) {
        RESOLVER.resolve(request, engineDescriptor);
        discoverSuites(engineDescriptor);
        engineDescriptor.accept(TestDescriptor::prune);
    }

}
