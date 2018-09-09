package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamAvg;

@NotThreadSafe
public class DecimalStreamAvg<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamAvg<Double> avg = new NumberStreamAvg<>();
    private final E converter;

    public DecimalStreamAvg(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        avg.process(value.getDefaultValue().doubleValueRaw());
        return null;
    }

    public E getAvg() {
        final double doubleResult = avg.getAvg();
        return converter.fromDefaultValue(new Decimal(doubleResult));
    }

    public long getCount() {
        return avg.getCount();
    }

}
