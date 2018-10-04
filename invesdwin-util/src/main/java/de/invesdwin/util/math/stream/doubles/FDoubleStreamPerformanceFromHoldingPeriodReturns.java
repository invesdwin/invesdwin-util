package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.doubles.scaled.FPercent;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamPerformanceFromHoldingPeriodReturns
        implements IStreamAlgorithm<FPercent, Void>, ISerializableValueObject {

    private FDouble performance = getInitialValue();
    private Double performanceDouble = performance.doubleValue();
    private final double initialPerformance = performanceDouble;
    private final FDoubleStreamProduct<FDouble> product = new FDoubleStreamProduct<FDouble>(FDouble.ZERO) {
        @Override
        protected FDouble getValueAdjustmentAddition() {
            return FDouble.valueOf(FPercent.ONE_HUNDRED_PERCENT.getRate());
        }
    };

    @Override
    public Void process(final FPercent holdingPeriodReturn) {
        //improve accuracy by using log sum instead of multiplication directly for the HPRs
        product.process(FDouble.valueOf(holdingPeriodReturn.getRate()));
        performance = null;
        performanceDouble = null;
        return null;
    }

    public FDouble getPerformance() {
        if (performance == null) {
            performance = new FDouble(getPerformanceDouble());
        }
        return performance;
    }

    public double getPerformanceDouble() {
        if (performanceDouble == null) {
            performanceDouble = calculatePerformanceDouble();
        }
        return performanceDouble;
    }

    private double calculatePerformanceDouble() {
        final double multiplier = product.getProductDouble();
        final double performanceDouble = initialPerformance * (multiplier + 1D);
        return performanceDouble;
    }

    public FDouble getInitialValue() {
        return FDouble.ONE;
    }

}
