package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * https://datascience.stackexchange.com/questions/42760/mad-vs-rmse-vs-mae-vs-msle-vs-r%C2%B2-when-to-use-which
 * 
 * Mean Absolute Error: MAE=mean(|e|).
 */
@NotThreadSafe
public class DoubleStreamMeanAbsoluteError implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamAvg avg = new DoubleStreamAvg();

    @Override
    public double process(final double prediction, final double actual) {
        final double error = DoubleStreamError.error(prediction, actual);
        avg.process(Doubles.abs(error));
        return Double.NaN;
    }

    public double getMeanAbsoluteError() {
        return avg.getAvg();
    }

}
