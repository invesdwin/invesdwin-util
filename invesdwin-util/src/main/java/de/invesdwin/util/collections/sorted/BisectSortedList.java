package de.invesdwin.util.collections.sorted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;

/**
 * A performant way to keep a list of ordered elements when the elements arrive in a random order
 * 
 * https://stackoverflow.com/questions/2945017/javas-equivalent-to-bisect-in-python
 */
@NotThreadSafe
public class BisectSortedList<E> extends ADelegateList<E> {
    private final Comparator<E> comparator;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BisectSortedList(final Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    protected List<E> newDelegate() {
        return new ArrayList<E>();
    }

    @Override
    public void add(final int index, final E o) {
        getDelegate().add(bisect(o), o);
    }

    private int bisect(final E x) {
        int lo = 0;
        int hi = size();
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            if (comparator.compare(getDelegate().get(mid), x) > 0) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    @Override
    public boolean add(final E o) {
        getDelegate().add(bisect(o), o);
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
