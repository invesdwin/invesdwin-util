package de.invesdwin.util.test;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;

@ParallelSuite
@SelectClasses({ ParallelTest3.class, ParallelTest4.class })
@Immutable
public class ParallelTestSuiteChild2 {

}
