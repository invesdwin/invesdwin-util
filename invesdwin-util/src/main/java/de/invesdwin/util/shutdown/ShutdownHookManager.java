package de.invesdwin.util.shutdown;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;

/**
 * Registers an internal Thread as a ShutdownHook in the JVM that runs the given callback on shutdown.
 * 
 * @author subes
 */
@ThreadSafe
public final class ShutdownHookManager {

    public static final ShutdownHookManager INSTANCE = new ShutdownHookManager();
    @GuardedBy("INSTANCE")
    private static final Map<IShutdownHook, ShutdownHookThread> REGISTERED_HOOKS = new HashMap<IShutdownHook, ShutdownHookThread>();
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
            final ShutdownHookThread thread = new ShutdownHookThread(hook);
            Assertions.assertThat(REGISTERED_HOOKS.put(hook, thread))
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
            final ShutdownHookThread removedThread = REGISTERED_HOOKS.remove(hook);
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
        private final IShutdownHook shutdownable;

        ShutdownHookThread(final IShutdownHook shutdownable) {
            this.shutdownable = shutdownable;
        }

        @Override
        public void run() {
            shuttingDown = true;
            try {
                shutdownable.shutdown();
            } catch (final Exception e) {
                getUncaughtExceptionHandler().uncaughtException(this, e);
            }
        }
    }

}
