package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

@NotThreadSafe
public class DoubleStreamRootMeanSquaredError implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamAvg avg = new DoubleStreamAvg();

    @Override
    public double process(final double actual, final double prediction) {
        final double error = DoubleStreamError.error(actual, prediction);
        avg.process(error * error);
        return Double.NaN;
    }

    /**
     * Mean Squared Error: MSE=mean(e^2)
     */
    public double getMeanSquaredError() {
        return avg.getAvg();
    }

    /**
     * Root Mean Squared Error: RMSE=sqrt(mean(e^2)).
     */
    public double getRootMeanSquaredError() {
        return Doubles.sqrt(getMeanSquaredError());
    }

}
