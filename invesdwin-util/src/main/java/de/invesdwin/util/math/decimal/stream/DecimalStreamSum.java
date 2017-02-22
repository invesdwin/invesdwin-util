package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamSum<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Void> {

    private double sum = 0;
    private final E converter;

    public DecimalStreamSum(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        if (value != null) {
            sum += value.getDefaultValue().doubleValueRaw();
        }
        return null;
    }

    public E getSum() {
        return converter.fromDefaultValue(new Decimal(sum));
    }

}
