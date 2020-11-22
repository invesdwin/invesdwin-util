package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DoubleStreamPerformanceRateFromHPRs
        implements IDoubleStreamAlgorithm, ISerializableValueObject {

    private final double initialPerformance = getInitialPerformanceRate();
    private final DoubleStreamProduct product = new DoubleStreamProduct() {
        @Override
        protected double getValueAdjustmentAddition() {
            return Percent.ONE_HUNDRED_PERCENT.getRate();
        }
    };

    @Override
    public double process(final double holdingPeriodReturnRate) {
        //improve accuracy by using log sum instead of multiplication directly for the HPRs
        product.process(holdingPeriodReturnRate);
        return Double.NaN;
    }

    public double getPerformanceRate() {
        final double multiplier = product.getProduct();
        final double performance = initialPerformance * (multiplier + 1D);
        return performance;
    }

    public double getInitialPerformanceRate() {
        return 1D;
    }

}
