package de.invesdwin.util.collections.iterable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlatteningIterator<E> extends ACloseableIterator<E> {

    private final ICloseableIterator<? extends Iterator<? extends E>> delegate;
    private ICloseableIterator<? extends E> curIterator;

    public FlatteningIterator(final ICloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected boolean innerHasNext() {
        try {
            return delegate.hasNext() || getIterator().hasNext();
        } catch (final NoSuchElementException e) {
            return false;
        }
    }

    @Override
    protected E innerNext() {
        return getIterator().next();
    }

    private ICloseableIterator<? extends E> getIterator() {
        while (curIterator == null || !curIterator.hasNext()) {
            curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        return curIterator;
    }

    @Override
    protected void innerClose() {
        if (curIterator != null) {
            curIterator.close();
        }
        delegate.close();
    }

}
