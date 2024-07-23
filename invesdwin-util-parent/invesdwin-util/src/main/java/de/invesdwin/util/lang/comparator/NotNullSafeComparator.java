package de.invesdwin.util.lang.comparator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;

@Immutable
public class NotNullSafeComparator<E> implements IComparator<E> {

    private final IComparator<E> nullSafe;

    protected NotNullSafeComparator(final IComparator<E> nullSafe) {
        Assertions.checkTrue(nullSafe.isNullSafe());
        this.nullSafe = nullSafe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compare(final Object o1, final Object o2) {
        final E e1 = (E) o1;
        final E e2 = (E) o2;
        return nullSafe.compareTypedNotNullSafe(e1, e2);
    }

    @Override
    public int compareTyped(final E e1, final E e2) {
        return nullSafe.compareTypedNotNullSafe(e1, e2);
    }

    @Override
    public int compareTypedNotNullSafe(final E e1, final E e2) {
        return nullSafe.compareTypedNotNullSafe(e1, e2);
    }

    @Override
    public boolean isAscending() {
        return nullSafe.isAscending();
    }

    @Override
    public boolean isNullSafe() {
        return false;
    }

    @Override
    public IComparator<E> asNullSafe() {
        return nullSafe;
    }

    @Override
    public IComparator<E> asNotNullSafe() {
        return this;
    }

    @Override
    public IComparator<E> asAscending() {
        return nullSafe.asAscending().asNotNullSafe();
    }

    @Override
    public IComparator<E> asDescending() {
        return nullSafe.asDescending().asNotNullSafe();
    }

    @Override
    public ISortAlgorithm getSortAlgorithm() {
        return nullSafe.getSortAlgorithm();
    }

}
