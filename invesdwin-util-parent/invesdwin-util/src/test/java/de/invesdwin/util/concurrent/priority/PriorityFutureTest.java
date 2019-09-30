package de.invesdwin.util.concurrent.priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class PriorityFutureTest {

    @Test
    public void testComparator() {
        final List<IPriorityRunnable> providers = new ArrayList<IPriorityRunnable>();
        providers.add(newPriorityRunnable(Double.NaN, 0));
        providers.add(null);
        providers.add(newPriorityRunnable(2000, 0));
        providers.add(newPriorityRunnable(1000, 0));
        providers.add(newPriorityRunnable(1000, 1));
        providers.add(newPriorityRunnable(1000, 2));
        Collections.sort(providers, PriorityFuture.COMPARATOR);
        Assertions.checkEquals(providers.toString(), "[1000.0_0, 1000.0_1, 1000.0_2, 2000.0_0, NaN_0, null]");
    }

    protected IPriorityRunnable newPriorityRunnable(final double priority, final int index) {
        return new IPriorityRunnable() {

            @Override
            public double getPriority() {
                return priority;
            }

            @Override
            public void run() {}

            @Override
            public String toString() {
                return String.valueOf(getPriority()) + "_" + index;
            }
        };
    }

}
