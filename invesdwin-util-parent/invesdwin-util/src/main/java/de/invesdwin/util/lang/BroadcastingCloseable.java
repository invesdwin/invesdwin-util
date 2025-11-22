package de.invesdwin.util.lang;

import java.io.Closeable;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.IFastIterableSet;

@ThreadSafe
public class BroadcastingCloseable implements Closeable {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(BroadcastingCloseable.class);

    @GuardedBy("this")
    private IFastIterableSet<Closeable> closeables;

    public synchronized boolean registerCloseable(final Closeable closeable) {
        final IFastIterableSet<Closeable> closeables = getOrCreateCloseables();
        return closeables.add(closeable);
    }

    private IFastIterableSet<Closeable> getOrCreateCloseables() {
        if (closeables == null) {
            closeables = ILockCollectionFactory.getInstance(false).newFastIterableLinkedSet();
        }
        return closeables;
    }

    public synchronized boolean unregisterCloseable(final Closeable closeable) {
        final IFastIterableSet<Closeable> closeables = getOrCreateCloseables();
        return closeables.remove(closeable);
    }

    @Override
    public void close() {
        final Closeable[] closeablesArray;
        synchronized (this) {
            if (closeables == null) {
                return;
            }
            closeablesArray = closeables.asArray(Closeables.EMPTY_ARRAY);
        }
        for (int i = 0; i < closeablesArray.length; i++) {
            final Closeable closeable = closeablesArray[i];
            try {
                closeable.close();
            } catch (final Throwable e) {
                LOG.catching(new RuntimeException("Ignoring", e));
            }
        }
        closeables = null;
    }

}
