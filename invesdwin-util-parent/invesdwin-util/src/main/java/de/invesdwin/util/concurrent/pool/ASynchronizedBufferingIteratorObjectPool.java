package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;

@ThreadSafe
public abstract class ASynchronizedBufferingIteratorObjectPool<E> extends ABufferingIteratorObjectPool<E> {

    public ASynchronizedBufferingIteratorObjectPool(final IBufferingIterator<E> bufferingIterator) {
        super(bufferingIterator);
    }

    @Override
    public synchronized E borrowObject() {
        return super.borrowObject();
    }

    @Override
    public synchronized void returnObject(final E element) {
        super.returnObject(element);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

}
