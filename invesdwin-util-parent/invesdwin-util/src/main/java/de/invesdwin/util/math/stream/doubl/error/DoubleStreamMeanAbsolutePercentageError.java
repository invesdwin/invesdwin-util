package de.invesdwin.util.math.stream.doubl.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.stream.doubl.DoubleStreamAvg;
import de.invesdwin.util.math.stream.doubl.IDoubleDoubleStreamAlgorithm;

/**
 * also known as mean absolute percentage deviation (MAPD)
 * 
 * MAPE = sum(|(actual-prediction)/actual|)/n
 *
 * A lower value is better.
 */
@NotThreadSafe
public class DoubleStreamMeanAbsolutePercentageError implements IDoubleDoubleStreamAlgorithm {

    private final DoubleStreamAvg avg = new DoubleStreamAvg();

    @Override
    public double process(final double actual, final double prediction) {
        avg.process(Doubles.abs(Doubles.divide(actual - prediction, actual)));
        return Double.NaN;
    }

    public Percent getMeanAbsolutePercentageError() {
        return new Percent(getMeanAbsolutePercentageErrorRate(), PercentScale.RATE);
    }

    public double getMeanAbsolutePercentageErrorRate() {
        return avg.getAvg();
    }

    public double getMeanAbsolutePercentageErrorPercent() {
        return getMeanAbsolutePercentageErrorRate() * 100D;
    }

}
