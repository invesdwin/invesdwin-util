package de.invesdwin.util.concurrent.priority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Before;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class PriorityThreadPoolExecutorTest {

    private int executed = 0;

    @Before
    public void before() {
        executed = 0;
    }

    @Test
    public void testCallable() throws InterruptedException {
        final List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(new IPriorityCallable<Void>() {
            @Override
            public Void call() throws Exception {
                Assertions.assertThat(executed).isEqualTo(0);
                FTimeUnit.MILLISECONDS.sleep(100);
                return null;
            }

            @Override
            public double getPriority() {
                return 0;
            }
        });
        for (int i = 10; i >= 1; i--) {
            final int priority = i;
            tasks.add(new IPriorityCallable<Void>() {

                @Override
                public Void call() throws Exception {
                    executed++;
                    Assertions.assertThat(executed).isEqualTo(priority);
                    FTimeUnit.MILLISECONDS.sleep(100);
                    return null;
                }

                @Override
                public double getPriority() {
                    return priority;
                }
            });
        }
        final WrappedExecutorService executor = Executors.newFixedPriorityThreadPool("testCallable", 1);
        Futures.submitAndGet(executor, tasks);
    }

    @Test
    public void testRunnable() throws InterruptedException {
        final List<Runnable> tasks = new ArrayList<>();
        tasks.add(new IPriorityRunnable() {
            @Override
            public void run() {
                Assertions.assertThat(executed).isEqualTo(0);
                try {
                    FTimeUnit.MILLISECONDS.sleep(100);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public double getPriority() {
                return 0;
            }
        });
        for (int i = 10; i >= 1; i--) {
            final int priority = i;
            tasks.add(new IPriorityRunnable() {

                @Override
                public void run() {
                    executed++;
                    Assertions.assertThat(executed).isEqualTo(priority);
                    try {
                        FTimeUnit.MILLISECONDS.sleep(100);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public double getPriority() {
                    return priority;
                }
            });
        }
        final WrappedExecutorService executor = Executors.newFixedPriorityThreadPool("testRunnable", 1);
        Futures.submitAndWait(executor, tasks);
    }

}
