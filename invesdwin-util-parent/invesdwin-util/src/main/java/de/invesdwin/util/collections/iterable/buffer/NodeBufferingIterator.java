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
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.error.FastNoSuchElementException;

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
public class NodeBufferingIterator<E extends INode<E>> implements IBufferingIterator<E>, ISerializableValueObject {

    private E head;
    private E tail;
    private int size = 0;

    public NodeBufferingIterator() {}

    public NodeBufferingIterator(final IBufferingIterator<E> iterable) {
        addAll(iterable);
    }

    public NodeBufferingIterator(final ICloseableIterator<? extends E> iterator) {
        addAll(iterator);
    }

    @Deprecated
    public NodeBufferingIterator(final Iterator<? extends E> iterator) {
        addAll(iterator);
    }

    public NodeBufferingIterator(final ICloseableIterable<? extends E> iterable) {
        addAll(iterable);
    }

    public NodeBufferingIterator(final Iterable<? extends E> iterable) {
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
        value.setNext(null);
        if (head != null) {
            head.setPrev(null);
        }
        return value;
    }

    @Override
    public E getHead() {
        if (head == null) {
            return null;
        } else {
            return head;
        }
    }

    @Override
    public E getTail() {
        if (tail == null) {
            return null;
        } else {
            return tail;
        }
    }

    @Override
    public boolean prepend(final E newHead) {
        if (newHead == null) {
            throw new NullPointerException();
        }
        newHead.setNext(head);
        head.setPrev(newHead);
        head = newHead;
        size++;
        return true;
    }

    @Override
    public boolean add(final E newTail) {
        if (newTail == null) {
            throw new NullPointerException();
        }
        if (head == null) {
            head = newTail;
            newTail.setPrev(null);
            newTail.setNext(null);
        } else {
            tail.setNext(newTail);
            newTail.setPrev(tail);
            newTail.setNext(null);
        }
        size++;
        tail = newTail;
        return true;
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
        if (head == element) {
            next();
            return true;
        }
        element.getPrev().setNext(element.getNext());
        if (element.getNext() != null) {
            element.getNext().setPrev(element.getPrev());
        } else {
            tail = element.getPrev();
        }
        element.setNext(null);
        element.setPrev(null);
        size--;
        return false;
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
            E prev = tail;
            final int sizeBefore = size;
            try {
                if (tail == null) {
                    prev = iterator.next();
                    size++;
                }
                if (head == null) {
                    head = prev;
                }
                while (true) {
                    final E next = iterator.next();
                    next.setPrev(prev);
                    next.setNext(null);
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
        } else if (iterable instanceof NodeBufferingIterator) {
            @SuppressWarnings("unchecked")
            final NodeBufferingIterator<E> cIterable = (NodeBufferingIterator<E>) iterable;
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
        } else if (iterator instanceof NodeBufferingIterator) {
            @SuppressWarnings("unchecked")
            final NodeBufferingIterator<E> cIterable = (NodeBufferingIterator<E>) iterator;
            return consume(cIterable);
        } else {
            return addAll(iterator);
        }
    }

    @Override
    public boolean consume(final IBufferingIterator<E> iterator) {
        if (iterator instanceof NodeBufferingIterator) {
            final NodeBufferingIterator<E> cIterator = (NodeBufferingIterator<E>) iterator;
            final int sizeBefore = size;
            size += cIterator.size;
            if (head == null) {
                head = cIterator.head;
            } else {
                cIterator.head.setPrev(tail);
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

    public interface INode<E extends INode<E>> {

        E getNext();

        void setNext(E next);

        E getPrev();

        void setPrev(E prev);

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
        private E innerRemovablePrev;
        private E innerRemovable;
        private E innerHead;

        private BufferingIteratorIterator(final E head) {
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
            final E value = innerHead;
            innerHead = innerHead.getNext();
            return value;
        }

        @Override
        public void remove() {
            if (innerRemovable == null) {
                throw new IllegalStateException("next not called yet");
            }
            final E next;
            if (innerRemovable != null) {
                next = innerRemovable.getNext();
            } else {
                next = null;
            }
            if (innerRemovablePrev == null) {
                if (innerRemovable == head) {
                    NodeBufferingIterator.this.next();
                    innerRemovable = null;
                }
            } else if (innerRemovablePrev.getNext() == innerRemovable) {
                innerRemovablePrev.setNext(next);
                if (next != null) {
                    next.setPrev(innerRemovablePrev);
                }
                innerRemovable.setNext(null);
                innerRemovable.setPrev(null);
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
        private E innerRemovablePrev;
        private E innerRemovable;
        private E innerHead;
        private final E innerTail;

        private SnapshotBufferingIteratorIterator(final E head, final E tail) {
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
            final E value = innerHead;
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
            final E next;
            if (innerRemovable != null) {
                next = innerRemovable.getNext();
            } else {
                next = null;
            }
            if (innerRemovablePrev == null) {
                if (innerRemovable == head) {
                    NodeBufferingIterator.this.next();
                    innerRemovable = null;
                }
            } else if (innerRemovablePrev.getNext() == innerRemovable) {
                innerRemovablePrev.setNext(next);
                if (next != null) {
                    next.setPrev(innerRemovablePrev);
                }
                innerRemovable.setNext(null);
                innerRemovable.setPrev(null);
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
        final E head = this.head;
        final E tail = this.tail;
        return new ICloseableIterable<E>() {
            @Override
            public ICloseableIterator<E> iterator() {
                return new SnapshotBufferingIteratorIterator(head, tail);
            }
        };
    }

}
