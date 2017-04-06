package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface IBufferingIterator<E> extends ICloseableIterator<E>, ICloseableIterable<E> {

    boolean isEmpty();

    E getHead();

    E getTail();

    /**
     * Prepends the element at head of the list
     */
    void prepend(E element);

    /**
     * Adds the element at tail of the list
     */
    void add(E element);

    boolean addAll(Iterator<? extends E> iterator);

    boolean addAll(Iterable<? extends E> iterable);

    boolean addAll(BufferingIterator<E> iterable);

    void clear();

    int size();

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    boolean consume(Iterable<? extends E> iterable);

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    boolean consume(Iterator<? extends E> iterator);

    /**
     * If given a BufferingIterator, it will get added to this one and emptied. This is a lot faster than using addAll.
     */
    boolean consume(BufferingIterator<E> iterator);

}
