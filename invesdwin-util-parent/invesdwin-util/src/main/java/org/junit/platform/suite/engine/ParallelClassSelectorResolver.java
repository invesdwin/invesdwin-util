package org.junit.platform.suite.engine;

import static org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.UniqueId.Segment;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;
import org.junit.platform.suite.engine.parameters.IParallelSuiteConfigurationParametersProvider;

// @Immutable
final class ParallelClassSelectorResolver implements SelectorResolver {

    static final IsParallelSuiteClass IS_PARALLEL_SUITE = new IsParallelSuiteClass();
    private static final org.junit.platform.commons.logging.Logger LOG = org.junit.platform.commons.logging.LoggerFactory
            .getLogger(ParallelClassSelectorResolver.class);

    private final Predicate<String> classNameFilter;
    private final ParallelSuiteEngineDescriptor suiteEngineDescriptor;
    private final ConfigurationParameters configurationParameters;

    ParallelClassSelectorResolver(final Predicate<String> classNameFilter,
            final ParallelSuiteEngineDescriptor suiteEngineDescriptor,
            final ConfigurationParameters configurationParameters) {
        this.classNameFilter = classNameFilter;
        this.suiteEngineDescriptor = suiteEngineDescriptor;
        this.configurationParameters = configurationParameters;
    }

    @Override
    public Resolution resolve(final ClassSelector selector, final Context context) {
        final Class<?> testClass = selector.getJavaClass();
        if (IS_PARALLEL_SUITE.test(testClass)) {
            if (classNameFilter.test(testClass.getName())) {
                final Optional<ParallelSuiteTestDescriptor> suiteWithDiscoveryRequest = context
                        .addToParent(parent -> newSuiteDescriptor(testClass, parent))
                        .map(suite -> suite.addDiscoveryRequestFrom(testClass));
                return toResolution(suiteWithDiscoveryRequest);
            }
        }
        return unresolved();
    }

    @Override
    public Resolution resolve(final UniqueIdSelector selector, final Context context) {
        final UniqueId uniqueId = selector.getUniqueId();
        final UniqueId engineId = suiteEngineDescriptor.getUniqueId();
        final List<Segment> resolvedSegments = engineId.getSegments();
        return uniqueId.getSegments()
                .stream()
                .skip(resolvedSegments.size())
                .findFirst()
                .filter(suiteSegment -> ParallelSuiteTestDescriptor.SEGMENT_TYPE.equals(suiteSegment.getType()))
                .flatMap(ParallelClassSelectorResolver::tryLoadSuiteClass)
                .filter(IS_PARALLEL_SUITE)
                .map(suiteClass -> context.addToParent(parent -> newSuiteDescriptor(suiteClass, parent))
                        .map(suite -> uniqueId.equals(suite.getUniqueId())
                                // The uniqueId selector either targeted a class annotated with @Suite;
                                ? suite.addDiscoveryRequestFrom(suiteClass)
                                // or a specific test in that suite
                                : suite.addDiscoveryRequestFrom(uniqueId)))
                .map(ParallelClassSelectorResolver::toResolution)
                .orElseGet(Resolution::unresolved);
    }

    static Optional<Class<?>> tryLoadSuiteClass(final UniqueId.Segment segment) {
        return org.junit.platform.commons.util.ReflectionUtils.tryToLoadClass(segment.getValue()).toOptional();
    }

    static Resolution toResolution(final Optional<ParallelSuiteTestDescriptor> suite) {
        return suite.map(Match::exact).map(Resolution::match).orElseGet(Resolution::unresolved);
    }

    private Optional<ParallelSuiteTestDescriptor> newSuiteDescriptor(final Class<?> suiteClass,
            final TestDescriptor parent) {
        final UniqueId id = parent.getUniqueId().append(ParallelSuiteTestDescriptor.SEGMENT_TYPE, suiteClass.getName());
        if (containsCycle(id)) {
            LOG.config(() -> createConfigContainsCycleMessage(suiteClass, id));
            return Optional.empty();
        }
        final IParallelSuiteConfigurationParametersProvider parentParallelSuite = findParentParallelSuite(parent);
        final ConfigurationParameters parentConfigurationParameters;
        if (parentParallelSuite != null) {
            parentConfigurationParameters = parentParallelSuite.getConfigurationParameters();
        } else {
            parentConfigurationParameters = configurationParameters;
        }
        return Optional.of(new ParallelSuiteTestDescriptor(id, suiteClass, parentConfigurationParameters));
    }

    private IParallelSuiteConfigurationParametersProvider findParentParallelSuite(final TestDescriptor parent) {
        if (parent instanceof IParallelSuiteConfigurationParametersProvider) {
            return (IParallelSuiteConfigurationParametersProvider) parent;
        }
        TestDescriptor prevInheritValue = parent;
        Optional<TestDescriptor> inherit = Optional.ofNullable(parent);
        while (inherit.isPresent()) {
            final TestDescriptor inheritValue = inherit.get();
            if (prevInheritValue == inheritValue) {
                //prevent endless recursion
                return null;
            }
            if (inheritValue instanceof IParallelSuiteConfigurationParametersProvider) {
                return (IParallelSuiteConfigurationParametersProvider) inheritValue;
            }
            inherit = inheritValue.getParent();
            prevInheritValue = inheritValue;
        }
        return null;
    }

    private static boolean containsCycle(final UniqueId id) {
        final List<Segment> segments = id.getSegments();
        final List<Segment> engineAndSuiteSegment = segments.subList(segments.size() - 2, segments.size());
        final List<Segment> ancestorSegments = segments.subList(0, segments.size() - 2);
        for (int i = 0; i < ancestorSegments.size() - 1; i++) {
            final List<Segment> candidate = ancestorSegments.subList(i, i + 2);
            if (engineAndSuiteSegment.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private static String createConfigContainsCycleMessage(final Class<?> suiteClass, final UniqueId suiteId) {
        //CHECKSTYLE:OFF
        return String.format(
                "The suite configuration of [%s] resulted in a cycle [%s] and will not be discovered a second time.",
                suiteClass.getName(), suiteId);
        //CHECKSTYLE:ON
    }

}
