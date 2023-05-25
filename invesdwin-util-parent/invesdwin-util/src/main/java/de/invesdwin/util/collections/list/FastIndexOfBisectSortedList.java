package de.invesdwin.util.collections.list;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ATransformingIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.lang.comparator.AComparator;
import de.invesdwin.util.lang.comparator.IComparator;

@NotThreadSafe
public class FastIndexOfBisectSortedList<E> implements List<E> {
    private final Map<E, IndexedValue<E>> map = new HashMap<>();
    private final BisectSortedList<IndexedValue<E>> list;

    /**
     * The comparator can throw a a DuplicateElementException to ignore an element.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public FastIndexOfBisectSortedList(final IComparator comparator) {
        list = new BisectSortedList<IndexedValue<E>>(new AComparator<IndexedValue<E>>() {
            @Override
            public int compareTypedNotNullSafe(final IndexedValue<E> o1, final IndexedValue<E> o2) {
                return comparator.compare(unwrap(o1), unwrap(o2));
            }
        }) {
            @Override
            protected void bisectAdd(final int bisectIndex, final IndexedValue<E> o) {
                super.bisectAdd(bisectIndex, o);
                o.index = bisectIndex;
                for (int i = bisectIndex + 1; i < size(); i++) {
                    final IndexedValue<E> next = get(i);
                    next.index = next.index + 1;
                }
                map.put(o.value, o);
            }
        };
    }

    private E unwrap(final IndexedValue<E> o) {
        if (o == null) {
            return null;
        } else {
            return o.value;
        }
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ATransformingIterator<IndexedValue<E>, E>(WrapperCloseableIterable.maybeWrap(list).iterator()) {
            @Override
            protected E transform(final IndexedValue<E> value) {
                return unwrap(value);
            }
        };
    }

    @Override
    public Object[] toArray() {
        final Object[] array = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = unwrap(list.get(i));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(final T[] a) {
        final T[] array;
        if (a.length >= size()) {
            array = a;
        } else {
            array = (T[]) Arrays.newInstance(a.getClass().getComponentType(), list.size());
        }
        for (int i = 0; i < list.size(); i++) {
            array[i] = (T) unwrap(list.get(i));
        }
        return array;
    }

    @Override
    public boolean add(final E e) {
        if (contains(e)) {
            return false;
        }
        return list.add(new IndexedValue<E>(e));
    }

    @Override
    public boolean remove(final Object o) {
        final IndexedValue<E> removed = map.remove(o);
        if (removed == null) {
            return false;
        }
        Assertions.checkSame(removed, list.remove(removed.index));
        for (int i = removed.index; i < list.size(); i++) {
            final IndexedValue<E> next = list.get(i);
            next.index = next.index - 1;
        }
        return true;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return map.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        for (final E o : c) {
            if (add(o)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            if (remove(o)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        list.clear();
        map.clear();
    }

    @Override
    public E get(final int index) {
        return unwrap(list.get(index));
    }

    @Override
    public E set(final int index, final E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final E element) {
        add(element);
    }

    @Override
    public E remove(final int index) {
        final IndexedValue<E> removed = list.remove(index);
        Assertions.checkSame(removed, map.remove(removed.value));
        return removed.value;
    }

    @Override
    public int indexOf(final Object o) {
        final IndexedValue<E> get = map.get(o);
        if (get == null) {
            return -1;
        }
        return get.index;
    }

    @Override
    public int lastIndexOf(final Object o) {
        return indexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
    }

    private static final class IndexedValue<E> {
        private final E value;
        private int index;

        private IndexedValue(final E value) {
            this.value = value;
        }
    }

}
