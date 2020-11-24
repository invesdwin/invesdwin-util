package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DoubleStreamDrawdownRate implements IDoubleStreamAlgorithm {

    private double maxEquity;

    public DoubleStreamDrawdownRate(final double initialEquity) {
        this.maxEquity = initialEquity;
    }

    @Override
    public double process(final double equity) {
        if (equity >= maxEquity) {
            maxEquity = equity;
            return 0D;
        } else {
            final double drawdown = maxEquity - equity;
            //on multimarket strategies the drawdown can actually become positive for orders
            final double drawdownRate = Percent.newRate(drawdown, maxEquity);
            if (drawdownRate < 0D) {
                return 0D;
            } else {
                return drawdownRate;
            }
        }
    }

}
