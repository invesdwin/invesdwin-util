package de.invesdwin.util.math.stream.doubl.zigzag;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;

@Immutable
public enum PriceDirection {
    /**
     * This is an X for PointAndFigure charts.
     */
    RISING("Rising") {
        @Override
        public double getPosition() {
            return 1;
        }
    },
    /**
     * This is an Y for PointAndFigure charts.
     */
    FALLING("Falling") {
        @Override
        public double getPosition() {
            return -1;
        }
    },
    UNCHANGED("Unchanged") {
        @Override
        public double getPosition() {
            return 0;
        }
    };

    private String text;

    PriceDirection(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static <T extends ADecimal<T>> PriceDirection fromCompare(final T startPrice, final T endPrice) {
        if (startPrice == null || endPrice == null) {
            return UNCHANGED;
        }
        return fromCompare(startPrice.getDefaultValue(), endPrice.getDefaultValue());
    }

    public static PriceDirection fromCompare(final double startPrice, final double endPrice) {
        final int compare = Doubles.compare(endPrice, startPrice);
        switch (compare) {
        case -1:
            return FALLING;
        case 0:
            return UNCHANGED;
        case 1:
            return RISING;
        default:
            throw new IllegalArgumentException("Invalid compareTo result: " + compare);
        }
    }

    public abstract double getPosition();

}
