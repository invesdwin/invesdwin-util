package de.invesdwin.util.concurrent.pool;

import java.util.NoSuchElementException;

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
        try {
            final E element = bufferingIterator.next();
            if (element != null) {
                return element;
            } else {
                return newObject();
            }
        } catch (final NoSuchElementException e) {
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
    public void invalidateObject(final E element) {
        //noop
    }

    @Override
    public void clear() {
        bufferingIterator.clear();
    }

}
