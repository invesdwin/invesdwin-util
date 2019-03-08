package de.invesdwin.util.math.stream.number;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
class OverflowingNumberStreamAvg<E extends Number> implements IStreamAlgorithm<E, Void> {

    private int count = 0;
    private double sum = 0;

    @Override
    public Void process(final E value) {
        count++;
        if (value != null) {
            sum += value.doubleValue();
        }
        return null;
    }

    public double getAvg() {
        final double doubleResult;
        if (count == 0) {
            doubleResult = 0D;
        } else {
            doubleResult = sum / count;
        }
        return doubleResult;
    }

}