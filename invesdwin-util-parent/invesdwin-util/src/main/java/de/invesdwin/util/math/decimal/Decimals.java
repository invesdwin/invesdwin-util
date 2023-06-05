package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Decimals {

    private Decimals() {}

    public static <T extends ADecimal<T>> T add(final T amount1, final T amount2) {
        if (amount1 == null) {
            return amount2;
        } else {
            return amount1.add(amount2);
        }
    }

    public static <T extends ADecimal<T>> T max(final T first, final T second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return first.orHigher(second);
        }
    }

    @SafeVarargs
    public static <T extends ADecimal<T>> T max(final T... array) {
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            max = max(max, array[i]);
        }
        return max;
    }

    public static <T extends ADecimal<T>> T min(final T first, final T second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return first.orLower(second);
        }
    }

    @SafeVarargs
    public static <T extends ADecimal<T>> T min(final T... array) {
        T min = array[0];
        for (int i = 1; i < array.length; i++) {
            min = min(min, array[i]);
        }
        return min;
    }

}
