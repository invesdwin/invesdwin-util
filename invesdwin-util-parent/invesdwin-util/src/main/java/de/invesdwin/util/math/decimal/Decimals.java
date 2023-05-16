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

}
