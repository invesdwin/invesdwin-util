package de.invesdwin.util.lang.comparator;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Comparables {

    private Comparables() {}

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
