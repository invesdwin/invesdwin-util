package de.invesdwin.util.lang.finalizer;

import java.io.Closeable;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.Booleans;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Best practices:
 * 
 * <ul>
 * <li>Create a static inner class for the finalizer (to prevent reference leak from the outer class) named as the outer
 * class with a Finalizer suffix (to see what class causes a memory leak due to reference leaks of the outer class in
 * the heap dump)</li>
 * <li>The clean() method should contain your code that should be executed both on close() and on finalization via
 * run(). You can add specific functionality for both cases by overriding the onClose() and onRun() methods. If
 * isCleaned() returns true, the clean method will not be invoked. The IFinalizerReference that was registered() will
 * still be removed.</li>
 * <li>Use only fields in the finalizer that don't introduce reference leaks of the outer class.</li>
 * <li>Don't lock/unlock anything in the finalizer, this might cause a deadlock during finalization.</li>
 * <li>Only finalize manually actual native resources (like files, handles), since heap references will be cleaned up
 * anyway during finalization.</li>
 * <li>Call close() from the outside to invoke the clean method and to unregister this instance from the
 * FinalizerManager.</li>
 * <li>If a finalizer should be reopened after having been closed, just call register again.</li>
 * <li>The clean() method will be called via the run method during finalization, you could add some warnings about
 * unclosed resources in your onRun() method override.</li>
 * <li>Use Throwables.setDebugStackTraceEnabled(true) to get exception stacktraces for reference leaks that would
 * prevent garbage collection and thus finalization. For this to work properly, make sure to call register() as late as
 * possible after all fields in the finalizer have been set.</li>
 * </ul>
 * 
 * @author subes
 *
 */
@NotThreadSafe
public abstract class AFinalizer implements Closeable, Runnable {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AFinalizer.class);
    private static final FastThreadLocal<Boolean> THREAD_FINALIZER_ACTIVE = new FastThreadLocal<>();
    private static final FastThreadLocal<Boolean> UNREGISTERING = new FastThreadLocal<>();

    @GuardedBy("this")
    private IFinalizerReference reference;

    /**
     * the actual action
     */
    protected abstract void clean();

    /**
     * This method can be used to clean normally from the outside.
     */
    @Override
    public final synchronized void close() {
        if (!isCleaned()) {
            final boolean registerThreadFinalizerActive = registerThreadFinalizerActive();
            try {
                onClose();
                onClean();
                clean();
            } finally {
                unregisterThreadFinalizerActive(registerThreadFinalizerActive);
            }
        }
        //clean reference even if already closed (isCleaned() might be implemented wrong)
        cleanReference();
    }

    /**
     * Can be overridden to disable reference cleanup.
     */
    protected void cleanReference() {
        if (reference != null) {
            reference.cleanReference();
            reference = null;
        }
    }

    /**
     * Invoked when the finalizer is closed by application code or finalized by a cleaner.
     */
    protected void onClean() {}

    /**
     * Invoked when the finalizer is closed by application code.
     */
    protected void onClose() {}

    /**
     * Invoked when the finalizer is finalized by a cleaner.
     */
    protected void onFinalize() {}

    /**
     * For internal use only. This method will get called when the reference had to be cleaned up because it was not
     * closed beforehand.
     */
    @Deprecated
    @Override
    public final synchronized void run() {
        try {
            if (!isCleaned() && Booleans.isNotTrue(UNREGISTERING.get())) {
                onFinalize();
                onClean();
                clean();
            }
        } catch (final Throwable t) {
            //don't propagate exceptions here or else the reference handling might eat the exception and worst case stop itself
            //CHECKSTYLE:OFF
            LOG.error("Exception in finalizer [{}]: {}", getClass().getName(), Throwables.getFullStackTrace(t));
            //CHECKSTYLE:ON
        }
    }

    public final synchronized boolean isClosed() {
        return isCleaned();
    }

    protected abstract boolean isCleaned();

    /**
     * If already registered, this method does nothing
     */
    public synchronized void register(final Object obj) {
        if (this.reference == null) {
            this.reference = FinalizerManager.register(obj, this);
        }
    }

    /**
     * If not already registered, this method does nothing
     */
    public synchronized void unregister() {
        if (this.reference == null || Booleans.isTrue(UNREGISTERING.get())) {
            return;
        }
        UNREGISTERING.set(true);
        try {
            reference.cleanReference();
            reference = null;
        } finally {
            UNREGISTERING.remove();
        }
    }

    public synchronized boolean isRegistered() {
        return reference != null;
    }

    /**
     * Return true if this finalizer should be cleaned when the current thread context is left. This is useful to keep
     * files/iterators as open as short as possible and to be able to release locks while still in the same thread.
     * Otherwise finalization would fail. A cleanup can still happen earlier.
     */
    public abstract boolean isThreadLocal();

    public static boolean isThreadFinalizerActive() {
        return Booleans.isTrue(THREAD_FINALIZER_ACTIVE.get());
    }

    public static boolean registerThreadFinalizerActive() {
        final boolean threadFinalizerActiveBefore = BooleanUtils.isTrue(THREAD_FINALIZER_ACTIVE.get());
        if (!threadFinalizerActiveBefore) {
            THREAD_FINALIZER_ACTIVE.set(true);
            return true;
        } else {
            return false;
        }
    }

    public static void unregisterThreadFinalizerActive(final boolean registerThreadFinalizerActive) {
        if (registerThreadFinalizerActive) {
            THREAD_FINALIZER_ACTIVE.remove();
        }
    }

}
