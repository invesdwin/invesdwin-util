package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

@NotThreadSafe
public class DoubleStreamStatisticalErrors implements IDoubleDoubleStreamAlgorithm, IStatisticalErrors {

    private final DoubleStreamMeanError meanError = new DoubleStreamMeanError();
    private final DoubleStreamMeanAbsoluteError meanAbsoluteError = new DoubleStreamMeanAbsoluteError();
    private final DoubleStreamMeanAbsolutePercentageError meanAbsolutePercentageError = new DoubleStreamMeanAbsolutePercentageError();
    private final DoubleStreamMeanAbsoluteDeviation meanAbsoluteDeviation = new DoubleStreamMeanAbsoluteDeviation();
    private final DoubleStreamMedianAbsoluteDeviation medianAbsoluteDeviation = new DoubleStreamMedianAbsoluteDeviation();
    private final DoubleStreamRootMeanSquaredError rootMeanSquaredError = new DoubleStreamRootMeanSquaredError();
    private final DoubleStreamRootMeanSquaredLogError rootMeanSquaredLogError = new DoubleStreamRootMeanSquaredLogError();

    @Override
    public double process(final double prediction, final double actual) {
        meanError.process(prediction, actual);
        meanAbsoluteError.process(prediction, actual);
        meanAbsolutePercentageError.process(prediction, actual);
        meanAbsoluteDeviation.process(prediction, actual);
        medianAbsoluteDeviation.process(prediction, actual);
        rootMeanSquaredError.process(prediction, actual);
        rootMeanSquaredLogError.process(prediction, actual);
        return Double.NaN;
    }

    @Override
    public double getMeanAbsoluteDeviation() {
        return meanAbsoluteDeviation.getMeanAbsoluteDeviation();
    }

    @Override
    public double getMeanAbsoluteError() {
        return meanAbsoluteError.getMeanAbsoluteError();
    }

    @Override
    public double getMeanAbsolutePercentageErrorRate() {
        return meanAbsolutePercentageError.getMeanAbsolutePercentageErrorRate();
    }

    @Override
    public Percent getMeanAbsolutePercentageError() {
        return meanAbsolutePercentageError.getMeanAbsolutePercentageError();
    }

    @Override
    public double getMeanError() {
        return meanError.getMeanError();
    }

    @Override
    public double getMedianAbsoluteDeviation() {
        return meanAbsoluteDeviation.getMeanAbsoluteDeviation();
    }

    @Override
    public double getMeanSquaredError() {
        return rootMeanSquaredError.getMeanSquaredError();
    }

    @Override
    public double getRootMeanSquaredError() {
        return rootMeanSquaredError.getRootMeanSquaredError();
    }

    @Override
    public double getMeanSquaredLogError() {
        return rootMeanSquaredLogError.getMeanSquaredLogError();
    }

    @Override
    public double getRootMeanSquaredLogError() {
        return rootMeanSquaredLogError.getRootMeanSquaredLogError();
    }

}
