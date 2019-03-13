package de.invesdwin.util.lang.finalizer;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;

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
