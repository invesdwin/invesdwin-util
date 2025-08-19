package org.junit.platform.suite.engine.parameters;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.DefaultParallelExecutionConfigurationStrategy;
import org.junit.platform.launcher.core.LauncherConfigurationParametersAccessor;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.test.ParallelSuite;

@Immutable
public class ParallelSuiteConfigurationParameters implements ConfigurationParameters {

    private static final String KEY_PARALLEL_SUITES = ParallelSuiteConfigurationParameters.class.getSimpleName()
            + "_PARALLEL_SUITES";
    private static final String KEY_PARALLEL_CLASSES = ParallelSuiteConfigurationParameters.class.getSimpleName()
            + "_PARALLEL_CLASSES";
    private static final String KEY_PARALLEL_METHODS = ParallelSuiteConfigurationParameters.class.getSimpleName()
            + "_PARALLEL_METHODS";

    private final Class<?> suiteClass;
    private final ConfigurationParameters parentConfigurationParameters;
    private final boolean failIfNoTests;
    private final boolean overrideParentParallelSuite;
    private final boolean parallelSuites;
    private final boolean parallelClasses;
    private final boolean parallelMethods;
    private final ConfigurationParameters delegate;

    public ParallelSuiteConfigurationParameters(final Class<?> suiteClass,
            final ConfigurationParameters parentConfigurationParameters) {
        this.suiteClass = suiteClass;
        this.parentConfigurationParameters = parentConfigurationParameters;
        this.failIfNoTests = getFailIfNoTests();
        this.overrideParentParallelSuite = getOverrideParentParallelSuite();
        this.parallelSuites = getParallelSuites();
        this.parallelClasses = getParallelClasses();
        this.parallelMethods = getParallelMethods();
        this.delegate = newConfigurationParameters();
    }

    public Class<?> getSuiteClass() {
        return suiteClass;
    }

    public boolean isFailIfNoTests() {
        return failIfNoTests;
    }

    private Boolean getFailIfNoTests() {
        //CHECKSTYLE:OFF
        return findAnnotation(suiteClass, ParallelSuite.class).map(ParallelSuite::failIfNoTests)
                .orElseThrow(() -> new JUnitException(
                        String.format("Suite [%s] was not annotated with @Suite", suiteClass.getName())));
        //CHECKSTYLE:ON
    }

    private boolean getOverrideParentParallelSuite() {
        if (parentConfigurationParameters == null) {
            return true;
        }
        //CHECKSTYLE:OFF
        return findAnnotation(suiteClass, ParallelSuite.class).map(ParallelSuite::overrideParentParallelSuite)
                .orElseThrow(() -> new JUnitException(
                        String.format("Suite [%s] was not annotated with @Suite", suiteClass.getName())));
        //CHECKSTYLE:ON
    }

    private boolean getParallelSuites() {
        if (!overrideParentParallelSuite) {
            final Optional<Boolean> parentParam = parentConfigurationParameters.getBoolean(KEY_PARALLEL_SUITES);
            if (parentParam.isPresent()) {
                return parentParam.get();
            }
        }
        //CHECKSTYLE:OFF
        return findAnnotation(suiteClass, ParallelSuite.class).map(ParallelSuite::parallelSuites)
                .orElseThrow(() -> new JUnitException(
                        String.format("Suite [%s] was not annotated with @Suite", suiteClass.getName())));
        //CHECKSTYLE:ON
    }

    private boolean getParallelClasses() {
        if (!overrideParentParallelSuite) {
            final Optional<Boolean> parentParam = parentConfigurationParameters.getBoolean(KEY_PARALLEL_CLASSES);
            if (parentParam.isPresent()) {
                return parentParam.get();
            }
        }
        //CHECKSTYLE:OFF
        return findAnnotation(suiteClass, ParallelSuite.class).map(ParallelSuite::parallelClasses)
                .orElseThrow(() -> new JUnitException(
                        String.format("Suite [%s] was not annotated with @Suite", suiteClass.getName())));
        //CHECKSTYLE:ON
    }

    private boolean getParallelMethods() {
        if (!overrideParentParallelSuite) {
            final Optional<Boolean> parentParam = parentConfigurationParameters.getBoolean(KEY_PARALLEL_METHODS);
            if (parentParam.isPresent()) {
                return parentParam.get();
            }
        }
        //CHECKSTYLE:OFF
        return findAnnotation(suiteClass, ParallelSuite.class).map(ParallelSuite::parallelMethods)
                .orElseThrow(() -> new JUnitException(
                        String.format("Suite [%s] was not annotated with @Suite", suiteClass.getName())));
        //CHECKSTYLE:ON
    }

    public boolean isParallelSuites() {
        return parallelSuites;
    }

    public boolean isParallelClasses() {
        return parallelClasses;
    }

    public boolean isParallelMethods() {
        return parallelMethods;
    }

    private ConfigurationParameters newConfigurationParameters() {
        final Map<String, String> overrideParamters = new LinkedHashMap<>();
        if (overrideParentParallelSuite) {
            overrideParamters.put(KEY_PARALLEL_SUITES, String.valueOf(parallelSuites));
            overrideParamters.put(KEY_PARALLEL_CLASSES, String.valueOf(parallelClasses));
            overrideParamters.put(KEY_PARALLEL_METHODS, String.valueOf(parallelMethods));
        }
        overrideParamters.put(DefaultParallelExecutionConfigurationStrategy.CONFIG_STRATEGY_PROPERTY_NAME,
                String.valueOf(DefaultParallelExecutionConfigurationStrategy.FIXED));
        overrideParamters.put(DefaultParallelExecutionConfigurationStrategy.CONFIG_FIXED_PARALLELISM_PROPERTY_NAME,
                String.valueOf(Executors.getCpuThreadPoolCount()));
        overrideParamters.put(JupiterConfiguration.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME,
                String.valueOf(parallelClasses || parallelMethods));
        overrideParamters.put(JupiterConfiguration.DEFAULT_CLASSES_EXECUTION_MODE_PROPERTY_NAME,
                String.valueOf(newExecutionMode(parallelClasses)));
        overrideParamters.put(JupiterConfiguration.DEFAULT_EXECUTION_MODE_PROPERTY_NAME,
                String.valueOf(newExecutionMode(parallelMethods)));
        return LauncherConfigurationParametersAccessor.overrideParamters(parentConfigurationParameters,
                overrideParamters);
    }

    private ExecutionMode newExecutionMode(final boolean parallel) {
        if (parallel) {
            return ExecutionMode.CONCURRENT;
        } else {
            return ExecutionMode.SAME_THREAD;
        }
    }

    @Override
    public Optional<String> get(final String key) {
        return delegate.get(key);
    }

    @Override
    public Optional<Boolean> getBoolean(final String key) {
        return delegate.getBoolean(key);
    }

    @Deprecated
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

}
