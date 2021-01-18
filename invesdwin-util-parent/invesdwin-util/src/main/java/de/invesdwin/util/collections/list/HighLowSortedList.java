package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;

/**
 * A performant way to keep a list of ordered elements when the elements arrive in a more or less ordered fashion
 */
@NotThreadSafe
public class HighLowSortedList<E> extends ADelegateList<E> {
    private final Comparator<E> comparator;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HighLowSortedList(final Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    protected List<E> newDelegate() {
        return new ArrayList<E>();
    }

    /**
     * adds the element at its appropriate spot doing the search in ascending order to add it in the front
     */
    @Override
    public void add(final int index, final E o) {
        final int size = getDelegate().size();
        for (int i = 0; i < size; i++) {
            if (comparator.compare(getDelegate().get(i), o) > 0) {
                getDelegate().add(i, o);
                return;
            }
        }
        getDelegate().add(o);
    }

    /**
     * adds the element at its appropriate spot doing the search in descending order to add it in the end
     */
    @Override
    public boolean add(final E o) {
        final int size = getDelegate().size();
        for (int i = size; i > 0; i--) {
            if (comparator.compare(getDelegate().get(i - 1), o) < 0) {
                getDelegate().add(i, o);
                return true;
            }
        }
        getDelegate().add(0, o);
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        for (final E o : c) {
            add(o);
        }
        return true;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return addAll(c);
    }

}
