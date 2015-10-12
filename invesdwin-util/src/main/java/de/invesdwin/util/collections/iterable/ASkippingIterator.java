package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ASkippingIterator<E> extends ACloseableIterator<E> {

    private final ACloseableIterator<E> delegate;
    private E cachedReadNext;

    public ASkippingIterator(final ACloseableIterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected boolean innerHasNext() {
        return readNext() != null;
    }

    @Override
    protected E innerNext() {
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
    protected void innerClose() {
        delegate.close();
    }

}
