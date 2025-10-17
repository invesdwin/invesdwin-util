package de.invesdwin.util.test;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;

@ParallelSuite
@SelectClasses({ ParallelTestSuiteChild1.class, ParallelTestSuiteChild2.class })
@Immutable
public class ParallelTestSuiteParent {

}
