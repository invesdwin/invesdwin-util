package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class NumberStreamSum<E extends Number> implements IStreamAlgorithm<E, Double> {

    private double sum = 0;

    @Override
    public Double process(final E value) {
        if (value != null) {
            sum += value.doubleValue();
        }
        return sum;
    }

    public double getSum() {
        return sum;
    }

}
