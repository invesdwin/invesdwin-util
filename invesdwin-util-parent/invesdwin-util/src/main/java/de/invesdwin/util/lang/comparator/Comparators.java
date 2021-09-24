package de.invesdwin.util.lang.comparator;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Comparators {

    public static final IComparator<Comparable<Object>> COMPARATOR = IComparator.getDefaultInstance();

    private Comparators() {
    }

    /**
     * Checks all elements
     */
    public static <T> void assertOrderAll(final IComparator<T> comparator, final List<? extends T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        T previousE = null;
        for (final T e : list) {
            if (previousE == null) {
                previousE = e;
            } else {
                final int compareResult = comparator.compare(e, previousE);
                if (compareResult < 0) {
                    org.assertj.core.api.Assertions.assertThat(compareResult)
                            .as("Not  %s order: previousE [%s], e [%s]",
                                    comparator.isAscending() ? "ascending" : "descending", previousE, e)
                            .isGreaterThanOrEqualTo(0);
                }
            }
        }
    }

    /**
     * Also does not allow the same element to appear twice
     */
    public static <T> void assertOrderAllNoDuplicates(final IComparator<T> comparator, final List<? extends T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        T previousE = null;
        for (final T e : list) {
            if (previousE == null) {
                previousE = e;
            } else {
                final int compareResult = comparator.compare(e, previousE);
                if (compareResult <= 0) {
                    org.assertj.core.api.Assertions.assertThat(compareResult)
                            .as("No strict %s order: previousE [%s], e [%s]",
                                    comparator.isAscending() ? "ascending" : "descending", previousE, e)
                            .isGreaterThanOrEqualTo(0);
                }
            }
        }
    }

    /**
     * Just checks the first and last element.
     */
    public static <T> void assertOrderFast(final IComparator<T> comparator, final List<? extends T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        final T firstE = list.get(0);
        final T lastE = list.get(list.size() - 1);
        final int compareResult = comparator.compare(lastE, firstE);
        if (compareResult < 0) {
            org.assertj.core.api.Assertions.assertThat(compareResult)
                    .as("No %s order!", comparator.isAscending() ? "ascending" : "descending")
                    .isGreaterThanOrEqualTo(0);
        }
    }

    public static <T> void sort(final IComparator<T> comparator, final List<? extends T> list) {
        Collections.sort(list, comparator);
    }

}
