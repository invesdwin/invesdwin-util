package org.junit.platform.suite.engine;

import java.util.function.Predicate;

import org.junit.jupiter.engine.discovery.predicates.IsPotentialTestContainer;
import org.junit.platform.commons.support.AnnotationSupport;

import de.invesdwin.util.test.ParallelSuite;

// @Immutable
final class IsParallelSuiteClass implements Predicate<Class<?>> {

    private static final IsPotentialTestContainer IS_POTENTIAL_TEST_CONTAINER = new IsPotentialTestContainer();

    @Override
    public boolean test(final Class<?> testClass) {
        return IS_POTENTIAL_TEST_CONTAINER.test(testClass) && hasSuiteAnnotation(testClass);
    }

    private boolean hasSuiteAnnotation(final Class<?> testClass) {
        return AnnotationSupport.isAnnotated(testClass, ParallelSuite.class);
    }

}
