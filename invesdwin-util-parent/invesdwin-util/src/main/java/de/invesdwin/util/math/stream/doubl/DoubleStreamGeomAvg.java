package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamGeomAvg implements IDoubleStreamAlgorithm {

    private final double valueAdjustmentAddition;
    private double logSum = 0D;
    private int count;

    public DoubleStreamGeomAvg() {
        final double valueAdjustmentAddition = getValueAdjustmentAddition();
        if (Doubles.isNaN(valueAdjustmentAddition)) {
            this.valueAdjustmentAddition = 0;
        } else {
            this.valueAdjustmentAddition = valueAdjustmentAddition;
        }
    }

    protected double getValueAdjustmentAddition() {
        return 0D;
    }

    @Override
    public double process(final double value) {
        final double doubleValue = value;
        final double adjValue = doubleValue + valueAdjustmentAddition;
        if (adjValue > 0D) {
            /*
             * though this is not nice, when trying to calculate the compoundDailyGrowthRate from a detrended equity
             * curve that goes negative, this is needed
             */
            logSum += Math.log(adjValue);
        }
        count++;
        return Double.NaN;
    }

    public double getGeomAvg() {
        if (count == 0) {
            return 0D;
        } else {
            final double result = Math.exp(logSum / count);
            final double geomAvg = result - valueAdjustmentAddition;
            return geomAvg;
        }
    }

    public void reset() {
        logSum = 0D;
        count = 0;
    }

}
