/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v2.0 which accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.suite.engine;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.suite.commons.SuiteLauncherDiscoveryRequestBuilder.request;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.OpenTest4JAwareThrowableCollector;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryResult;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.platform.suite.commons.SuiteLauncherDiscoveryRequestBuilder;
import org.junit.platform.suite.engine.parameters.IParallelSuiteConfigurationParametersProvider;
import org.junit.platform.suite.engine.parameters.ParallelSuiteConfigurationParameters;

// @Immutable
final class ParallelSuiteTestDescriptor extends AbstractTestDescriptor
        implements IParallelSuiteConfigurationParametersProvider {

    static final String SEGMENT_TYPE = "parallelSuite";

    private final SuiteLauncherDiscoveryRequestBuilder discoveryRequestBuilder = request();

    private final ParallelSuiteConfigurationParameters configurationParameters;

    private LauncherDiscoveryResult launcherDiscoveryResult;
    private ParallelSuiteLauncher launcher;

    ParallelSuiteTestDescriptor(final UniqueId id, final Class<?> suiteClass,
            final ConfigurationParameters parentConfigurationParameters) {
        super(id, getSuiteDisplayName(suiteClass), ClassSource.from(suiteClass));
        this.configurationParameters = new ParallelSuiteConfigurationParameters(suiteClass,
                parentConfigurationParameters);
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

        final LauncherDiscoveryRequest request = discoveryRequestBuilder.filterStandardClassNamePatterns(true)
                .enableImplicitConfigurationParameters(false)
                .parentConfigurationParameters(configurationParameters)
                .applyConfigurationParametersFromSuite(configurationParameters.getSuiteClass())
                .build();
        this.launcher = ParallelSuiteLauncher.create();
        this.launcherDiscoveryResult = launcher.discover(request, getUniqueId());
        launcherDiscoveryResult.getTestEngines()
                .stream()
                .map(testEngine -> launcherDiscoveryResult.getEngineTestDescriptor(testEngine))
                .forEach(this::addChild);
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    private static String getSuiteDisplayName(final Class<?> testClass) {
        return findAnnotation(testClass, SuiteDisplayName.class).map(SuiteDisplayName::value)
                .filter(org.junit.platform.commons.util.StringUtils::isNotBlank)
                .orElse(testClass.getSimpleName());
    }

    void execute(final EngineExecutionListener parentEngineExecutionListener) {
        parentEngineExecutionListener.executionStarted(this);
        final ThrowableCollector throwableCollector = new OpenTest4JAwareThrowableCollector();

        final List<Method> beforeSuiteMethods = LifecycleMethodUtils
                .findBeforeSuiteMethods(configurationParameters.getSuiteClass(), throwableCollector);
        final List<Method> afterSuiteMethods = LifecycleMethodUtils
                .findAfterSuiteMethods(configurationParameters.getSuiteClass(), throwableCollector);

        executeBeforeSuiteMethods(beforeSuiteMethods, throwableCollector);

        final TestExecutionSummary summary = executeTests(parentEngineExecutionListener, throwableCollector);

        executeAfterSuiteMethods(afterSuiteMethods, throwableCollector);

        final TestExecutionResult testExecutionResult = computeTestExecutionResult(summary, throwableCollector);
        parentEngineExecutionListener.executionFinished(this, testExecutionResult);
    }

    private void executeBeforeSuiteMethods(final List<Method> beforeSuiteMethods,
            final ThrowableCollector throwableCollector) {
        if (throwableCollector.isNotEmpty()) {
            return;
        }
        for (final Method beforeSuiteMethod : beforeSuiteMethods) {
            throwableCollector.execute(
                    () -> org.junit.platform.commons.util.ReflectionUtils.invokeMethod(beforeSuiteMethod, null));
            if (throwableCollector.isNotEmpty()) {
                return;
            }
        }
    }

    private TestExecutionSummary executeTests(final EngineExecutionListener parentEngineExecutionListener,
            final ThrowableCollector throwableCollector) {
        if (throwableCollector.isNotEmpty()) {
            return null;
        }

        // #2838: The discovery result from a suite may have been filtered by
        // post discovery filters from the launcher. The discovery result should
        // be pruned accordingly.
        final LauncherDiscoveryResult discoveryResult = this.launcherDiscoveryResult
                .withRetainedEngines(getChildren()::contains);
        return launcher.execute(discoveryResult, parentEngineExecutionListener);
    }

    private void executeAfterSuiteMethods(final List<Method> afterSuiteMethods,
            final ThrowableCollector throwableCollector) {
        for (final Method afterSuiteMethod : afterSuiteMethods) {
            throwableCollector.execute(
                    () -> org.junit.platform.commons.util.ReflectionUtils.invokeMethod(afterSuiteMethod, null));
        }
    }

    private TestExecutionResult computeTestExecutionResult(final TestExecutionSummary summary,
            final ThrowableCollector throwableCollector) {
        if (throwableCollector.isNotEmpty()) {
            return TestExecutionResult.failed(throwableCollector.getThrowable());
        }
        if (configurationParameters.isFailIfNoTests() && summary.getTestsFoundCount() == 0) {
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

}
