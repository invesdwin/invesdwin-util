package de.invesdwin.util.collections.iterable;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This iterator can be used to buffer another iterator. Useful to load from a file immediately to keep the file open as
 * shorty as possible, then serve the items from memory and removing them on the go to keep memory consumption low.
 * 
 * Helpful to fix too many open files during iteration of lots of files in parallel without too much of a performance
 * overhead.
 * 
 * Also a faster alternative to any list when only iteration is needed.
 */
@NotThreadSafe
public class BufferingIterator<E> implements ICloseableIterator<E>, Iterable<E> {

    private Node head;
    private Node tail;
    private int size = 0;

    public BufferingIterator() {}

    public BufferingIterator(final Iterator<? extends E> iterator) {
        addAll(iterator);
    }

    public BufferingIterator(final Iterable<? extends E> iterable) {
        addAll(iterable);
    }

    @Override
    public boolean hasNext() {
        return head != null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public E next() {
        final E value = getHead();
        head = head.getNext();
        size--;
        return value;
    }

    public E getHead() {
        if (head == null) {
            throw new NoSuchElementException();
        } else {
            return head.getValue();
        }
    }

    public E getTail() {
        return tail.getValue();
    }

    public void add(final E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        final Node newTail = new Node(element);
        if (head == null) {
            head = newTail;
        } else {
            tail.setNext(newTail);
        }
        size++;
        tail = newTail;
    }

    public void addAll(final Iterable<? extends E> iterable) {
        if (iterable == null) {
            return;
        } else if (iterable instanceof BufferingIterator) {
            @SuppressWarnings("unchecked")
            final BufferingIterator<E> cIterable = (BufferingIterator<E>) iterable;
            size += cIterable.size;
            if (head == null) {
                head = cIterable.head;
            } else {
                tail.setNext(cIterable.head);
            }
            tail = cIterable.tail;
            cIterable.clear();
        } else {
            addAll(iterable.iterator());
        }
    }

    public void addAll(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            return;
        }
        Node prev = tail;
        try {
            if (tail == null) {
                prev = new Node(iterator.next());
                size++;
            }
            if (head == null) {
                head = prev;
            }
            while (true) {
                final Node next = new Node(iterator.next());
                prev.setNext(next);
                prev = next;
                size++;
            }
        } catch (final NoSuchElementException e) {
            if (iterator instanceof Closeable) {
                final Closeable cIterator = (Closeable) iterator;
                try {
                    cIterator.close();
                } catch (final IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        tail = prev;
    }

    @Override
    public void close() {
        clear();
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
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

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private Node innerHead = head;

            @Override
            public boolean hasNext() {
                return innerHead != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final E value = innerHead.getValue();
                innerHead = innerHead.getNext();
                return value;
            }

        };
    }

}
