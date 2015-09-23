package de.invesdwin.util.concurrent;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;

@ThreadSafe
public class ConfiguredForkJoinPool extends ForkJoinPool {

    private final IShutdownHook shutdownHook = new IShutdownHook() {
        @Override
        public void shutdown() throws Exception {
            shutdownNow();
        }
    };

    public ConfiguredForkJoinPool(final String name, final int parallelism, final boolean asyncMode) {
        super(parallelism, new ConfiguredForkJoinWorkerThreadFactory(name),
                Thread.getDefaultUncaughtExceptionHandler(), false);
        configure();
    }

    private void configure() {
        /*
         * All executors should be shutdown on application shutdown.
         */
        ShutdownHookManager.register(shutdownHook);
    }

    private void unconfigure() {
        if (!isShutdown()) {
            ShutdownHookManager.unregister(shutdownHook);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        unconfigure();
    }

    @Override
    public List<Runnable> shutdownNow() {
        final List<Runnable> l = super.shutdownNow();
        unconfigure();
        return l;
    }

    public boolean awaitTermination() throws InterruptedException {
        return awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public boolean awaitQuiescence() {
        return awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
    }

}
