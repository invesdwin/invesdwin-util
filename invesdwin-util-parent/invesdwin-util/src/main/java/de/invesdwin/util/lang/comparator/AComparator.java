package de.invesdwin.util.lang.comparator;

import javax.annotation.concurrent.Immutable;

/**
 * Is ascending internally
 */
@Immutable
public abstract class AComparator<E> implements IComparator<E> {

    private static final AComparator<Comparable<Object>> DEFAULT_INSTANCE = new AComparator<Comparable<Object>>() {
        @Override
        public int compareTypedNotNullSafe(final Comparable<Object> e1, final Comparable<Object> e2) {
            return e1.compareTo(e2);
        }
    };

    private IComparator<E> descending;
    private IComparator<E> notNullSafe;

    public AComparator() {
    }

    @Override
    public boolean isAscending() {
        return true;
    }

    @Override
    public boolean isNullSafe() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(final Object o1, final Object o2) {
        final E e1 = (E) o1;
        final E e2 = (E) o2;
        return compareTyped(e1, e2);
    }

    @Override
    public abstract int compareTypedNotNullSafe(E o1, E o2);

    @Override
    public int compareTyped(final E o1, final E o2) {
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) {
            if (isNullFirst()) {
                return 1;
            } else {
                return -1;
            }
        } else if (o2 == null) {
            if (isNullFirst()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return compareTypedNotNullSafe(o1, o2);
        }
    }

    protected boolean isNullFirst() {
        return false;
    }

    @Override
    public IComparator<E> asNullSafe() {
        return this;
    }

    @Override
    public IComparator<E> asAscending() {
        return this;
    }

    @Override
    public IComparator<E> asNotNullSafe() {
        if (notNullSafe == null) {
            notNullSafe = newNotNullSafeComparator();
        }
        return notNullSafe;
    }

    protected IComparator<E> newNotNullSafeComparator() {
        return new NotNullSafeComparator<E>(this);
    }

    @Override
    public IComparator<E> asDescending() {
        if (descending == null) {
            descending = newDescendingComparator();
        }
        return descending;
    }

    protected IComparator<E> newDescendingComparator() {
        return new DescendingComparator<E>(this);
    }

    @SuppressWarnings("unchecked")
    public static <T> IComparator<T> getDefaultInstance() {
        return (IComparator<T>) DEFAULT_INSTANCE;
    }

}
