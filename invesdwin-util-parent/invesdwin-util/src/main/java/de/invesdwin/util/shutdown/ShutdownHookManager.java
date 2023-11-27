package de.invesdwin.util.shutdown;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Objects;

/**
 * Registers an internal Thread as a ShutdownHook in the JVM that runs the given callback on shutdown.
 * 
 * @author subes
 */
@ThreadSafe
public final class ShutdownHookManager {

    public static final ShutdownHookManager INSTANCE = new ShutdownHookManager();
    @GuardedBy("INSTANCE")
    private static final Map<Integer, ShutdownHookThread> REGISTERED_HOOKS = new HashMap<Integer, ShutdownHookThread>();
    private static volatile boolean shuttingDown;

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
            final int identityHashCode = System.identityHashCode(hook);
            final ShutdownHookThread thread = new ShutdownHookThread(identityHashCode, hook);
            Assertions.assertThat(REGISTERED_HOOKS.put(identityHashCode, thread))
                    .as("Hook [%s] has already been registered!", hook)
                    .isNull();
            Runtime.getRuntime().addShutdownHook(thread);
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
            this.identityHashCode = identityHashCode;
            this.shutdownable = new WeakReference<IShutdownHook>(shutdownable);
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

}
