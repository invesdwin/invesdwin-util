package org.junit.platform.suite.engine;

import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.joining;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.util.FunctionUtils.where;
import static org.junit.platform.suite.engine.SuiteLauncherDiscoveryRequestBuilder.request;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.CancellationToken;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.DiscoveryIssue;
import org.junit.platform.engine.EngineDiscoveryListener;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.OutputDirectoryCreator;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.UniqueId.Segment;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.junit.platform.engine.support.store.Namespace;
import org.junit.platform.engine.support.store.NamespacedHierarchicalStore;
import org.junit.platform.launcher.LauncherDiscoveryListener;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryResult;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.platform.suite.engine.parameters.IParallelSuiteConfigurationParametersProvider;
import org.junit.platform.suite.engine.parameters.ParallelSuiteConfigurationParameters;

// @Immutable
final class ParallelSuiteTestDescriptor extends AbstractTestDescriptor
        implements IParallelSuiteConfigurationParametersProvider {

    static final String SEGMENT_TYPE = "parallelSuite";

    private final SuiteLauncherDiscoveryRequestBuilder discoveryRequestBuilder = request();
    private final ParallelSuiteConfigurationParameters configurationParameters;
    private final OutputDirectoryCreator outputDirectoryCreator;
    private final LifecycleMethods lifecycleMethods;

    private @Nullable LauncherDiscoveryResult launcherDiscoveryResult;
    private @Nullable ParallelSuiteLauncher launcher;

    ParallelSuiteTestDescriptor(final UniqueId id, final Class<?> suiteClass,
            final ConfigurationParameters parentConfigurationParameters,
            final OutputDirectoryCreator outputDirectoryCreator, final EngineDiscoveryListener discoveryListener,
            final DiscoveryIssueReporter issueReporter) {
        super(id, getSuiteDisplayName(suiteClass, issueReporter), ClassSource.from(suiteClass));
        this.configurationParameters = new ParallelSuiteConfigurationParameters(suiteClass,
                parentConfigurationParameters);
        this.outputDirectoryCreator = outputDirectoryCreator;
        this.lifecycleMethods = new LifecycleMethods(suiteClass, issueReporter);
        this.discoveryRequestBuilder.listener(DiscoveryIssueForwardingListener.create(id, discoveryListener));
    }

    @Override
    public ConfigurationParameters getConfigurationParameters() {
        return configurationParameters;
    }

    public boolean isParallelSuites() {
        return configurationParameters.isParallelSuites();
    }

    ParallelSuiteTestDescriptor addDiscoveryRequestFrom(final Class<?> suiteClass) {
        org.junit.platform.commons.util.Preconditions.condition(launcherDiscoveryResult == null,
                "discovery request cannot be modified after discovery");
        discoveryRequestBuilder.applySelectorsAndFiltersFromSuite(suiteClass);
        return this;
    }

    ParallelSuiteTestDescriptor addDiscoveryRequestFrom(final UniqueId uniqueId) {
        org.junit.platform.commons.util.Preconditions.condition(launcherDiscoveryResult == null,
                "discovery request cannot be modified after discovery");
        discoveryRequestBuilder.selectors(DiscoverySelectors.selectUniqueId(uniqueId));
        return this;
    }

    void discover() {
        if (launcherDiscoveryResult != null) {
            return;
        }

        // @formatter:off
        final LauncherDiscoveryRequest request = discoveryRequestBuilder
                .filterStandardClassNamePatterns()
                .disableImplicitConfigurationParameters()
                .parentConfigurationParameters(configurationParameters)
                .applyConfigurationParametersFromSuite(configurationParameters.getSuiteClass())
                .outputDirectoryCreator(outputDirectoryCreator)
                .build();
        // @formatter:on
        this.launcher = ParallelSuiteLauncher.create();
        this.launcherDiscoveryResult = launcher.discover(request, getUniqueId());
        // @formatter:off
        launcherDiscoveryResult.getTestEngines()
                .stream()
                .map(testEngine -> launcherDiscoveryResult.getEngineTestDescriptor(testEngine))
                .forEach(this::addChild);
        // @formatter:on
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    private static String getSuiteDisplayName(final Class<?> suiteClass, final DiscoveryIssueReporter issueReporter) {
        // @formatter:off
        final Predicate<String> nonBlank = issueReporter.createReportingCondition(org.junit.platform.commons.util.StringUtils::isNotBlank, __ -> {
            //CHECKSTYLE:OFF
            final String message = String.format("@SuiteDisplayName on %s must be declared with a non-blank value.",
                    suiteClass.getName());
            //CHECKSTYLE:ON
            return DiscoveryIssue.builder(DiscoveryIssue.Severity.WARNING, message)
                    .source(ClassSource.from(suiteClass))
                    .build();
        }).toPredicate();

        return findAnnotation(suiteClass, SuiteDisplayName.class)
                .map(SuiteDisplayName::value)
                .filter(nonBlank)
                .orElse(suiteClass.getSimpleName());
        // @formatter:on
    }

    void execute(final EngineExecutionListener executionListener,
            final NamespacedHierarchicalStore<Namespace> requestLevelStore, final CancellationToken cancellationToken) {
        if (cancellationToken.isCancellationRequested()) {
            executionListener.executionSkipped(this, "Execution cancelled");
            return;
        }

        executionListener.executionStarted(this);
        final ThrowableCollector throwableCollector = new OpenTest4JAwareThrowableCollector();

        executeBeforeSuiteMethods(throwableCollector);

        final TestExecutionSummary summary = executeTests(executionListener, requestLevelStore, cancellationToken,
                throwableCollector);

        executeAfterSuiteMethods(throwableCollector);

        final TestExecutionResult testExecutionResult = computeTestExecutionResult(summary, throwableCollector);
        executionListener.executionFinished(this, testExecutionResult);
    }

    private void executeBeforeSuiteMethods(final ThrowableCollector throwableCollector) {
        if (throwableCollector.isNotEmpty()) {
            return;
        }
        for (final Method beforeSuiteMethod : lifecycleMethods.beforeSuite) {
            throwableCollector.execute(() -> ReflectionSupport.invokeMethod(beforeSuiteMethod, null));
            if (throwableCollector.isNotEmpty()) {
                return;
            }
        }
    }

    private @Nullable TestExecutionSummary executeTests(final EngineExecutionListener executionListener,
            final NamespacedHierarchicalStore<Namespace> requestLevelStore, final CancellationToken cancellationToken,
            final ThrowableCollector throwableCollector) {
        if (throwableCollector.isNotEmpty()) {
            return null;
        }

        // #2838: The discovery result from a suite may have been filtered by
        // post discovery filters from the launcher. The discovery result should
        // be pruned accordingly.
        final LauncherDiscoveryResult discoveryResult = java.util.Objects.requireNonNull(this.launcherDiscoveryResult)
                .withRetainedEngines(getChildren()::contains);
        return java.util.Objects.requireNonNull(launcher)
                .execute(discoveryResult, executionListener, requestLevelStore,
                        cancellationToken);
    }

    private void executeAfterSuiteMethods(final ThrowableCollector throwableCollector) {
        for (final Method afterSuiteMethod : lifecycleMethods.afterSuite) {
            throwableCollector.execute(() -> ReflectionSupport.invokeMethod(afterSuiteMethod, null));
        }
    }

    private TestExecutionResult computeTestExecutionResult(final TestExecutionSummary summary,
            final ThrowableCollector throwableCollector) {
        final Throwable throwable = throwableCollector.getThrowable();
        if (throwable != null) {
            return TestExecutionResult.failed(throwable);
        }
        if (configurationParameters.isFailIfNoTests()
                && java.util.Objects.requireNonNull(summary).getTestsFoundCount() == 0) {
            return TestExecutionResult.failed(new NoTestsDiscoveredException(configurationParameters.getSuiteClass()));
        }
        return TestExecutionResult.successful();
    }

    @Override
    public boolean mayRegisterTests() {
        // While a suite will not register new tests after discovery, we pretend
        // it does. This allows the suite to fail if no tests were discovered.
        // Otherwise, the empty suite would be pruned.
        return true;
    }

    private static class LifecycleMethods {

        private final List<Method> beforeSuite;
        private final List<Method> afterSuite;

        LifecycleMethods(final Class<?> suiteClass, final DiscoveryIssueReporter issueReporter) {
            beforeSuite = LifecycleMethodUtils.findBeforeSuiteMethods(suiteClass, issueReporter);
            afterSuite = LifecycleMethodUtils.findAfterSuiteMethods(suiteClass, issueReporter);
        }
    }

    private static final class DiscoveryIssueForwardingListener implements LauncherDiscoveryListener {

        private static final Predicate<Segment> SUITE_SEGMENTS = where(Segment::getType, isEqual(SEGMENT_TYPE));

        private final EngineDiscoveryListener discoveryListener;
        private final BiFunction<UniqueId, DiscoveryIssue, DiscoveryIssue> issueTransformer;

        private DiscoveryIssueForwardingListener(final EngineDiscoveryListener discoveryListener,
                final BiFunction<UniqueId, DiscoveryIssue, DiscoveryIssue> issueTransformer) {
            this.discoveryListener = discoveryListener;
            this.issueTransformer = issueTransformer;
        }

        static DiscoveryIssueForwardingListener create(final UniqueId id,
                final EngineDiscoveryListener discoveryListener) {
            final boolean isNestedSuite = id.getSegments().stream().filter(SUITE_SEGMENTS).count() > 1;
            if (isNestedSuite) {
                return new DiscoveryIssueForwardingListener(discoveryListener, (__, issue) -> issue);
            }
            return new DiscoveryIssueForwardingListener(discoveryListener,
                    (engineUniqueId, issue) -> issue.withMessage(message -> {
                        final String engineId = engineUniqueId.getLastSegment().getValue();
                        if (SuiteEngineDescriptor.ENGINE_ID.equals(engineId)) {
                            return message;
                        }
                        final String suitePath = engineUniqueId.getSegments()
                                .stream() //
                                .filter(SUITE_SEGMENTS) //
                                .map(Segment::getValue) //
                                .collect(joining(" > "));
                        //CHECKSTYLE:OFF
                        if (message.endsWith(".")) {
                            message = message.substring(0, message.length() - 1);
                        }
                        return String.format("[%s] %s (via @Suite %s).", engineId, message, suitePath);
                        //CHECKSTYLE:ON
                    }));
        }

        @Override
        public void issueEncountered(final UniqueId engineUniqueId, final DiscoveryIssue issue) {
            final DiscoveryIssue transformedIssue = this.issueTransformer.apply(engineUniqueId, issue);
            this.discoveryListener.issueEncountered(engineUniqueId, transformedIssue);
        }
    }

}
