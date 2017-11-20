package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamPerformanceFromHoldingPeriodReturns
        implements IDecimalStreamAlgorithm<Percent, Void>, ISerializableValueObject {

    private Decimal performance = getInitialValue();
    private Double performanceDouble = performance.doubleValueRaw();
    private final double initialPerformance = performanceDouble;
    private final DecimalStreamProduct<Decimal> product = new DecimalStreamProduct<Decimal>(Decimal.ZERO) {
        @Override
        protected Decimal getValueAdjustmentAddition() {
            return Percent.ONE_HUNDRED_PERCENT.getRate();
        }
    };

    @Override
    public Void process(final Percent holdingPeriodReturn) {
        //improve accuracy by using log sum instead of multiplication directly for the HPRs
        product.process(holdingPeriodReturn.getRate());
        performance = null;
        performanceDouble = null;
        return null;
    }

    public Decimal getPerformance() {
        if (performance == null) {
            performance = new Decimal(getPerformanceDouble());
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

    public Decimal getInitialValue() {
        return Decimal.ONE;
    }

}
