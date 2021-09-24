package de.invesdwin.util.lang.comparator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ACriteriaComparator<E> extends AComparator<E> {

    @SuppressWarnings("unchecked")
    @Override
    public int compare(final Object o1, final Object o2) {
        final E e1 = (E) o1;
        final E e2 = (E) o2;
        return compareTyped(e1, e2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTyped(final E o1, final E o2) {
        final Comparable<Object> c1 = (Comparable<Object>) getCompareCriteria(o1);
        final Comparable<Object> c2 = (Comparable<Object>) getCompareCriteria(o2);
        if (c1 == null) {
            if (isNullFirst()) {
                return 1;
            } else {
                return -1;
            }
        } else if (c2 == null) {
            if (isNullFirst()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return compareCriteriaNotNullSafe(c1, c2);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTypedNotNullSafe(final E o1, final E o2) {
        final Comparable<Object> c1 = (Comparable<Object>) getCompareCriteria(o1);
        final Comparable<Object> c2 = (Comparable<Object>) getCompareCriteria(o2);
        return compareCriteriaNotNullSafe(c1, c2);
    }

    public int compareCriteriaNotNullSafe(final Comparable<Object> c1, final Comparable<Object> c2) {
        return c1.compareTo(c2);
    }

    public Comparable<?> getCompareCriteria(final E e) {
        if (e == null) {
            return null;
        } else {
            return getCompareCriteriaNotNullSafe(e);
        }
    }

    /**
     * Null never reaches this method. This is ensured internally.
     */
    public abstract Comparable<?> getCompareCriteriaNotNullSafe(@Nonnull E e);

}
