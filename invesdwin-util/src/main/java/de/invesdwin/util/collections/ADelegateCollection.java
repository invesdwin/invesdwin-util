package de.invesdwin.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateCollection<E> implements Collection<E> {

    private Collection<E> delegate;

    protected synchronized Collection<E> getDelegate() {
        if (delegate == null) {
            this.delegate = createDelegate();
        }
        return delegate;
    }

    protected abstract Collection<E> createDelegate();

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return getDelegate().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getDelegate().iterator();
    }

    @Override
    public Object[] toArray() {
        return getDelegate().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return getDelegate().toArray(a);
    }

    @Override
    public boolean add(final E e) {
        if (isAddAllowed(e)) {
            return getDelegate().add(e);
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(final Object o) {
        return getDelegate().remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return getDelegate().containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        final List<E> allowedElements = new ArrayList<E>();
        for (final E e : c) {
            if (isAddAllowed(e)) {
                allowedElements.add(e);
            }
        }
        return getDelegate().addAll(allowedElements);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return getDelegate().removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return getDelegate().retainAll(c);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    /**
     * Can be overwritten to add restrictions
     */
    public boolean isAddAllowed(final E e) {
        return true;
    }

}
