package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

@NotThreadSafe
public class DoubleStreamMeanError implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamAvg avg = new DoubleStreamAvg();

    @Override
    public double process(final double actual, final double prediction) {
        final double error = DoubleStreamError.error(actual, prediction);
        avg.process(error);
        return Double.NaN;
    }

    public double getMeanError() {
        return avg.getAvg();
    }

}
