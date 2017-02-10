package de.invesdwin.util.collections.iterable.buffer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Objects;

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
public class BufferingIterator<E> implements IBufferingIterator<E> {

    private Node head;
    private Node tail;
    private int size = 0;

    public BufferingIterator() {}

    public BufferingIterator(final BufferingIterator<E> iterable) {
        addAll(iterable);
    }

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

    @Override
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

    @Override
    public E getHead() {
        if (head == null) {
            return null;
        } else {
            return head.getValue();
        }
    }

    @Override
    public E getTail() {
        if (tail == null) {
            return null;
        } else {
            return tail.getValue();
        }
    }

    @Override
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

    @Override
    public void addAll(final Iterable<? extends E> iterable) {
        if (iterable == null) {
            return;
        } else {
            addAll(iterable.iterator());
        }
    }

    @Override
    public void addAll(final BufferingIterator<E> iterable) {
        if (iterable == null) {
            return;
        } else {
            addAll(iterable.iterator());
        }
    }

    @Override
    public void addAll(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            return;
        } else {
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
    }

    @Override
    public void consume(final Iterable<? extends E> iterable) {
        if (iterable == null) {
            return;
        } else if (iterable instanceof BufferingIterator) {
            @SuppressWarnings("unchecked")
            final BufferingIterator<E> cIterable = (BufferingIterator<E>) iterable;
            consume(cIterable);
        } else {
            addAll(iterable.iterator());
        }
    }

    @Override
    public void consume(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            return;
        } else if (iterator instanceof BufferingIterator) {
            @SuppressWarnings("unchecked")
            final BufferingIterator<E> cIterable = (BufferingIterator<E>) iterator;
            consume(cIterable);
        } else {
            addAll(iterator);
        }
    }

    @Override
    public void consume(final BufferingIterator<E> iterator) {
        size += iterator.size;
        if (head == null) {
            head = iterator.head;
        } else {
            tail.setNext(iterator.head);
        }
        tail = iterator.tail;
        iterator.clear();
    }

    @Override
    public void close() {
        clear();
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    private class Node {
        private final E value;
        private Node next;

        Node(final E value) {
            this.value = value;
        }

        Node(final Node copy) {
            this.value = copy.value;
            this.next = copy.next;
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

        @Override
        public String toString() {
            return Objects.toString(value);
        }
    }

    @Override
    public String toString() {
        return Lists.toListWithoutHasNext(iterator()).toString();
    }

    @Override
    public ICloseableIterator<E> iterator() {
        return new ICloseableIterator<E>() {

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

            @Override
            public void close() {
                innerHead = null;
            }

        };
    }

}
