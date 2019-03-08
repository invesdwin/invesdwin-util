package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class DecimalStreamProfitFactor<E extends ADecimal<E>> implements IStreamAlgorithm<E, Void> {

    private double profitSum;
    private double lossSum;

    @Override
    public Void process(final E value) {
        final double doubleValue = value.doubleValue();
        if (doubleValue > 0) {
            profitSum += doubleValue;
        } else {
            lossSum += doubleValue;
        }
        return null;
    }

    public Percent getProfitFactor() {
        return new Percent(new Decimal(profitSum), new Decimal(Doubles.abs(lossSum)));
    }

}
