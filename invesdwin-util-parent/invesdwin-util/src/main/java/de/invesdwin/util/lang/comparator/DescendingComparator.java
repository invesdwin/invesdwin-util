package de.invesdwin.util.lang.comparator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;

@Immutable
final class DescendingComparator<E> implements IComparator<E> {

    private final IComparator<E> ascending;
    private IComparator<E> notNullSafe;

    DescendingComparator(final IComparator<E> ascending) {
        Assertions.checkTrue(ascending.isNullSafe());
        Assertions.checkTrue(ascending.isAscending());
        this.ascending = ascending;
    }

    @Override
    public int compare(final Object o1, final Object o2) {
        final int compare = ascending.compare(o1, o2);
        return compare * -1;
    }

    @Override
    public int compareTyped(final E o1, final E o2) {
        final int compare = ascending.compareTyped(o1, o2);
        return compare * -1;
    }

    @Override
    public int compareTypedNotNullSafe(final E o1, final E o2) {
        final int compare = ascending.compareTypedNotNullSafe(o1, o2);
        return compare * -1;
    }

    @Override
    public boolean isAscending() {
        return false;
    }

    @Override
    public boolean isNullSafe() {
        return ascending.isNullSafe();
    }

    @Override
    public IComparator<E> asNullSafe() {
        return ascending.asNullSafe();
    }

    @Override
    public IComparator<E> asNotNullSafe() {
        if (notNullSafe == null) {
            notNullSafe = new NotNullSafeComparator<>(this);
        }
        return notNullSafe;
    }

    @Override
    public IComparator<E> asAscending() {
        return ascending;
    }

    @Override
    public IComparator<E> asDescending() {
        return this;
    }

}