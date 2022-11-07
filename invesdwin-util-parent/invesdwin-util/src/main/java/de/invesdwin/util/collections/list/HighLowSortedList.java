package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateList;
import de.invesdwin.util.lang.comparator.IComparator;

/**
 * A performant way to keep a list of ordered elements when the elements arrive in a more or less ordered fashion
 */
@NotThreadSafe
public class HighLowSortedList<E> extends ADelegateList<E> {
    private final IComparator<E> comparator;

    /**
     * The comparator can throw a a DuplicateElementException to ignore an element.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HighLowSortedList(final IComparator comparator) {
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
        try {
            final int size = getDelegate().size();
            for (int i = 0; i < size; i++) {
                if (comparator.compareTyped(getDelegate().get(i), o) > 0) {
                    getDelegate().add(i, o);
                    return;
                }
            }
            getDelegate().add(o);
        } catch (final NoSuchElementException e) {
            //ignore DuplicateElementException
        }
    }

    /**
     * adds the element at its appropriate spot doing the search in descending order to add it in the end
     */
    @Override
    public boolean add(final E o) {
        try {
            final int size = getDelegate().size();
            for (int i = size; i > 0; i--) {
                if (comparator.compareTyped(getDelegate().get(i - 1), o) < 0) {
                    getDelegate().add(i, o);
                    return true;
                }
            }
            getDelegate().add(0, o);
            return true;
        } catch (final NoSuchElementException e) {
            //ignore DuplicateElementException
            return false;
        }
    }

    /**
     * adds the element at its appropriate spot doing the search in descending order to add it in the end
     */
    public int addGetIndex(final E o) {
        try {
            final int size = getDelegate().size();
            for (int i = size; i > 0; i--) {
                if (comparator.compareTyped(getDelegate().get(i - 1), o) < 0) {
                    getDelegate().add(i, o);
                    return i;
                }
            }
            getDelegate().add(0, o);
            return 0;
        } catch (final NoSuchElementException e) {
            //ignore DuplicateElementException
            return -1;
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
