package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class DoubleStreamInitialDrawdownRelativeRate implements IDoubleStreamAlgorithm {

    private double initialEquity;
    private double maxEquity;

    public DoubleStreamInitialDrawdownRelativeRate(final double initialEquity) {
        this.initialEquity = initialEquity;
        this.maxEquity = initialEquity;
    }

    public double getMaxEquity() {
        return maxEquity;
    }

    @Override
    public double process(final double equity) {
        if (equity >= maxEquity) {
            maxEquity = equity;
            return 0D;
        } else {
            final double drawdown = maxEquity - equity;
            //on multimarket strategies the drawdown can actually become positive for orders
            final double drawdownRate = Percent.newRate(drawdown, initialEquity);
            if (drawdownRate < 0D) {
                return 0D;
            } else {
                return drawdownRate;
            }
        }
    }

    public void reset() {
        this.maxEquity = 0D;
    }

    public void reset(final double initialEquity) {
        this.initialEquity = initialEquity;
        this.maxEquity = initialEquity;
    }

}
