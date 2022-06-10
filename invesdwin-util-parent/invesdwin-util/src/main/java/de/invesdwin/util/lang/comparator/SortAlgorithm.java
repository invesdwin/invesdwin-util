package de.invesdwin.util.lang.comparator;

import java.util.Comparator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum SortAlgorithm implements ISortAlgorithm {
    TIMSORT {

        @Override
        public <T> void sort(final List<? extends T> list, final Comparator<? super T> comparator) {
            Comparators.sort(list, comparator);
        }

        @Override
        public <T extends Comparable<? super T>> void sort(final List<? extends T> list) {
            Comparators.sort(list);
        }

    },
    MERGESORT {
        @Override
        public <T> void sort(final List<? extends T> list, final Comparator<? super T> comparator) {
            Comparators.mergeSort(list, comparator);
        }

        @Override
        public <T extends Comparable<? super T>> void sort(final List<? extends T> list) {
            Comparators.mergeSort(list);
        }
    },
    BUBBLESORT {
        @Override
        public <T> void sort(final List<? extends T> list, final Comparator<? super T> comparator) {
            Comparators.bubbleSort(list, comparator);
        }

        @Override
        public <T extends Comparable<? super T>> void sort(final List<? extends T> list) {
            Comparators.bubbleSort(list);
        }
    };

    public static final SortAlgorithm DEFAULT = TIMSORT;

    @Override
    public abstract <T> void sort(List<? extends T> list, Comparator<? super T> comparator);

    @Override
    public abstract <T extends Comparable<? super T>> void sort(List<? extends T> list);

}
