package de.invesdwin.util.collections.iterable.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.collection.fast.IFastToListCloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * By decision not implements IFastToListProvider since we would have to memorize how many elements were skipped
 * already. This does not work too well with HashSets where the order is undefined. Thus just keep the default behavior.
 */
@NotThreadSafe
public class CollectionCloseableIterator<E> implements IFastToListCloseableIterator<E> {

    private Iterator<? extends E> delegate;

    public CollectionCloseableIterator(final Collection<? extends E> collection) {
        this.delegate = collection.iterator();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        try {
            return delegate.next();
        } catch (final NoSuchElementException e) {
            close();
            throw FastNoSuchElementException.maybeReplace(e, "CollectionCloseableIterator: next threw");
        }
    }

    @Override
    public void close() {
        delegate = EmptyCloseableIterator.getInstance();
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
        throw new UnsupportedOperationException("do this from the iterable");
    }

    @Override
    public E getTail() {
        throw new UnsupportedOperationException("too slow on collections");
    }

}
