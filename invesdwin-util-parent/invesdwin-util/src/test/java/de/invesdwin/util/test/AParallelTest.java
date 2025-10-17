package de.invesdwin.util.test;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public abstract class AParallelTest {

    protected void test(final String methodName) {
        final Thread thread = Thread.currentThread();
        final Instant start = new Instant();
        //CHECKSTYLE:OFF
        System.out.println("[" + thread.getName() + "] " + getClass().getSimpleName() + "." + methodName + " started");
        //CHECKSTYLE:ON
        FTimeUnit.MILLISECONDS.sleepNoInterrupt(100);
        //CHECKSTYLE:OFF
        System.out.println("[" + thread.getName() + "] " + getClass().getSimpleName() + "." + methodName
                + " finished after " + start);
        //CHECKSTYLE:ON
    }

}
