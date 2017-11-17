package de.invesdwin.util.collections.delegate;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ADelegateList<E> extends ADelegateCollection<E> implements List<E> {

    private final List<E> delegateList = (List<E>) super.getDelegate();

    public ADelegateList() {
        super();
    }

    ADelegateList(final List<E> delegate) {
        super(delegate);
    }

    @Override
    public List<E> getDelegate() {
        return delegateList;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        final Collection<E> allowedElements = filterAllowedElements(c);
        return getDelegate().addAll(index, allowedElements);
    }

    @Override
    public E get(final int index) {
        return getDelegate().get(index);
    }

    @Override
    public E set(final int index, final E element) {
        if (isAddAllowed(element)) {
            return getDelegate().set(index, element);
        } else {
            return get(index);
        }
    }

    @Override
    public void add(final int index, final E element) {
        if (isAddAllowed(element)) {
            getDelegate().add(index, element);
        }
    }

    @Override
    public E remove(final int index) {
        return getDelegate().remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return getDelegate().indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return getDelegate().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return getDelegate().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return getDelegate().listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return getDelegate().subList(fromIndex, toIndex);
    }

    @Override
    protected abstract List<E> newDelegate();

    public static <T> List<T> maybeUnwrapToRoot(final List<T> list) {
        List<T> cur = list;
        while (cur instanceof ADelegateList) {
            final ADelegateList<T> c = (ADelegateList<T>) cur;
            cur = c.getDelegate();
        }
        return cur;
    }

    @Override
    public Spliterator<E> spliterator() {
        return getDelegate().spliterator();
    }

}
