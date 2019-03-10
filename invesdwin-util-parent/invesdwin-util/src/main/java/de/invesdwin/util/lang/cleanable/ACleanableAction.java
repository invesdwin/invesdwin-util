package de.invesdwin.util.lang.cleanable;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public abstract class ACleanableAction implements Closeable, Runnable {

    private ICleanableReference cleanableReference;

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
            synchronized (this) {
                if (!isClosed()) {
                    onClose();
                    clean();
                    if (cleanableReference != null) {
                        cleanableReference.cleanReference();
                        cleanableReference = null;
                    }
                }
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
        if (!isClosed()) {
            synchronized (this) {
                if (!isClosed()) {
                    onRun();
                    clean();
                }
            }
        }
    }

    public abstract boolean isClosed();

    protected void onRun() {}

    public static ACleanableAction valueOfCloseable(final Closeable closeable) {
        return new ACleanableAction() {

            private volatile Closeable delegate = closeable;

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

    public static ACleanableAction valueOfRunnable(final Runnable runnable) {
        return new ACleanableAction() {
            private volatile Runnable delegate = runnable;

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

    public static ACleanableAction valueOfCombined(final ACleanableAction... cleanableActions) {
        return new ACleanableAction() {

            private volatile ACleanableAction[] delegates = cleanableActions;

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
        Assertions.checkNull(cleanableReference);
        this.cleanableReference = CleanableManager.register(obj, this);
    }

    public ICleanableReference getCleanableReference() {
        return cleanableReference;
    }

    public void setCleanableReference(final ICleanableReference cleanableReference) {
        this.cleanableReference = cleanableReference;
    }

}
