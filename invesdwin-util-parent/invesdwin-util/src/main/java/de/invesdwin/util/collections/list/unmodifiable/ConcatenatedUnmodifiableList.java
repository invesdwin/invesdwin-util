package de.invesdwin.util.collections.list.unmodifiable;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.collection.ListCloseableIterator;

@NotThreadSafe
public class ConcatenatedUnmodifiableList<E> extends AUnmodifiableList<E> {

    private final List<? extends E> l1;
    private final List<? extends E> l2;

    public ConcatenatedUnmodifiableList(final List<? extends E> l1, final List<? extends E> l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public int size() {
        return l1.size() + l2.size();
    }

    @Override
    public boolean isEmpty() {
        return l1.isEmpty() && l2.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return l1.contains(o) || l2.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ListCloseableIterator<>(this);
    }

    @Override
    public Object[] toArray() {
        final Object[] array = new Object[size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] a) {
        for (int i = 0; i < a.length; i++) {
            a[i] = (T) get(i);
        }
        return a;
    }

    @Override
    public E get(final int index) {
        final int l1Size = l1.size();
        if (index >= l1Size) {
            final int l2Index = index - l1Size;
            return l2.get(l2Index);
        } else {
            return l1.get(index);
        }
    }

    @Override
    public int indexOf(final Object o) {
        final int indexOf1 = l1.indexOf(o);
        if (indexOf1 >= 0) {
            return indexOf1;
        }
        final int indexOf2 = l2.indexOf(o);
        if (indexOf2 >= 0) {
            return indexOf2 + l1.size();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        final int indexOf1 = l1.lastIndexOf(o);
        if (indexOf1 >= 0) {
            return indexOf1;
        }
        final int indexOf2 = l2.lastIndexOf(o);
        if (indexOf2 >= 0) {
            return indexOf2 + l1.size();
        }
        return -1;
    }

}
