package de.invesdwin.util.lang.comparator;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Comparables {

    @SuppressWarnings("rawtypes")
    private static final Comparator COMPARATOR = new Comparator() {

        @Override
        public int compare(final Object o1, final Object o2) {
            return Comparables.compare((Comparable) o1, (Comparable) o2);
        }
    };

    @SuppressWarnings("rawtypes")
    private static final Comparator COMPARATOR_NOT_NULL_SAFE = new Comparator() {

        @Override
        public int compare(final Object o1, final Object o2) {
            return Comparables.compareNotNullSafe((Comparable) o1, (Comparable) o2);
        }
    };

    private Comparables() {}

    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> getComparator() {
        return COMPARATOR;
    }

    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> getComparatorNotNullSafe() {
        return COMPARATOR_NOT_NULL_SAFE;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Comparable> int compare(final T a, final T b) {
        if (a == null && b == null) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Comparable> int compareNotNullSafe(final T a, final T b) {
        return a.compareTo(b);
    }
}
