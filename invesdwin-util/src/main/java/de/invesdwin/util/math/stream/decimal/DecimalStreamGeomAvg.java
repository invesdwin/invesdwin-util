package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamGeomAvg<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private double logSum = 0D;
    private int count;
    private final double valueAdjustmentAddition;
    private final E converter;
    private E geomAvg;

    public DecimalStreamGeomAvg(final E converter) {
        this.converter = converter;
        final E valueAdjustmentAddition = getValueAdjustmentAddition();
        if (valueAdjustmentAddition == null) {
            this.valueAdjustmentAddition = 0;
        } else {
            this.valueAdjustmentAddition = valueAdjustmentAddition.getDefaultValue().doubleValueRaw();
        }
    }

    protected E getValueAdjustmentAddition() {
        return null;
    }

    @Override
    public Void process(final E value) {
        final double doubleValue = value.getDefaultValue().doubleValueRaw();
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
        final double doubleResult;
        if (count == 0) {
            doubleResult = 0D;
        } else {
            doubleResult = Math.exp(logSum / count);
        }
        final Decimal result = new Decimal(doubleResult);
        if (result.isZero()) {
            return converter.zero();
        }
        final E geomAvg = converter.fromDefaultValue(result.subtract(new Decimal(valueAdjustmentAddition)));
        return geomAvg;
    }

}
