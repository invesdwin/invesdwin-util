package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;
import de.invesdwin.util.math.stream.number.NumberStreamAvg;

@NotThreadSafe
public class FDoubleStreamAvg<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private final NumberStreamAvg<Double> avg = new NumberStreamAvg<>();
    private final E converter;

    public FDoubleStreamAvg(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        avg.process(value.getDefaultValue());
        return null;
    }

    public E getAvg() {
        final double doubleResult = avg.getAvg();
        return converter.fromDefaultValue(doubleResult);
    }

    public long getCount() {
        return avg.getCount();
    }

}
