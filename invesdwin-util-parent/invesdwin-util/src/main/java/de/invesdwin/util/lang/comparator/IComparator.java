package de.invesdwin.util.lang.comparator;

import java.util.List;

public interface IComparator<E> extends ISerializableComparator<Object> {

    boolean isAscending();

    boolean isNullSafe();

    default ISerializableComparator<E> asTyped() {
        return this::compareTyped;
    }

    int compareTypedNotNullSafe(E e1, E e2);

    int compareTyped(E e1, E e2);

    IComparator<E> asNullSafe();

    default IComparator<E> asNullSafe(final boolean nullSafe) {
        if (nullSafe) {
            return asNullSafe();
        } else {
            return asNotNullSafe();
        }
    }

    IComparator<E> asNotNullSafe();

    default IComparator<E> asNotNullSafe(final boolean notNullSafe) {
        if (notNullSafe) {
            return asNotNullSafe();
        } else {
            return asNullSafe();
        }
    }

    IComparator<E> asAscending();

    default IComparator<E> asAscending(final boolean ascending) {
        if (ascending) {
            return asAscending();
        } else {
            return asDescending();
        }
    }

    IComparator<E> asDescending();

    default IComparator<E> asDescending(final boolean descending) {
        if (descending) {
            return asDescending();
        } else {
            return asAscending();
        }
    }

    static <T> IComparator<T> getDefaultInstance() {
        return AComparator.getDefaultInstance();
    }

    ISortAlgorithm getSortAlgorithm();

    default void sort(final List<? extends E> list) {
        getSortAlgorithm().sort(list, this);
    }

}
