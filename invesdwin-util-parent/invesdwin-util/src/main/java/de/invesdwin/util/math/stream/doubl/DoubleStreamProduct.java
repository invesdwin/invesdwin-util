package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class DoubleStreamProduct implements IDoubleStreamAlgorithm, ISerializableValueObject {

    private double logSum = 0D;
    private final double valueAdjustmentAddition;
    private int count;

    public DoubleStreamProduct() {
        final double valueAdjustmentAddition = getValueAdjustmentAddition();
        if (Doubles.isNaN(valueAdjustmentAddition)) {
            this.valueAdjustmentAddition = 0D;
        } else {
            this.valueAdjustmentAddition = valueAdjustmentAddition;
        }
    }

    protected double getValueAdjustmentAddition() {
        return 0D;
    }

    @Override
    public double process(final double value) {
        final double adjValue = value + valueAdjustmentAddition;
        logSum += Doubles.log(adjValue);
        count++;
        return Double.NaN;
    }

    public double getProduct() {
        if (count == 0) {
            return 0D;
        }
        final double result = Math.exp(logSum);
        if (result == 0D) {
            return 0D;
        }
        return result - valueAdjustmentAddition;
    }

}
