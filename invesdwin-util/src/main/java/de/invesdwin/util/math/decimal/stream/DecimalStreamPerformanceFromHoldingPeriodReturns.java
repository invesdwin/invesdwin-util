package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamPerformanceFromHoldingPeriodReturns
        implements IDecimalStreamAlgorithm<Percent, Void>, ISerializableValueObject {

    private final Decimal initialPerformance = getInitialValue();
    private Decimal performance = initialPerformance;
    private final DecimalStreamProduct<Percent> product = new DecimalStreamProduct<Percent>(Percent.ZERO_PERCENT) {
        @Override
        protected Percent getValueAdjustmentAddition() {
            return Percent.ONE_HUNDRED_PERCENT;
        }
    };

    @Override
    public Void process(final Percent holdingPeriodReturn) {
        //improve accuracy by using log sum instead of multiplication directly for the HPRs
        product.process(holdingPeriodReturn);
        performance = null;
        return null;
    }

    public Decimal getPerformance() {
        if (performance == null) {
            final Percent multiplier = product.getProduct();
            performance = initialPerformance.multiply(multiplier.getRate().add(Decimal.ONE));
        }
        return performance;
    }

    public Decimal getInitialValue() {
        return Decimal.ONE;
    }

}
