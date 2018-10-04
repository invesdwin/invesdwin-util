package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamGeomAvg<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

    private double logSum = 0D;
    private int count;
    private final double valueAdjustmentAddition;
    private final E converter;
    private E geomAvg;

    public FDoubleStreamGeomAvg(final E converter) {
        this.converter = converter;
        final E valueAdjustmentAddition = getValueAdjustmentAddition();
        if (valueAdjustmentAddition == null) {
            this.valueAdjustmentAddition = 0;
        } else {
            this.valueAdjustmentAddition = valueAdjustmentAddition.getDefaultValue();
        }
    }

    protected E getValueAdjustmentAddition() {
        return null;
    }

    @Override
    public Void process(final E value) {
        final double doubleValue = value.getDefaultValue();
        final double adjValue = doubleValue + valueAdjustmentAddition;
        if (adjValue > 0D) {
            /*
             * though this is not nice, when trying to calculate the compoundDailyGrowthRate from a detrended equity
             * curve that goes negative, this is needed
             */
            logSum += Math.log(adjValue);
        }
        count++;
        geomAvg = null;
        return null;
    }

    public E getGeomAvg() {
        if (geomAvg == null) {
            geomAvg = calculateGeomAvg();
        }
        return geomAvg;
    }

    private E calculateGeomAvg() {
        if (count == 0) {
            return converter.zero();
        } else {
            final double result = Math.exp(logSum / count);
            final E geomAvg = converter.fromDefaultValue(result - valueAdjustmentAddition);
            return geomAvg;
        }
    }

}
