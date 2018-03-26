package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamAvg<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private int count = 0;
    private double sum = 0;
    private final E converter;

    public DecimalStreamAvg(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        count++;
        if (value != null) {
            sum += value.getDefaultValue().doubleValueRaw();
        }
        return null;
    }

    public E getAvg() {
        final double doubleResult;
        if (count == 0) {
            doubleResult = 0D;
        } else {
            doubleResult = sum / count;
        }
        return converter.fromDefaultValue(new Decimal(doubleResult));
    }

    public E getSum() {
        return converter.fromDefaultValue(new Decimal(sum));
    }

    public int getCount() {
        return count;
    }

}
