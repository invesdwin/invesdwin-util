package org.junit.platform.suite.engine.parameters;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ParallelSuiteProperties {

    private static final String PARALLEL_SUITE_ENABLED = ParallelSuiteProperties.class.getName()
            + ".PARALLEL_SUITE_ENABLED";

    private ParallelSuiteProperties() {}

    public static boolean isParallelSuiteEnabled() {
        //CHECKSTYLE:OFF
        final String value = System.getProperty(PARALLEL_SUITE_ENABLED);
        //CHECKSTYLE:ON
        if (value == null) {
            return true;
        }
        return Boolean.parseBoolean(value);
    }

    public static void setParallelSuiteEnabled(final Boolean parallelSuiteEnabled) {
        if (parallelSuiteEnabled == null) {
            //CHECKSTYLE:OFF
            System.clearProperty(PARALLEL_SUITE_ENABLED);
            //CHECKSTYLE:ON
        } else {
            //CHECKSTYLE:OFF
            System.setProperty(PARALLEL_SUITE_ENABLED, String.valueOf(parallelSuiteEnabled));
            //CHECKSTYLE:ON
        }
    }

}
