package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface IBufferingIterator<E> extends ICloseableIterator<E>, ICloseableIterable<E> {

    boolean isEmpty();

    E getHead();

    E getTail();

    void add(E element);

    void addAll(Iterator<? extends E> iterator);

    void addAll(Iterable<? extends E> iterable);

    void addAll(BufferingIterator<E> iterable);

    void clear();

    int size();

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    void consume(Iterable<? extends E> iterable);

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    void consume(Iterator<? extends E> iterator);

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    void consume(BufferingIterator<E> iterator);

}
