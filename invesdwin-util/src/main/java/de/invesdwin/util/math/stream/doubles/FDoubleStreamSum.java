package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamSum<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private double sum = 0;
    private final E converter;

    public FDoubleStreamSum(final E converter) {
        this.converter = converter;
    }

    @Override
    public Void process(final E value) {
        if (value != null) {
            sum += value.getDefaultValue();
        }
        return null;
    }

    public E getSum() {
        return converter.fromDefaultValue(sum);
    }

}
