package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ASkippingIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<E> delegate;
    private E cachedReadNext;

    public ASkippingIterator(final ICloseableIterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return readNext() != null;
    }

    @Override
    public E next() {
        final E readNext = readNext();
        cachedReadNext = null;
        if (readNext == null) {
            throw new NoSuchElementException();
        }
        return readNext;
    }

    @SuppressWarnings("null")
    private E readNext() {
        if (cachedReadNext != null) {
            return cachedReadNext;
        } else {
            try {
                while (delegate.hasNext()) {
                    final E next = delegate.next();
                    if (!skip(next)) {
                        cachedReadNext = next;
                        break;
                    }
                }
                //catching nosuchelement might be faster sometimes than checking hasNext(), e.g. for LevelDB
            } catch (final NoSuchElementException e) {
                close();
                return null;
            }
            return cachedReadNext;
        }
    }

    protected abstract boolean skip(E element);

    @Override
    public void close() {
        delegate.close();
    }

}
