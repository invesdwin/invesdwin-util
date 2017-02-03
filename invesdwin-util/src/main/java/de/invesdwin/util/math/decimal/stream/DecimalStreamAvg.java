package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamAvg<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Void> {

    private int count = 0;
    private double sum = 0;
    private final E converter;

    public DecimalStreamAvg(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        count++;
        sum += value.getDefaultValue().doubleValueRaw();
        return null;
    }

    public E getAvg() {
        return converter.fromDefaultValue(new Decimal(sum / count));
    }

}
