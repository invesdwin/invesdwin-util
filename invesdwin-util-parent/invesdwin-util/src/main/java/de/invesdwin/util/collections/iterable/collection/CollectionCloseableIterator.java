package de.invesdwin.util.collections.iterable.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * By decision not implements IFastToListProvider since we would have to memorize how many elements were skipped
 * already. This does not work too well with HashSets where the order is undefined. Thus just keep the default behavior.
 */
@NotThreadSafe
public class CollectionCloseableIterator<E> implements ICloseableIterator<E> {

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

}
