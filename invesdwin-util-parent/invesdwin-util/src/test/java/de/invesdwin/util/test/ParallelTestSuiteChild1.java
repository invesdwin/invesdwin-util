package de.invesdwin.util.test;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;

@ParallelSuite
@SelectClasses({ ParallelTest1.class, ParallelTest2.class })
@Immutable
public class ParallelTestSuiteChild1 {

}
