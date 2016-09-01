package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface IBufferingIterator<E> extends ICloseableIterator<E>, ICloseableIterable<E> {

    boolean isEmpty();

    E getHead();

    E getTail();

    void add(E element);

    void addAll(Iterable<? extends E> iterable);

    void addAll(Iterator<? extends E> iterator);

    void clear();

    int size();

}
