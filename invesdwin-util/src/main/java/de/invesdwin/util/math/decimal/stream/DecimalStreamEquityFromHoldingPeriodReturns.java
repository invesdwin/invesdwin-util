package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamEquityFromHoldingPeriodReturns implements IDecimalStreamAlgorithm<Percent, Decimal> {

    private Decimal equity = getInitialEquity();

    @Override
    public Decimal process(final Percent holdingPeriodReturn) {
        equity = equity.multiply(holdingPeriodReturn.getRate().add(Decimal.ONE));
        return equity;
    }

    public Decimal getInitialEquity() {
        return Decimal.ONE;
    }

}
