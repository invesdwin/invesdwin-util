package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DoubleStreamProfitFactor implements IDoubleStreamAlgorithm {

    private double profitSum;
    private double lossSum;

    @Override
    public double process(final double value) {
        if (value > 0D) {
            profitSum += value;
        } else {
            lossSum += value;
        }
        return Double.NaN;
    }

    public Percent getProfitFactor() {
        return new Percent(profitSum, Doubles.abs(lossSum));
    }

}
