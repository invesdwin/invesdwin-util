package de.invesdwin.util.collections.array.large.queue;

import java.util.Collection;
import java.util.NoSuchElementException;

import de.invesdwin.util.error.FastNoSuchElementException;

public interface IByteLargeQueue {

    /**
     * Unlike a double there is no NaN for a byte, so all 256 values are valid elements. Thus {@link #MISSING_VALUE} can
     * only be used as a best effort signal, an empty queue should be detected with {@link #isEmpty()} instead.
     */
    byte MISSING_VALUE = (byte) 0;

    long size();

    boolean isEmpty();

    /**
     * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity
     * restrictions, returning {@code true} upon success and throwing an {@code IllegalStateException} if no space is
     * currently available.
     *
     * @param e
     *            the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws IllegalStateException
     *             if the element cannot be added at this time due to capacity restrictions
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added to this queue
     */
    default boolean add(final byte e) {
        if (offer(e)) {
            return true;
        } else {
            throw new IllegalStateException("offer returned false");
        }
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity
     * restrictions. When using a capacity-restricted queue, this method is generally preferable to {@link #add}, which
     * can fail to insert an element only by throwing an exception.
     *
     * @param e
     *            the element to add
     * @return {@code true} if the element was added to this queue, else {@code false}
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added to this queue
     */
    boolean offer(byte e);

    /**
     * Retrieves and removes the head of this queue. This method differs from {@link #poll() poll()} only in that it
     * throws an exception if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException
     *             if this queue is empty
     */
    default byte remove() {
        if (isEmpty()) {
            throw FastNoSuchElementException.getInstance("poll returned nothing");
        }
        return poll();
    }

    /**
     * Retrieves and removes the head of this queue, or returns {@link #MISSING_VALUE} if this queue is empty (use
     * {@link #isEmpty()} to distinguish a missing value from a zero element).
     *
     * @return the head of this queue, or {@link #MISSING_VALUE} if this queue is empty
     */
    byte poll();

    /**
     * Retrieves, but does not remove, the head of this queue. This method differs from {@link #peek peek} only in that
     * it throws an exception if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException
     *             if this queue is empty
     */
    default byte element() {
        if (isEmpty()) {
            throw FastNoSuchElementException.getInstance("peek returned nothing");
        }
        return peek();
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns {@link #MISSING_VALUE} if this queue is empty
     * (use {@link #isEmpty()} to distinguish a missing value from a zero element).
     *
     * @return the head of this queue, or {@link #MISSING_VALUE} if this queue is empty
     */
    byte peek();

    void clear();

}
