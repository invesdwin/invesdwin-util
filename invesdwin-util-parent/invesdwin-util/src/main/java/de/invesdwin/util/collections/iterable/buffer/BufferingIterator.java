package de.invesdwin.util.collections.iterable.buffer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterator;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.Strings;

/**
 * This iterator can be used to buffer another iterator. Useful to load from a file immediately to keep the file open as
 * shorty as possible, then serve the items from memory and removing them on the go to keep memory consumption low.
 * 
 * Helpful to fix too many open files during iteration of lots of files in parallel without too much of a performance
 * overhead. This is not a replacement for classic lists, since those can be iterated faster if no item removal is
 * required. Though BufferingIterator beats HashSets in raw iteration speed.
 * 
 * Also a faster alternative to any list when only iteration is needed.
 */
@NotThreadSafe
public class BufferingIterator<E> implements IBufferingIterator<E>, ISerializableValueObject {

    private Node<E> head;
    private Node<E> tail;
    private int size = 0;

    public BufferingIterator() {}

    public BufferingIterator(final IBufferingIterator<E> iterable) {
        addAll(iterable);
    }

    public BufferingIterator(final ICloseableIterator<? extends E> iterator) {
        addAll(iterator);
    }

    @Deprecated
    public BufferingIterator(final Iterator<? extends E> iterator) {
        addAll(iterator);
    }

    public BufferingIterator(final ICloseableIterable<? extends E> iterable) {
        addAll(iterable);
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
        if (head == null) {
            throw FastNoSuchElementException.getInstance("BufferingIterator next() head is null");
        }
        final E value = getHead();
        head = head.getNext();
        size--;
        return value;
    }

    @Override
    public void remove() {
        //noop; next already removed the value
    }

    @Override
    public boolean remove(final E element) {
        if (element == null) {
            return false;
        }
        if (head == null) {
            return false;
        }
        if (Objects.equals(head.getValue(), element)) {
            next();
            return true;
        }
        Node<E> curHead = head;
        while (curHead != null) {
            final Node<E> next = curHead.getNext();
            if (Objects.equals(next, element)) {
                final Node<E> nextNext = next.getNext();
                curHead.setNext(nextNext);
                if (nextNext == null) {
                    tail = curHead;
                }
                size--;
                return true;
            }
            curHead = curHead.getNext();
        }
        return false;
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
    public boolean prepend(final E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        final Node<E> newHead = new Node<>(element);
        newHead.setNext(head);
        head = newHead;
        size++;
        return true;
    }

    @Override
    public boolean add(final E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        final Node<E> newTail = new Node<>(element);
        if (head == null) {
            head = newTail;
        } else {
            tail.setNext(newTail);
        }
        size++;
        tail = newTail;
        return true;
    }

    @Override
    public boolean addAll(final Iterable<? extends E> iterable) {
        if (iterable == null) {
            return false;
        } else {
            return addAll(WrapperCloseableIterable.maybeWrap(iterable));
        }
    }

    @Override
    public boolean addAll(final ICloseableIterable<? extends E> iterable) {
        if (iterable == null) {
            return false;
        } else {
            return addAll(iterable.iterator());
        }
    }

    @Override
    public boolean addAll(final IBufferingIterator<E> iterable) {
        if (iterable == null) {
            return false;
        } else {
            return addAll(iterable.iterator());
        }
    }

    @Deprecated
    @Override
    public boolean addAll(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            return false;
        } else {
            return addAll(WrapperCloseableIterator.maybeWrap(iterator));
        }
    }

    @Override
    public boolean addAll(final ICloseableIterator<? extends E> iterator) {
        if (iterator == null) {
            return false;
        } else {
            Node<E> prev = tail;
            final int sizeBefore = size;
            try {
                if (tail == null) {
                    prev = new Node<>(iterator.next());
                    size++;
                }
                if (head == null) {
                    head = prev;
                }
                while (true) {
                    final Node<E> next = new Node<>(iterator.next());
                    prev.setNext(next);
                    prev = next;
                    size++;
                }
            } catch (final NoSuchElementException e) {
                //end reached
            } finally {
                iterator.close();
            }
            tail = prev;
            return sizeBefore < size;
        }
    }

    @Override
    public boolean consume(final Iterable<? extends E> iterable) {
        if (iterable == null) {
            return false;
        } else if (iterable instanceof BufferingIterator) {
            @SuppressWarnings("unchecked")
            final BufferingIterator<E> cIterable = (BufferingIterator<E>) iterable;
            return consume(cIterable);
        } else {
            return addAll(iterable);
        }
    }

    @Deprecated
    @Override
    public boolean consume(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            return false;
        } else if (iterator instanceof BufferingIterator) {
            @SuppressWarnings("unchecked")
            final BufferingIterator<E> cIterable = (BufferingIterator<E>) iterator;
            return consume(cIterable);
        } else {
            return addAll(iterator);
        }
    }

    @Override
    public boolean consume(final IBufferingIterator<E> iterator) {
        if (iterator instanceof BufferingIterator) {
            final BufferingIterator<E> cIterator = (BufferingIterator<E>) iterator;
            final int sizeBefore = size;
            size += cIterator.size;
            if (head == null) {
                head = cIterator.head;
            } else {
                tail.setNext(cIterator.head);
            }
            tail = cIterator.tail;
            iterator.clear();
            return sizeBefore < size;
        } else {
            return addAll((ICloseableIterator<E>) iterator);
        }
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

    private static class Node<_E> implements ISerializableValueObject {
        private final _E value;
        private Node<_E> next;

        Node(final _E value) {
            this.value = value;
        }

        public _E getValue() {
            return value;
        }

        public Node<_E> getNext() {
            return next;
        }

        public void setNext(final Node<_E> next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return Strings.asString(value);
        }
    }

    @Override
    public String toString() {
        return Lists.toListWithoutHasNext(iterator()).toString();
    }

    @Override
    public ICloseableIterator<E> iterator() {
        if (head == null) {
            return EmptyCloseableIterator.getInstance();
        } else {
            return new BufferingIteratorIterator(head);
        }
    }

    private final class BufferingIteratorIterator implements ICloseableIterator<E> {
        private Node<E> innerRemovablePrev;
        private Node<E> innerRemovable;
        private Node<E> innerHead;

        private BufferingIteratorIterator(final Node<E> head) {
            this.innerHead = head;
        }

        @Override
        public boolean hasNext() {
            return innerHead != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw FastNoSuchElementException.getInstance("BufferingIterator: hasNext is false");
            }
            innerRemovablePrev = innerRemovable;
            innerRemovable = innerHead;
            final E value = innerHead.getValue();
            innerHead = innerHead.getNext();
            return value;
        }

        @Override
        public void remove() {
            if (innerRemovable == null) {
                throw new IllegalStateException("next not called yet");
            }
            final Node<E> next;
            if (innerRemovable != null) {
                next = innerRemovable.getNext();
            } else {
                next = null;
            }
            if (innerRemovablePrev == null) {
                if (innerRemovable == head) {
                    BufferingIterator.this.next();
                    innerRemovable = null;
                }
            } else if (innerRemovablePrev.getNext() == innerRemovable) {
                innerRemovablePrev.setNext(next);
                size--;
                if (next == null && innerRemovable == tail) {
                    tail = innerRemovablePrev;
                }
            }
        }

        @Override
        public void close() {
            innerHead = null;
        }
    }

    private final class SnapshotBufferingIteratorIterator implements ICloseableIterator<E> {
        private Node<E> innerRemovablePrev;
        private Node<E> innerRemovable;
        private Node<E> innerHead;
        private final Node<E> innerTail;

        private SnapshotBufferingIteratorIterator(final Node<E> head, final Node<E> tail) {
            this.innerHead = head;
            this.innerTail = tail;
        }

        @Override
        public boolean hasNext() {
            return innerHead != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw FastNoSuchElementException.getInstance("BufferingIterator: hasNext is false");
            }
            innerRemovablePrev = innerRemovable;
            innerRemovable = innerHead;
            final E value = innerHead.getValue();
            if (innerHead == innerTail) {
                innerHead = null;
            } else {
                innerHead = innerHead.getNext();
            }
            return value;
        }

        @Override
        public void remove() {
            if (innerRemovable == null) {
                throw new IllegalStateException("next not called yet");
            }
            final Node<E> next;
            if (innerRemovable != null) {
                next = innerRemovable.getNext();
            } else {
                next = null;
            }
            if (innerRemovablePrev == null) {
                if (innerRemovable == head) {
                    BufferingIterator.this.next();
                    innerRemovable = null;
                }
            } else if (innerRemovablePrev.getNext() == innerRemovable) {
                innerRemovablePrev.setNext(next);
                size--;
                if (next == null && innerRemovable == tail) {
                    tail = innerRemovablePrev;
                }
            }
        }

        @Override
        public void close() {
            innerHead = null;
        }
    }

    @Override
    public ICloseableIterable<E> snapshot() {
        final Node<E> head = this.head;
        final Node<E> tail = this.tail;
        return new ICloseableIterable<E>() {
            @Override
            public ICloseableIterator<E> iterator() {
                return new SnapshotBufferingIteratorIterator(head, tail);
            }
        };
    }

}
