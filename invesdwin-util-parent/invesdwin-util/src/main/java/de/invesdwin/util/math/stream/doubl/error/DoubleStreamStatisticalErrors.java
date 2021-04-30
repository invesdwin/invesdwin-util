package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

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
    public double process(final double actual, final double prediction) {
        meanError.process(actual, prediction);
        meanAbsoluteError.process(actual, prediction);
        meanAbsolutePercentageError.process(actual, prediction);
        meanAbsoluteDeviation.process(actual, prediction);
        medianAbsoluteDeviation.process(actual, prediction);
        rootMeanSquaredError.process(actual, prediction);
        rootMeanSquaredLogError.process(actual, prediction);
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
    public double getMeanError() {
        return meanError.getMeanError();
    }

    @Override
    public double getMedianAbsoluteDeviation() {
        return medianAbsoluteDeviation.getMedianAbsoluteDeviation();
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
