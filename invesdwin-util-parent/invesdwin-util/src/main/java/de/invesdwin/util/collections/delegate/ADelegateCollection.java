package de.invesdwin.util.collections.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;

@NotThreadSafe
public abstract class ADelegateCollection<E> implements Collection<E>, ISerializableValueObject {

    private final Collection<E> delegate;

    public ADelegateCollection() {
        this.delegate = newDelegate();
    }

    protected ADelegateCollection(final Collection<E> delegate) {
        this.delegate = delegate;
    }

    public Collection<E> getDelegate() {
        return delegate;
    }

    protected abstract Collection<E> newDelegate();

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
        final Collection<E> allowedElements = filterAllowedElements(c);
        return getDelegate().addAll(allowedElements);
    }

    protected Collection<E> filterAllowedElements(final Collection<? extends E> c) {
        final List<E> allowedElements = new ArrayList<E>(c.size());
        for (final E e : c) {
            if (isAddAllowed(e)) {
                allowedElements.add(e);
            }
        }
        return allowedElements;
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

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public boolean equals(final Object obj) {
        return getDelegate().equals(obj);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    public static <T> Collection<T> maybeUnwrapToRoot(final Collection<T> collection) {
        Collection<T> cur = collection;
        while (cur instanceof ADelegateCollection) {
            final ADelegateCollection<T> c = (ADelegateCollection<T>) cur;
            cur = c.getDelegate();
        }
        return cur;
    }

}
