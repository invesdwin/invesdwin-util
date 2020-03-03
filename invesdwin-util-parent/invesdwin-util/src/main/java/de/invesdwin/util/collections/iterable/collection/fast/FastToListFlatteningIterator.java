package de.invesdwin.util.collections.iterable.collection.fast;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class FastToListFlatteningIterator<E> implements IFastToListCloseableIterator<E> {

    private final IFastToListCloseableIterator<? extends Iterator<? extends E>> delegate;
    private IFastToListCloseableIterator<E> curIterator;

    @SafeVarargs
    public FastToListFlatteningIterator(final IFastToListCloseableIterator<? extends E>... delegate) {
        this.delegate = (IFastToListCloseableIterator<? extends Iterator<? extends E>>) WrapperCloseableIterable
                .maybeWrap(delegate)
                .iterator();
    }

    public FastToListFlatteningIterator(final IFastToListCloseableIterator<? extends Iterator<? extends E>> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasNext() {
        if (curIterator == null && delegate.hasNext()) {
            curIterator = (IFastToListCloseableIterator<E>) WrapperCloseableIterator.maybeWrap(delegate.next());
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

    @SuppressWarnings("deprecation")
    private void nextIterator() {
        curIterator.close();
        curIterator = null;
        //might throw another final NoSuchElement exception
        curIterator = (IFastToListCloseableIterator<E>) WrapperCloseableIterator.maybeWrap(delegate.next());
    }

    @SuppressWarnings("deprecation")
    @Override
    public E next() {
        if (curIterator == null) {
            //might throw a NoSuchElement exception
            curIterator = (IFastToListCloseableIterator<E>) WrapperCloseableIterator.maybeWrap(delegate.next());
        }
        while (curIterator != null) {
            try {
                return curIterator.next();
            } catch (final NoSuchElementException e) {
                nextIterator();
            }
        }
        throw new FastNoSuchElementException("FlatteningIterator: curIterator is null");
    }

    @Override
    public void close() {
        if (curIterator != null) {
            curIterator.close();
        }
        delegate.close();
    }

    @Override
    public List<E> toList() {
        throw new UnsupportedOperationException("do this from the iterable");
    }

    @Override
    public List<E> toList(final List<E> list) {
        throw new UnsupportedOperationException("do this from the iterable");
    }

    @Override
    public E getHead() {
        if (!hasNext()) {
            return null;
        }
        return curIterator.getHead();
    }

    @Override
    public E getTail() {
        if (!hasNext()) {
            return null;
        }
        final IFastToListCloseableIterator<E> tailIterator;
        final Iterator<E> tailIteratorUnwrapped = (Iterator<E>) delegate.getTail();
        if (tailIteratorUnwrapped != null) {
            tailIterator = (IFastToListCloseableIterator<E>) WrapperCloseableIterator.maybeWrap(tailIteratorUnwrapped);
        } else {
            tailIterator = curIterator;
        }
        return tailIterator.getTail();
    }

}
