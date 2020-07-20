package de.invesdwin.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.future.Futures;

@ThreadSafe
public class ExecutorsTest {

    private final AtomicInteger recursion = new AtomicInteger();
    private volatile boolean success;

    @Test
    public void testSingleThreadExecutor() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newFixedThreadPool("testSingleThreadExecutor", 1);
        Assertions.assertThat(executor.getFullPendingCount()).isEqualTo(1);
        for (int i = 0; i < 2; i++) {
            executor.execute(new WaitingRunnable(i));
        }
        executor.shutdown();
        executor.awaitTermination();
    }

    private static class WaitingRunnable implements Runnable {

        private final int id;

        WaitingRunnable(final int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(id + ": beendet"); //SUPPRESS CHECKSTYLE single line
        }
    }

    @Test
    public void testInterruptWithShutdownNow() throws InterruptedException {
        try {
            final ExecutorService executor = Executors.newFixedThreadPool("testInterruptMitShutdownNow", 1);
            final Future<Void> future = executor.submit(new Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    success = true;
                    TimeUnit.DAYS.sleep(9999);
                    return null;
                }
            });
            while (!success) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            executor.shutdownNow();
            Futures.get(future);
            Assertions.failExceptionExpected();
        } catch (final Throwable t) {
            Assertions.assertThat(t).isInstanceOf(InterruptedException.class);
        }
    }

    @Test
    public void testCached() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newCachedThreadPool("testCached");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                success = true;
            }
        });
        executor.shutdown();
        executor.awaitTermination();
        Assertions.assertThat(success).isTrue();
    }

    @Test
    public void testExceptionWithFixedThreadPool() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newFixedThreadPool("testException", 1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Before Exception"); //SUPPRESS CHECKSTYLE single line
                throw new RuntimeException("The Exception");
            }
        });
        executor.shutdown();
        executor.awaitTermination();
    }

    @Test
    public void testExceptionWithScheduledThreadPool() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newScheduledThreadPool("testException", 1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Before Exception"); //SUPPRESS CHECKSTYLE single line
                throw new RuntimeException("The Exception");
            }
        });
        executor.shutdown();
        executor.awaitTermination();
    }

    @Test
    public void testSpecialWait() throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool("testSpecialWait", 1);
        for (int i = 0; i < 3; i++) {
            executor.execute(getWorker(i));
        }
        final Runnable spezial = getWorker(999);
        Futures.submitAndWait(executor, spezial);
        System.out.println("end"); //SUPPRESS CHECKSTYLE single line
    }

    @Test
    public void testDynamic() throws InterruptedException {
        final List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 100; i++) {
            tasks.add(getWorker(i));
        }

        final WrappedExecutorService executor = Executors.newFixedThreadPool("testDynamic", 10);
        Assertions.assertThat(executor.getFullPendingCount()).isEqualTo(10);
        Futures.submitAndWait(executor, tasks);
        System.out.println("end"); //SUPPRESS CHECKSTYLE single line
    }

    //    @Test
    //    public void testStatisch() {
    //        AsyncThreadPoolExecutor executor = (AsyncThreadPoolExecutor) Executors.newFixedThreadPool("testStatisch", 10);
    //        List<Future<?>> futures = new ArrayList<Future<?>>();
    //        for (int i = 1; !executor.aktiveThreadAnzahlErreicht(); i++) {
    //            futures.add(executor.submit(getWorker(i)));
    //        }
    //        for (Future<?> future : futures) {
    //            Assert.assertNull(SynchronisationUtil.get(future));
    //        }
    //        log.debug("ende");
    //    }

    @Test
    public void testRecursiveWorker() throws InterruptedException {
        final int rekusionen = 100;
        final WrappedExecutorService executor = Executors.newFixedThreadPool("testRecursiveWorker", 2);
        Assertions.assertThat(executor.getFullPendingCount()).isEqualTo(2);
        final Object result = new Object();
        final Future<Object> future = executor.submit(getRecursiveWorker(executor, rekusionen), result);
        Assertions.assertThat(Futures.get(future)).isSameAs(result);
        executor.shutdown();
        executor.awaitTermination();
        System.out.println("end"); //SUPPRESS CHECKSTYLE single line
    }

    @Test
    public void testCancel() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newFixedThreadPool("testCancel", 1);
        final Future<?> futureRunning = executor.submit(getWorker(1));
        final Future<?> futureDelayed = executor.submit(getWorker(2));
        TimeUnit.MILLISECONDS.sleep(10);
        Assertions.assertThat(futureDelayed.cancel(false)).isTrue();
        Assertions.assertThat(futureRunning.cancel(true)).isTrue();
        TimeUnit.MILLISECONDS.sleep(100);
        executor.shutdown();
        executor.awaitTermination();
    }

    private Runnable getWorker(final int i) {
        return new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                    System.out.println("end worker " + i); //SUPPRESS CHECKSTYLE single line
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private Runnable getRecursiveWorker(final ExecutorService executor, final int maxRecursion) {
        return new Runnable() {
            @Override
            public void run() {
                final int curRecursion = recursion.incrementAndGet();
                if (curRecursion != maxRecursion) {
                    final Runnable worker = getRecursiveWorker(executor, maxRecursion);
                    executor.execute(worker);
                }
                System.out.println("end worker recursionlevel " + curRecursion); //SUPPRESS CHECKSTYLE single line
            }
        };
    }

}
