package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DoubleStreamSum<E extends Number> implements IStreamAlgorithm<E, Double> {

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
