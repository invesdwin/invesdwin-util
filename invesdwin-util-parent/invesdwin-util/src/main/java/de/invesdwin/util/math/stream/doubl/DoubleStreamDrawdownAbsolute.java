package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DoubleStreamDrawdownAbsolute implements IDoubleStreamAlgorithm {

    private double maxEquity;

    public DoubleStreamDrawdownAbsolute() {
        this.maxEquity = 0D;
    }

    public DoubleStreamDrawdownAbsolute(final double initialEquity) {
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
            if (drawdown < 0D) {
                return 0D;
            } else {
                return drawdown;
            }
        }
    }

    public void reset() {
        this.maxEquity = 0D;
    }

    public void reset(final double initialEquity) {
        this.maxEquity = initialEquity;
    }

}
