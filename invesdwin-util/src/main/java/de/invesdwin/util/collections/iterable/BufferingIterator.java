package de.invesdwin.util.collections.iterable;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This iterator can be used to buffer another iterator. Useful to load from a file immediately to keep the file open as
 * shorty as possible, then serve the items from memory and removing them on the go to keep memory consumption low.
 * 
 * Helpful to fix too many open files during iteration of lots of files in parallel without too much of a performance
 * overhead.
 */
@NotThreadSafe
public class BufferingIterator<E> extends ACloseableIterator<E> {

    private Node head = null;

    public BufferingIterator(final ACloseableIterator<E> iterator) {
        Node prev = new Node(iterator.next());
        head = prev;
        try {
            while (true) {
                final Node next = new Node(iterator.next());
                prev.setNext(next);
                prev = next;
            }
        } catch (final NoSuchElementException e) {
            iterator.close();
        }
    }

    @Override
    protected boolean innerHasNext() {
        return head != null;
    }

    @Override
    protected E innerNext() {
        if (head == null) {
            throw new NoSuchElementException();
        }
        final E value = head.getValue();
        head = head.getNext();
        return value;
    }

    @Override
    protected void innerClose() {
        head = null;
    }

    private class Node {
        private final E value;
        private Node next;

        Node(final E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(final Node next) {
            this.next = next;
        }
    }

}
