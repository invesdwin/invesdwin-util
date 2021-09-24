package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.lang.comparator.IComparator;

/**
 * A performant way to keep a list of ordered elements when the elements arrive in a random order
 * 
 * https://stackoverflow.com/questions/2945017/javas-equivalent-to-bisect-in-python
 */
@NotThreadSafe
public class BisectSortedList<E> extends ADelegateList<E> {
    private final IComparator<E> comparator;

    /**
     * The comparator can throw a a DuplicateElementException to ignore an element.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BisectSortedList(final IComparator comparator) {
        this.comparator = comparator;
    }

    @Override
    protected List<E> newDelegate() {
        return new ArrayList<E>();
    }

    @Override
    public void add(final int index, final E o) {
        try {
            getDelegate().add(bisect(o), o);
        } catch (final DuplicateElementException e) {
            //ignore duplicate
        }
    }

    private int bisect(final E x) {
        int lo = 0;
        int hi = size();
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            if (comparator.compareTyped(getDelegate().get(mid), x) > 0) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    @Override
    public boolean add(final E o) {
        try {
            getDelegate().add(bisect(o), o);
            return true;
        } catch (final DuplicateElementException e) {
            //ignore duplicate
            return false;
        }
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

}
