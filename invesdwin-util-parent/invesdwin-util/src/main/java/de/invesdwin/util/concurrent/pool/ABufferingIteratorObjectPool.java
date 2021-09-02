package de.invesdwin.util.concurrent.pool;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.IBufferingIterator;

@NotThreadSafe
public abstract class ABufferingIteratorObjectPool<E> implements IObjectPool<E> {

    protected final IBufferingIterator<E> bufferingIterator;

    public ABufferingIteratorObjectPool(final IBufferingIterator<E> bufferingIterator) {
        this.bufferingIterator = bufferingIterator;
    }

    @Override
    public E borrowObject() {
        final E element = bufferingIterator.next();
        if (element != null) {
            return element;
        } else {
            return newObject();
        }
    }

    protected abstract E newObject();

    @Override
    public void returnObject(final E element) {
        if (element == null) {
            return;
        }
        bufferingIterator.add(element);
    }

    @Override
    public void clear() {
        bufferingIterator.clear();
    }

}
