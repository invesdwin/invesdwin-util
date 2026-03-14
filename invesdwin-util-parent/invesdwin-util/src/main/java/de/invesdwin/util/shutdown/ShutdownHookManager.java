package de.invesdwin.util.shutdown;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.error.InterruptedRuntimeException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.duration.Duration;

/**
 * Registers an internal Thread as a ShutdownHook in the JVM that runs the given callback on shutdown.
 * 
 * @author subes
 */
@ThreadSafe
public final class ShutdownHookManager {

    public static final ShutdownHookManager INSTANCE = new ShutdownHookManager();
    @GuardedBy("INSTANCE")
    private static final Map<Integer, ShutdownHookThread> REGISTERED_HOOKS = ILockCollectionFactory.getInstance(false)
            .newLinkedMap();
    private static volatile boolean shuttingDown;
    private static final LoopInterruptedCheck REMOVE_OBSOLETE_THREADS_CHECK = new LoopInterruptedCheck(
            Duration.ONE_MINUTE) {
        @Override
        protected boolean onInterval() {
            return true;
        }
    };
    private static final AtomicInteger SHUTDOWN_HOOK_THREAD_COUNT = new AtomicInteger();
    private static final AtomicInteger SYSTEM_EXIT_ASYNC_COUNT = new AtomicInteger();
    private static volatile int asyncExitCode = -1;

    static {
        /**
         * At least one ShutdownHook is needed for the flag to be working.
         */
        register(new IShutdownHook() {
            @Override
            public void shutdown() throws Exception {}
        });
    }

    private ShutdownHookManager() {}

    /**
     * Instead of using the linked hack, we use the ShutdownHookManager. The first started ShutdownHook sets this flag.
     * There is always at least one ShutdownHook registered in the JVM.
     * 
     * @see <a href="http://www.seropian.eu/2009/10/how-to-know-when-java-virtual-machine.html#answer">Hacky
     *      alternative</a>
     */
    public static boolean isShuttingDown() {
        return shuttingDown;
    }

    public static void register(final IShutdownHook hook) {
        synchronized (INSTANCE) {
            if (isShuttingDown()) {
                //too late
                return;
            }
            if (REMOVE_OBSOLETE_THREADS_CHECK.checkClockNoInterrupt()) {
                removeObsoleteThreads();
            }
            final int identityHashCode = System.identityHashCode(hook);
            final ShutdownHookThread newThread = new ShutdownHookThread(identityHashCode, hook);
            final ShutdownHookThread existing = REGISTERED_HOOKS.put(identityHashCode, newThread);
            if (existing != null) {
                //identity might get recycled
                if (existing.isObsolete()) {
                    Runtime.getRuntime().removeShutdownHook(existing);
                } else {
                    Assertions.assertThat(existing).as("Hook [%s] has already been registered!", hook).isNull();
                }
            }
            Runtime.getRuntime().addShutdownHook(newThread);
        }
    }

    public static boolean registerNoThrow(final IShutdownHook hook) {
        synchronized (INSTANCE) {
            if (isShuttingDown()) {
                //too late
                return false;
            }
            if (REMOVE_OBSOLETE_THREADS_CHECK.checkClockNoInterrupt()) {
                removeObsoleteThreads();
            }
            final int identityHashCode = System.identityHashCode(hook);
            final ShutdownHookThread newThread = new ShutdownHookThread(identityHashCode, hook);
            final ShutdownHookThread existing = REGISTERED_HOOKS.put(identityHashCode, newThread);
            if (existing != null) {
                //identity might get recycled
                Runtime.getRuntime().removeShutdownHook(existing);
            }
            Runtime.getRuntime().addShutdownHook(newThread);
            return existing != null;
        }
    }

    private static void removeObsoleteThreads() {
        final Iterator<Entry<Integer, ShutdownHookThread>> it = REGISTERED_HOOKS.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<Integer, ShutdownHookThread> entry = it.next();
            final ShutdownHookThread thread = entry.getValue();
            if (thread.isObsolete()) {
                Runtime.getRuntime().removeShutdownHook(thread);
                it.remove();
            }
        }
    }

    public static void unregister(final IShutdownHook hook) {
        synchronized (INSTANCE) {
            if (isShuttingDown()) {
                //too late
                return;
            }
            final int identityHashCode = System.identityHashCode(hook);
            final ShutdownHookThread removedThread = REGISTERED_HOOKS.remove(identityHashCode);
            Assertions.assertThat(shuttingDown || removedThread != null)
                    .as("Hook [%s] was never registered!", hook)
                    .isTrue();
            Assertions.assertThat(Runtime.getRuntime().removeShutdownHook(removedThread)).isTrue();
        }
    }

    /**
     * Is threadsafe via encapsulation.
     * 
     * @author subes
     */
    @ThreadSafe
    private static class ShutdownHookThread extends Thread {
        private final WeakReference<IShutdownHook> shutdownable;
        private final int identityHashCode;

        ShutdownHookThread(final int identityHashCode, final IShutdownHook shutdownable) {
            super(ShutdownHookManager.class.getSimpleName() + "." + ShutdownHookThread.class.getSimpleName() + "."
                    + SHUTDOWN_HOOK_THREAD_COUNT.incrementAndGet());
            this.identityHashCode = identityHashCode;
            this.shutdownable = new WeakReference<IShutdownHook>(shutdownable);
        }

        public boolean isObsolete() {
            return shutdownable.get() == null;
        }

        @Override
        public void run() {
            shuttingDown = true;
            try {
                final IShutdownHook hook = shutdownable.get();
                if (hook != null) {
                    hook.shutdown();
                }
            } catch (final Exception e) {
                getUncaughtExceptionHandler().uncaughtException(this, e);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(ShutdownHookThread.class, identityHashCode);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ShutdownHookThread) {
                final ShutdownHookThread cObj = (ShutdownHookThread) obj;
                return cObj.identityHashCode == identityHashCode;
            } else {
                return false;
            }
        }
    }

    /**
     * This method can be called to prevent deadlocks by allowing the outer thread to unwind the call stack due to an
     * InterruptedException that will not be retried, thus unlocking locks. Blocking on System.exit will happen in an
     * asynchronous thread which prevents deadlocks in asynchronous shutdown hooks that might try to lock the same locks
     * that the caller thread had.
     */
    public static InterruptedRuntimeException systemExitAsync(final int exitCode) {
        final int callCount = SYSTEM_EXIT_ASYNC_COUNT.incrementAndGet();
        if (callCount == 1) {
            asyncExitCode = exitCode;
            final String threadName = newSystemExitAsyncThreadName(exitCode);
            new Thread(threadName) {
                @Override
                public void run() {
                    System.exit(asyncExitCode);
                }
            }.start();
            return new InterruptedRuntimeException("Initially calling System.exit(" + exitCode
                    + ") asynchronously in thread: " + newSystemExitAsyncThreadName(exitCode));
        } else {
            return new InterruptedRuntimeException("Already calling System.exit(" + exitCode
                    + ") asynchronously in thread: " + newSystemExitAsyncThreadName(asyncExitCode));
        }
    }

    private static String newSystemExitAsyncThreadName(final int exitCode) {
        return ShutdownHookManager.class.getSimpleName() + ".systemExitAsync(" + exitCode + ")";
    }

}
