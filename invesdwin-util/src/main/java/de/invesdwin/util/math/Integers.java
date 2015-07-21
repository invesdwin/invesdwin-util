package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.ADelegateComparator;

@Immutable
public final class Integers {

    public static final ADelegateComparator<Integer> COMPARATOR = new ADelegateComparator<Integer>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Integer e) {
            return e;
        }
    };

    private Integers() {}

    public static Integer max(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Integer min(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

}
