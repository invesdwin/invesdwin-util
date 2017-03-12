package de.invesdwin.util.collections.iterable.collection;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@Immutable
public class CollectionCloseableIterable<E> implements ICloseableIterable<E> {

    private final Collection<? extends E> collection;

    public CollectionCloseableIterable(final Collection<? extends E> collection) {
        this.collection = collection;
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new CollectionCloseableIterator<E>(collection);
    }

}
