package de.invesdwin.util.collections.queue;

import java.util.Collection;
import java.util.NoSuchElementException;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Doubles;

public interface IDoubleQueue {

    int size();

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
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException
     *             if the specified element is null and this queue does not permit null elements
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added to this queue
     */
    default boolean add(final double e) {
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
     * @throws ClassCastException
     *             if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException
     *             if the specified element is null and this queue does not permit null elements
     * @throws IllegalArgumentException
     *             if some property of this element prevents it from being added to this queue
     */
    boolean offer(double e);

    /**
     * Retrieves and removes the head of this queue. This method differs from {@link #poll() poll()} only in that it
     * throws an exception if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException
     *             if this queue is empty
     */
    default double remove() {
        final double poll = poll();
        if (!Doubles.isNaN(poll)) {
            return poll;
        } else {
            throw FastNoSuchElementException.getInstance("poll returned NaN");
        }
    }

    /**
     * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    double poll();

    /**
     * Retrieves, but does not remove, the head of this queue. This method differs from {@link #peek peek} only in that
     * it throws an exception if this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException
     *             if this queue is empty
     */
    default double element() {
        final double peek = peek();
        if (!Doubles.isNaN(peek)) {
            return peek;
        } else {
            throw FastNoSuchElementException.getInstance("peek returned NaN");
        }
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    double peek();

    void clear();

}
