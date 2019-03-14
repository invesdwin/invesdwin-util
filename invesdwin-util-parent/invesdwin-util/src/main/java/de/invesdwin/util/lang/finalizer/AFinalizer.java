package de.invesdwin.util.lang.finalizer;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

/**
 * Best practices:
 * 
 * <ul>
 * <li>Create a static inner class for the finalizer (to prevent reference leak from the outer class) named as the outer
 * class with a Finalizer suffix (to see what class causes a memory leak due to reference leaks of the outer class in
 * the heap dump)</li>
 * <li>The clean() method should contain your code that should be executed both on close() and on finalization via
 * run(). You can add specific functionality for both cases by overriding the onClose() and onRun() methods. If
 * isClosed() returns true, the clean method will not be invoked. The IFinalizerReference that was registered() will
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

    private IFinalizerReference reference;

    /**
     * the actual action
     */
    protected abstract void clean();

    /**
     * This method can be used to clean normally from the outside.
     */
    @Override
    public final void close() {
        if (!isClosed()) {
            onClose();
            clean();
        }
        //clean reference even if already closed (isClosed() might be implemented wrong)
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

    protected void onClose() {}

    /**
     * For internal use only. This method will get called when the reference had to be cleaned up because it was not
     * closed beforehand.
     */
    @Deprecated
    @Override
    public final void run() {
        try {
            if (!isClosed()) {
                onRun();
                clean();
            }
        } catch (final Throwable t) {
            //don't propagate exceptions here or else the reference handling might eat the exception and worst case stop itself
            LOG.error("Exception in finalizer [%s]: %s", getClass().getName(), Throwables.getFullStackTrace(t));
        }
    }

    public abstract boolean isClosed();

    protected void onRun() {}

    /**
     * If already registered, this method does nothing
     */
    public void register(final Object obj) {
        if (this.reference == null) {
            this.reference = FinalizerManager.register(obj, this);
        }
    }

    public IFinalizerReference getReference() {
        return reference;
    }

    public void setReference(final IFinalizerReference reference) {
        this.reference = reference;
    }

}
