package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamPerformanceFromHoldingPeriodReturns implements IDecimalStreamAlgorithm<Percent, Decimal> {

    private Decimal performance = getInitialValue();

    @Override
    public Decimal process(final Percent holdingPeriodReturn) {
        performance = performance.multiply(holdingPeriodReturn.getRate().add(Decimal.ONE));
        return performance;
    }

    public Decimal getPerformance() {
        return performance;
    }

    public Decimal getInitialValue() {
        return Decimal.ONE;
    }

}
