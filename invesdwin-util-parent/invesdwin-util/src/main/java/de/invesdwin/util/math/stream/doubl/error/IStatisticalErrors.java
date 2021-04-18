package de.invesdwin.util.math.stream.doubl.error;

import de.invesdwin.util.math.decimal.scaled.Percent;

public interface IStatisticalErrors {

    double getMeanAbsoluteDeviation();

    double getMeanAbsoluteError();

    Percent getMeanAbsolutePercentageError();

    double getMeanAbsolutePercentageErrorRate();

    double getMeanAbsolutePercentageErrorPercent();

    double getMeanError();

    double getMedianAbsoluteDeviation();

    double getMeanSquaredError();

    double getRootMeanSquaredError();

    double getMeanSquaredLogError();

    double getRootMeanSquaredLogError();

}
