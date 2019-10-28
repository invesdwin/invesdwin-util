package de.invesdwin.util.lang.finalizer.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.finalizer.FinalizerManager;
import de.invesdwin.util.lang.finalizer.IFinalizerReference;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;
import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * Adapted from io.netty.util.internal.ObjectCleaner
 */
@NotThreadSafe
public class FallbackFinalizerManagerProvider implements IFinalizerManagerProvider {

    private static final int REFERENCE_QUEUE_POLL_TIMEOUT_MS = Duration.ONE_SECOND.intValue(FTimeUnit.MILLISECONDS);

    // This will hold a reference to the AutomaticCleanerReference which will be removed once we called cleanup()
    private static final Set<AutomaticCleanerReference> CLEANERS = ILockCollectionFactory.getInstance(true)
            .newConcurrentSet();
    private static final ReferenceQueue<Object> REFERENCE_QUEUE = new ReferenceQueue<Object>();
    private static final AtomicBoolean CLEANER_RUNNING = new AtomicBoolean(false);
    private static final Runnable CLEANER_TASK = new Runnable() {
        @Override
        public void run() {
            boolean interrupted = false;
            while (true) {
                // Keep on processing as long as the LIVE_SET is not empty and once it becomes empty
                // See if we can let this thread complete.
                while (!CLEANERS.isEmpty()) {
                    final AutomaticCleanerReference reference;
                    try {
                        reference = (AutomaticCleanerReference) REFERENCE_QUEUE.remove(REFERENCE_QUEUE_POLL_TIMEOUT_MS);
                    } catch (final InterruptedException ex) {
                        // Just consume and move on
                        interrupted = true;
                        continue;
                    }
                    if (reference != null) {
                        try {
                            reference.cleanup();
                        } catch (final Throwable ignored) {
                            // ignore exceptions, and don't log in case the logger throws an exception, blocks, or has
                            // other unexpected side effects.
                        }
                        CLEANERS.remove(reference);
                    }
                }
                CLEANER_RUNNING.set(false);

                // Its important to first access the LIVE_SET and then CLEANER_RUNNING to ensure correct
                // behavior in multi-threaded environments.
                if (CLEANERS.isEmpty() || !CLEANER_RUNNING.compareAndSet(false, true)) {
                    // There was nothing added after we set STARTED to false or some other cleanup Thread
                    // was started already so its safe to let this Thread complete now.
                    break;
                }
            }
            if (interrupted) {
                // As we caught the InterruptedException above we should mark the Thread as interrupted.
                Thread.currentThread().interrupt();
            }
        }
    };

    /**
     * Register the given {@link Object} for which the {@link Runnable} will be executed once there are no references to
     * the object anymore.
     *
     * This should only be used if there are no other ways to execute some cleanup once the Object is not reachable
     * anymore because it is not a cheap way to handle the cleanup.
     */
    @Override
    public IFinalizerReference register(final Object obj, final AFinalizer finalizer) {
        Assertions.checkNotNull(obj);
        Assertions.checkNotNull(finalizer);
        final AutomaticCleanerReference reference = new AutomaticCleanerReference(obj, finalizer);
        // Its important to add the reference to the LIVE_SET before we access CLEANER_RUNNING to ensure correct
        // behavior in multi-threaded environments.
        CLEANERS.add(reference);

        // Check if there is already a cleaner running.
        if (CLEANER_RUNNING.compareAndSet(false, true)) {
            final Thread cleanupThread = new FastThreadLocalThread(CLEANER_TASK);
            cleanupThread.setPriority(Thread.MIN_PRIORITY);
            // Set to null to ensure we not create classloader leaks by holding a strong reference to the inherited
            // classloader.
            // See:
            // - https://github.com/netty/netty/issues/7290
            // - https://bugs.openjdk.java.net/browse/JDK-7008595
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    cleanupThread.setContextClassLoader(null);
                    return null;
                }
            });
            cleanupThread.setName(FinalizerManager.class.getSimpleName());

            // Mark this as a daemon thread to ensure that we the JVM can exit if this is the only thread that is
            // running.
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }

        return new FinalizerReference(reference);
    }

    private static final class AutomaticCleanerReference extends PhantomReference<Object> {
        private final Runnable cleanupTask;

        AutomaticCleanerReference(final Object referent, final Runnable cleanupTask) {
            super(referent, REFERENCE_QUEUE);
            this.cleanupTask = cleanupTask;
        }

        void cleanup() {
            cleanupTask.run();
        }

        @Override
        public Thread get() {
            return null;
        }

        @Override
        public void clear() {
            CLEANERS.remove(this);
            super.clear();
        }
    }

    public static final class FinalizerReference implements IFinalizerReference {

        private AutomaticCleanerReference reference;

        public FinalizerReference(final AutomaticCleanerReference ref) {
            Assertions.checkNotNull(ref);
            this.reference = ref;
        }

        @Override
        public synchronized void cleanReference() {
            if (reference != null) {
                reference.clear();
                reference = null;
            }
        }

    }

}
