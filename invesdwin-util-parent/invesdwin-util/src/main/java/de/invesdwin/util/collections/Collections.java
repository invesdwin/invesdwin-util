package de.invesdwin.util.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.ACollectionsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.ACollectionsStaticFacade", targets = {
        java.util.Collections.class }, filterSeeMethodSignatures = { "java.util.Collections#min(java.util.Collection)",
                "java.util.Collections#max(java.util.Collection)" })
public class Collections extends ACollectionsStaticFacade {

    private static final int INSERTIONSORT_THRESHOLD = 7;

    /**
     * https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_introduction
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final Collection<? extends T> col, final Class<T> type) {
        if (col == null) {
            return null;
        } else {
            try {
                final T[] newArray = (T[]) Array.newInstance(type, 0);
                return col.toArray(newArray);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> void sortNonTransitive(final List<T> col) {
        final Object[] a = col.toArray();
        sortNonTransitive(a);
        final ListIterator<T> i = col.listIterator();
        for (final Object e : a) {
            i.next();
            i.set((T) e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void sortNonTransitive(final List<T> col, final Comparator<? super T> c) {
        final Object[] a = col.toArray();
        sortNonTransitive(a, (Comparator) c);
        final ListIterator<T> i = col.listIterator();
        for (final Object e : a) {
            i.next();
            i.set((T) e);
        }
    }

    public static void sortNonTransitive(final Object[] a) {
        legacyMergeSort(a);
    }

    /** To be removed in a future release. */
    private static void legacyMergeSort(final Object[] a) {
        final Object[] aux = a.clone();
        mergeSort(aux, a, 0, a.length, 0);
    }

    public static void sortNonTransitive(final Object[] a, final int fromIndex, final int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        legacyMergeSort(a, fromIndex, toIndex);
    }

    public static <T> void sortNonTransitive(final T[] a, final Comparator<? super T> c) {
        if (c == null) {
            sortNonTransitive(a);
        } else {
            legacyMergeSort(a, c);
        }
    }

    public static <T> void sortNonTransitive(final T[] a, final int fromIndex, final int toIndex,
            final Comparator<? super T> c) {
        if (c == null) {
            sortNonTransitive(a, fromIndex, toIndex);
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
