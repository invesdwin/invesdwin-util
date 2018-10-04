package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class NumberStreamMax<E extends Number> implements IStreamAlgorithm<E, Double> {

    private Double max;

    @Override
    public Double process(final E value) {
        max = Doubles.max(max, (Double) value.doubleValue());
        return max;
    }

    public Double getMax() {
        return max;
    }

}
