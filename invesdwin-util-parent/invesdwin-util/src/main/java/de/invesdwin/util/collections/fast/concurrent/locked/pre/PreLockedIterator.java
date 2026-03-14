package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.streams.closeable.Closeables;

@ThreadSafe
public class PreLockedIterator<E> extends ACloseableIterator<E> {

    private ILock lock;
    private Iterator<E> delegate;

    public PreLockedIterator(final TextDescription name, final Iterator<E> delegate, final ILock lock) {
        super(name);
        this.delegate = delegate;
        this.lock = lock;
    }

    @Override
    protected boolean innerHasNext() {
        return delegate.hasNext();
    }

    @Override
    protected E innerNext() {
        return delegate.next();
    }

    @Override
    protected void innerClose() {
        if (lock != null) {
            Closeables.closeQuietly(delegate);
            delegate = EmptyCloseableIterator.getInstance();
            lock.unlock();
            lock = null;
        }
    }

}
