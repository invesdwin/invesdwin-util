package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamProduct<E extends AFDouble<E>>
        implements IStreamAlgorithm<E, Void>, ISerializableValueObject {

    private double logSum = 0D;
    private final double valueAdjustmentAddition;
    private final E converter;
    private E product;
    private Double productDouble;
    private int count;

    public FDoubleStreamProduct(final E converter) {
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
        product = null;
        productDouble = null;
        return null;
    }

    public E getProduct() {
        if (product == null) {
            product = converter.fromDefaultValue(getProductDouble());
        }
        return product;
    }

    public double getProductDouble() {
        if (productDouble == null) {
            productDouble = calculateProductDouble();
        }
        return productDouble;
    }

    private double calculateProductDouble() {
        final double doubleResult;
        if (count == 0) {
            doubleResult = 0D;
        } else {
            doubleResult = Math.exp(logSum);
        }
        if (doubleResult == 0D) {
            return 0D;
        }
        return doubleResult - valueAdjustmentAddition;
    }

}
