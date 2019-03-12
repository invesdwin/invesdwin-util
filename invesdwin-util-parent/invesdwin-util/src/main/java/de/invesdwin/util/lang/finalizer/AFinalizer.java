package de.invesdwin.util.lang.finalizer;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public abstract class AFinalizer implements Closeable, Runnable {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AFinalizer.class);

    private IFinalizerReference finalizerReference;

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
            if (finalizerReference != null) {
                finalizerReference.cleanReference();
                finalizerReference = null;
            }
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

    public static AFinalizer valueOfCloseable(final Closeable closeable) {
        return new AFinalizer() {

            private Closeable delegate = closeable;

            @Override
            protected void clean() {
                try {
                    delegate.close();
                    delegate = null;
                } catch (final IOException e) {
                    new RuntimeException(e);
                }
            }

            @Override
            public boolean isClosed() {
                return delegate == null;
            }
        };
    }

    public static AFinalizer valueOfRunnable(final Runnable runnable) {
        return new AFinalizer() {
            private Runnable delegate = runnable;

            @Override
            protected void clean() {
                delegate.run();
                delegate = null;
            }

            @Override
            public boolean isClosed() {
                return delegate == null;
            }
        };
    }

    public static AFinalizer valueOfCombined(final AFinalizer... finalizers) {
        return new AFinalizer() {

            private AFinalizer[] delegates = finalizers;

            @Override
            protected void clean() {
                delegates = null;
            }

            @Override
            protected void onRun() {
                for (int i = 0; i < delegates.length; i++) {
                    delegates[i].run();
                }
            }

            @Override
            protected void onClose() {
                for (int i = 0; i < delegates.length; i++) {
                    delegates[i].close();
                }
            }

            @Override
            public boolean isClosed() {
                return delegates == null;
            }
        };
    }

    public void register(final Object obj) {
        Assertions.checkNull(finalizerReference);
        this.finalizerReference = FinalizerManager.register(obj, this);
    }

    public IFinalizerReference getFinalizerReference() {
        return finalizerReference;
    }

    public void setFinalizerReference(final IFinalizerReference cleanableReference) {
        this.finalizerReference = cleanableReference;
    }

}
