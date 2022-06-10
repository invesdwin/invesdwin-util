package de.invesdwin.util.lang.comparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Comparators {

    public static final IComparator<Comparable<Object>> COMPARATOR = IComparator.getDefaultInstance();
    private static final int INSERTIONSORT_THRESHOLD = 7;

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

    public static <T extends Comparable<? super T>> void sort(final List<? extends T> list) {
        Collections.sort(list);
    }

    public static <T> void sort(final List<? extends T> list, final Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void bubbleSort(final List<T> a) {
        for (int max = a.size() - 1; max > 0; max--) {
            boolean swapped = false;
            for (int i = 0; i < max; i++) {
                final T left = a.get(i);
                final T right = a.get(i + 1);
                if (((Comparable) left).compareTo(right) > 0) {
                    a.set(i + 1, left);
                    a.set(i, right);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public static <T> void bubbleSort(final List<T> a, final Comparator<? super T> c) {
        for (int max = a.size() - 1; max > 0; max--) {
            boolean swapped = false;
            for (int i = 0; i < max; i++) {
                final T left = a.get(i);
                final T right = a.get(i + 1);
                final int compare = c.compare(left, right);
                if (compare > 0) {
                    a.set(i + 1, left);
                    a.set(i, right);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void bubbleSort(final T[] a) {
        for (int max = a.length - 1; max > 0; max--) {
            boolean swapped = false;
            for (int i = 0; i < max; i++) {
                final T left = a[i];
                final T right = a[i + 1];
                if (((Comparable) left).compareTo(right) > 0) {
                    a[i + 1] = left;
                    a[i] = right;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * https://www.happycoders.eu/de/algorithmen/bubble-sort/
     */
    public static <T> void bubbleSort(final T[] a, final Comparator<? super T> c) {
        for (int max = a.length - 1; max > 0; max--) {
            boolean swapped = false;
            for (int i = 0; i < max; i++) {
                final T left = a[i];
                final T right = a[i + 1];
                final int compare = c.compare(left, right);
                if (compare > 0) {
                    a[i + 1] = left;
                    a[i] = right;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> void mergeSort(final List<T> col) {
        final Object[] a = col.toArray();
        mergeSort(a);
        final ListIterator<T> i = col.listIterator();
        for (final Object e : a) {
            i.next();
            i.set((T) e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void mergeSort(final List<T> col, final Comparator<? super T> c) {
        final Object[] a = col.toArray();
        mergeSort(a, (Comparator) c);
        final ListIterator<T> i = col.listIterator();
        for (final Object e : a) {
            i.next();
            i.set((T) e);
        }
    }

    public static void mergeSort(final Object[] a) {
        legacyMergeSort(a);
    }

    /** To be removed in a future release. */
    private static void legacyMergeSort(final Object[] a) {
        final Object[] aux = a.clone();
        mergeSort(aux, a, 0, a.length, 0);
    }

    public static void mergeSort(final Object[] a, final int fromIndex, final int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        legacyMergeSort(a, fromIndex, toIndex);
    }

    public static <T> void mergeSort(final T[] a, final Comparator<? super T> c) {
        if (c == null) {
            mergeSort(a);
        } else {
            legacyMergeSort(a, c);
        }
    }

    public static <T> void mergeSort(final T[] a, final int fromIndex, final int toIndex,
            final Comparator<? super T> c) {
        if (c == null) {
            mergeSort(a, fromIndex, toIndex);
        } else {
            rangeCheck(a.length, fromIndex, toIndex);
            legacyMergeSort(a, fromIndex, toIndex, c);
        }
    }

    private static void legacyMergeSort(final Object[] a, final int fromIndex, final int toIndex) {
        final Object[] aux = Arrays.copyOfRange(a, fromIndex, toIndex);
        mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void mergeSort(final Object[] src, final Object[] dest, final int pLow, final int pHigh,
            final int pOff) {
        int low = pLow;
        int high = pHigh;
        final int off = pOff;
        final int length = high - low;

        // Insertion sort on smallest arrays
        if (length < INSERTIONSORT_THRESHOLD) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && ((Comparable) dest[j - 1]).compareTo(dest[j]) > 0; j--) {
                    swap(dest, j, j - 1);
                }
            }
            return;
        }

        // Recursively sort halves of dest into src
        final int destLow = low;
        final int destHigh = high;
        low += off;
        high += off;
        final int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off);
        mergeSort(dest, src, mid, high, -off);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (((Comparable) src[mid - 1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && ((Comparable) src[p]).compareTo(src[q]) <= 0) {
                dest[i] = src[p++];
            } else {
                dest[i] = src[q++];
            }
        }
    }

    private static <T> void legacyMergeSort(final T[] a, final Comparator<? super T> c) {
        final T[] aux = a.clone();
        if (c == null) {
            mergeSort(aux, a, 0, a.length, 0);
        } else {
            mergeSort(aux, a, 0, a.length, 0, c);
        }
    }

    private static <T> void legacyMergeSort(final T[] a, final int fromIndex, final int toIndex,
            final Comparator<? super T> c) {
        final T[] aux = Arrays.copyOfRange(a, fromIndex, toIndex);
        if (c == null) {
            mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
        } else {
            mergeSort(aux, a, fromIndex, toIndex, -fromIndex, c);
        }
    }

    private static void swap(final Object[] x, final int a, final int b) {
        final Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void mergeSort(final Object[] src, final Object[] dest, final int pLow, final int pHigh,
            final int pOff, final Comparator c) {
        int low = pLow;
        int high = pHigh;
        final int off = pOff;
        final int length = high - low;

        // Insertion sort on smallest arrays
        if (length < INSERTIONSORT_THRESHOLD) {
            for (int i = low; i < high; i++) {
                for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--) {
                    swap(dest, j, j - 1);
                }
            }
            return;
        }

        // Recursively sort halves of dest into src
        final int destLow = low;
        final int destHigh = high;
        low += off;
        high += off;
        final int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off, c);
        mergeSort(dest, src, mid, high, -off, c);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (c.compare(src[mid - 1], src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0) {
                dest[i] = src[p++];
            } else {
                dest[i] = src[q++];
            }
        }
    }

    private static void rangeCheck(final int arrayLength, final int fromIndex, final int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

}
