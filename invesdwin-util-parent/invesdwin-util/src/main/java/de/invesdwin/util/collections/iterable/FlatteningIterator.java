package de.invesdwin.util.collections.iterable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class FlatteningIterator<E> implements ICloseableIterator<E> {

    private final ICloseableIterator<? extends Iterator<? extends E>> delegate;
    private ICloseableIterator<? extends E> curIterator;

    @SafeVarargs
    public FlatteningIterator(final Iterator<? extends E>... delegate) {
        this.delegate = WrapperCloseableIterable.maybeWrap(delegate).iterator();
    }

    public FlatteningIterator(final ICloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasNext() {
        if (curIterator == null && delegate.hasNext()) {
            curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        while (curIterator != null) {
            if (curIterator.hasNext()) {
                return true;
            } else if (delegate.hasNext()) {
                try {
                    nextIterator();
                } catch (final NoSuchElementException e) {
                    //might happen on null value
                    return false;
                }
            } else {
                break;
            }
        }
        return false;
    }

    private void nextIterator() {
        if (curIterator != null) {
            curIterator.close();
            curIterator = null;
        }
        //might throw another final NoSuchElement exception
        curIterator = delegateNext();
    }

    @Override
    public E next() {
        if (curIterator == null) {
            //might throw a NoSuchElement exception
            curIterator = delegateNext();
            if (curIterator == null) {
                throw new NullPointerException();
            }
        }
        while (curIterator != null) {
            try {
                final E next = curIterator.next();
                if (next == null) {
                    throw new NullPointerException("FlatteningIterator: next() curIterator.next() returned null");
                }
                return next;
            } catch (final NoSuchElementException e) {
                nextIterator();
            }
        }
        throw FastNoSuchElementException.getInstance("FlatteningIterator: curIterator is null");
    }

    @SuppressWarnings("deprecation")
    private ICloseableIterator<? extends E> delegateNext() {
        final Iterator<? extends E> delegateNext = delegate.next();
        if (delegateNext == null) {
            throw new NullPointerException("FlatteningIterator: delegateNext() delegate.next() returned null");
        }
        return WrapperCloseableIterator.maybeWrap(delegateNext);
    }

    @Override
    public void close() {
        /*
         * WARNING: not closing the rest of the iterators, since this could lead to additional data being loaded by
         * ATimeSeriesDB because further iterators are created lazy. Instead the delegate has to decide if it should
         * close the iterators it has available next. In most cases it is fine and faster to just discard further
         * iterators.
         */
        //        while (curIterator != null) {
        //            curIterator.close();
        //            if (delegate.hasNext()) {
        //                curIterator = WrapperCloseableIterator.maybeWrap(delegate.next());
        //            } else {
        //                curIterator = null;
        //            }
        //        }
        if (curIterator != null) {
            curIterator.close();
            curIterator = null;
        }
        delegate.close();
    }

}
