package de.invesdwin.util.collections.iterable.skip;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.IPeekingCloseableIterator;
import de.invesdwin.util.collections.iterable.PeekingCloseableIterator;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public abstract class ASkipDuplicateKeysIterator<E> implements ICloseableIterator<E> {

    public static final boolean DEFAULT_KEEP_LAST_DUPLICATE = true;
    private final IPeekingCloseableIterator<E> delegate;

    public ASkipDuplicateKeysIterator(final ICloseableIterator<? extends E> delegate) {
        this.delegate = new PeekingCloseableIterator<>(delegate);
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        E peekValue = delegate.peek();
        E value = peekValue;
        try {
            while (extractEndTime(peekValue).equalsNotNullSafe(extractEndTime(value))) {
                if (isKeepLastDuplicate()) {
                    value = peekValue;
                }
                //skip duplicate
                Assertions.checkSame(peekValue, delegate.next());
                peekValue = delegate.peek();
            }
        } catch (final Throwable t) {
            //end reached
        }
        return value;
    }

    protected boolean isKeepLastDuplicate() {
        return DEFAULT_KEEP_LAST_DUPLICATE;
    }

    protected abstract FDate extractEndTime(E value);

    @Override
    public void close() {
        delegate.close();
    }

}
