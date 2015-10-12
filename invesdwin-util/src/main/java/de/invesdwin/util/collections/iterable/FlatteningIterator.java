package de.invesdwin.util.collections.iterable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlatteningIterator<E> extends ACloseableIterator<E> {

    private final ACloseableIterator<? extends Iterator<? extends E>> delegate;
    private ACloseableIterator<? extends E> curIterator;

    public FlatteningIterator(final ACloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected boolean innerHasNext() {
        try {
            return getIterator().hasNext() || delegate.hasNext();
        } catch (final NoSuchElementException e) {
            return false;
        }
    }

    @Override
    protected E innerNext() {
        return getIterator().next();
    }

    private ACloseableIterator<? extends E> getIterator() {
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
