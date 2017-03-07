package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DecimalStreamProfitFactor<E extends ADecimal<E>> implements IDecimalStreamAlgorithm<E, Void> {

    private double profitSum;
    private double lossSum;

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValueRaw();
        if (doubleValue > 0) {
            profitSum += doubleValue;
        } else {
            lossSum += doubleValue;
        }
        return null;
    }

    public Percent getProfitFactor() {
        return new Percent(new Decimal(profitSum), new Decimal(Math.abs(lossSum)));
    }

}
