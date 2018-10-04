package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

@NotThreadSafe
public class FDoubleStreamProfitFactor<E extends AFDouble<E>> implements IStreamAlgorithm<E, Void> {

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
