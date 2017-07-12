package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamProduct<E extends ADecimal<E>>
        implements IDecimalStreamAlgorithm<E, Void>, ISerializableValueObject {

    private double logSum = 0D;
    private final double valueAdjustmentAddition;
    private final E converter;
    private E product;
    private int count;

    public DecimalStreamProduct(final E converter) {
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
        product = null;
        return null;
    }

    public E getProduct() {
        if (product == null) {
            product = calculateProduct();
        }
        return product;
    }

    private E calculateProduct() {
        final double doubleResult;
        if (count == 0) {
            doubleResult = 0D;
        } else {
            doubleResult = Math.exp(logSum);
        }
        final Decimal result = new Decimal(doubleResult);
        if (result.isZero()) {
            return converter.zero();
        }
        final E geomAvg = converter.fromDefaultValue(result.subtract(new Decimal(valueAdjustmentAddition)));
        return geomAvg;
    }

}
