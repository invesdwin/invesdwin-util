package de.invesdwin.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ListSet<E> implements List<E>, Set<E> {
    private final List<E> list = newList();
    private final Set<E> set = newSet();

    public ListSet() {
        super();
    }

    protected List<E> newList() {
        return new ArrayList<E>();
    }

    protected Set<E> newSet() {
        return new HashSet<E>();
    }

    @Override
    public void add(final int index, final E element) {
        if (this.set.add(element)) {
            list.add(index, element);
        }
    }

    @Override
    public boolean add(final E o) {
        if (this.set.add(o)) {
            return this.list.add(o);
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        final Iterator<? extends E> i = c.iterator();
        while (i.hasNext()) {
            final E element = i.next();
            if (this.add(element)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        boolean changed = false;
        int insertIndex = index;
        final Iterator<? extends E> i = c.iterator();
        while (i.hasNext()) {
            final E element = i.next();
            if (this.set.add(element)) {
                this.list.add(insertIndex++, element);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        this.set.clear();
        this.list.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return this.set.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.set.containsAll(c);
    }

    @Override
    public E get(final int index) {
        return this.list.get(index);
    }

    @Override
    public int indexOf(final Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return this.list.lastIndexOf(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
        return this.list.listIterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        return this.list.listIterator(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(final int index) {
        final E element = this.list.remove(index);
        if (element != null) {
            this.set.remove(element);
        }
        return element;
    }

    @Override
    public boolean remove(final Object o) {
        if (this.set.remove(o)) {
            this.list.remove(o);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        if (this.set.removeAll(c)) {
            this.list.removeAll(c);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        if (this.set.retainAll(c)) {
            this.list.retainAll(c);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public E set(final int index, final E element) {
        this.set.add(element);
        return this.list.set(index, element);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof ListSet && this.list.equals(((ListSet<?>) other).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}