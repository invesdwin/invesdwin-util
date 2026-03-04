package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@NotThreadSafe
public class ListSet<E> extends ADelegateList<E> implements Set<E> {
    private final Set<E> set = newSet();

    @Override
    protected final List<E> newDelegate() {
        return newList();
    }

    protected List<E> newList() {
        return new ArrayList<E>();
    }

    protected Set<E> newSet() {
        return ILockCollectionFactory.getInstance(false).newSet();
    }

    @Override
    public void add(final int index, final E element) {
        if (this.set.add(element)) {
            getDelegate().add(index, element);
        }
    }

    @Override
    public boolean add(final E o) {
        if (this.set.add(o)) {
            return this.getDelegate().add(o);
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
                this.getDelegate().add(insertIndex++, element);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        this.set.clear();
        this.getDelegate().clear();
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
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(final int index) {
        final E element = this.getDelegate().remove(index);
        if (element != null) {
            this.set.remove(element);
        }
        return element;
    }

    @Override
    public boolean remove(final Object o) {
        if (this.set.remove(o)) {
            this.getDelegate().remove(o);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        if (this.set.removeAll(c)) {
            this.getDelegate().removeAll(c);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        if (this.set.retainAll(c)) {
            this.getDelegate().retainAll(c);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public E set(final int index, final E element) {
        final E removed = this.getDelegate().set(index, element);
        if (removed != element) {
            this.set.remove(removed);
            this.set.add(element);
        }
        return removed;
    }

}