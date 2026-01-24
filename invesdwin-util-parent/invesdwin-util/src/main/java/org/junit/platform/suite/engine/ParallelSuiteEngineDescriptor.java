package org.junit.platform.suite.engine;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.suite.engine.parameters.IParallelSuiteConfigurationParametersProvider;
import org.junit.platform.suite.engine.parameters.ParallelSuiteConfigurationParameters;

// @Immutable
final class ParallelSuiteEngineDescriptor extends EngineDescriptor
        implements IParallelSuiteConfigurationParametersProvider {

    static final String ENGINE_ID = "invesdwin-parallel-suite";

    private final ConfigurationParameters configurationParameters;

    ParallelSuiteEngineDescriptor(final UniqueId uniqueId, final DiscoveryIssueReporter issueReporter,
            final ConfigurationParameters parentConfigurationParameters) {
        super(uniqueId, "Invesdwin Parallel Suite");
        final Class<?> suiteClass = uniqueId.getSegments()
                .stream()
                .filter(suiteSegment -> ParallelSuiteTestDescriptor.SEGMENT_TYPE.equals(suiteSegment.getType()))
                .findFirst()
                .flatMap(ParallelClassSelectorResolver::tryLoadSuiteClass)
                .filter(new IsParallelSuiteClass(issueReporter))
                .orElse(null);
        if (suiteClass == null) {
            this.configurationParameters = parentConfigurationParameters;
        } else {
            this.configurationParameters = new ParallelSuiteConfigurationParameters(suiteClass,
                    parentConfigurationParameters);
        }
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public ConfigurationParameters getConfigurationParameters() {
        return configurationParameters;
    }

}
