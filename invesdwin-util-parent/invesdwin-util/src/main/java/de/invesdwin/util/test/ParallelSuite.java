/*
 * Copyright 2015-2024 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v2.0 which accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package de.invesdwin.util.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;

/**
 * https://stackoverflow.com/a/78303359/67492
 * 
 * https://blogs.oracle.com/javamagazine/post/junit-build-custom-test-engines-java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Testable
public @interface ParallelSuite {

    /**
     * Fail suite if no tests were discovered.
     */
    boolean failIfNoTests() default true;

    /**
     * When false, inherits settings from parent parallel suite. When true, overrides parents settings.
     */
    boolean overrideParentParallelSuite() default false;

    /**
     * Run suites in parallel.
     */
    boolean parallelSuites() default false;

    /**
     * Run classes in parallel.
     */
    boolean parallelClasses() default true;

    /**
     * Run methods in parallel.
     */
    boolean parallelMethods() default false;

}
