package org.junit.platform.suite.engine;

import static org.junit.platform.commons.support.ModifierSupport.isAbstract;
import static org.junit.platform.commons.support.ModifierSupport.isNotAbstract;
import static org.junit.platform.commons.support.ModifierSupport.isNotPrivate;

import java.util.function.Predicate;

import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.DiscoveryIssue;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter;
import org.junit.platform.engine.support.discovery.DiscoveryIssueReporter.Condition;

import de.invesdwin.util.test.ParallelSuite;

// @Immutable
final class IsParallelSuiteClass implements Predicate<Class<?>> {

    private final Condition<Class<?>> condition;

    IsParallelSuiteClass(final DiscoveryIssueReporter issueReporter) {
        this.condition = isNotPrivateUnlessAbstract(issueReporter) //
                .and(isNotLocal(issueReporter)) //
                .and(isNotInner(issueReporter));
    }

    @Override
    public boolean test(final Class<?> testClass) {
        return hasSuiteAnnotation(testClass) //
                && condition.check(testClass) //
                && isNotAbstract(testClass);
    }

    private boolean hasSuiteAnnotation(final Class<?> testClass) {
        return AnnotationSupport.isAnnotated(testClass, ParallelSuite.class);
    }

    private static Condition<Class<?>> isNotPrivateUnlessAbstract(final DiscoveryIssueReporter issueReporter) {
        // Allow abstract test classes to be private because @Suite is inherited and subclasses may widen access.
        return issueReporter.createReportingCondition(testClass -> isNotPrivate(testClass) || isAbstract(testClass),
                testClass -> createIssue(testClass, "must not be private."));
    }

    private static Condition<Class<?>> isNotLocal(final DiscoveryIssueReporter issueReporter) {
        return issueReporter.createReportingCondition(testClass -> !testClass.isLocalClass(),
                testClass -> createIssue(testClass, "must not be a local class."));
    }

    private static Condition<Class<?>> isNotInner(final DiscoveryIssueReporter issueReporter) {
        return issueReporter.createReportingCondition(
                testClass -> !org.junit.platform.commons.util.ReflectionUtils.isInnerClass(testClass),
                testClass -> createIssue(testClass,
                        "must not be an inner class. Did you forget to declare it static?"));
    }

    private static DiscoveryIssue createIssue(final Class<?> testClass, final String detailMessage) {
        //CHECKSTYLE:OFF
        final String message = String.format("@Suite class '%s' %s It will not be executed.", testClass.getName(),
                detailMessage);
        //CHECKSTYLE:ON
        return DiscoveryIssue.builder(DiscoveryIssue.Severity.WARNING, message) //
                .source(ClassSource.from(testClass)) //
                .build();
    }

}
