package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * (Root) Mean Squared Log Error: MSLE=mean((log(p+1)−log(y+1))^2)
 * 
 * https://datascience.stackexchange.com/questions/42760/mad-vs-rmse-vs-mae-vs-msle-vs-r%C2%B2-when-to-use-which
 *
 */
@NotThreadSafe
public class DoubleStreamRootMeanSquaredLogError implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamAvg avg = new DoubleStreamAvg();

    @Override
    public double process(final double actual, final double prediction) {
        final double difference = Doubles.log(prediction + 1) - Doubles.log(actual + 1);
        avg.process(difference * difference);
        return Double.NaN;
    }

    public double getMeanSquaredLogError() {
        return avg.getAvg();
    }

    /**
     * https://medium.com/analytics-vidhya/root-mean-square-log-error-rmse-vs-rmlse-935c6cc1802a
     */
    public double getRootMeanSquaredLogError() {
        return Doubles.sqrt(getMeanSquaredLogError());
    }

}
