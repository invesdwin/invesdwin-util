package de.invesdwin.util.math.stream.doubl;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;

/**
 * http://quant.stackexchange.com/questions/4286/detrending-price-data-for-analysis-of-signal-returns
 * 
 * http://www.automated-trading-system.com/detrending-for-trend-following/
 */
@NotThreadSafe
public class DoubleStreamRelativeDetrending {

    private final double toX;
    private final double toY;

    private final double fromX;
    private final double fromY;
    private final double logAvgChangeYperX;

    public DoubleStreamRelativeDetrending(final double fromX, final double fromY, final double toX, final double toY) {
        validateNotNanOrZero("fromX", fromX);
        this.fromX = fromX;
        validateNotNanOrZero("fromY", fromY);
        this.fromY = fromY;
        validateNotNanOrZero("toX", toX);
        this.toX = toX;
        validateNotNanOrZero("toY", toY);
        this.toY = toY;
        final double xChange = scaleChangeInX(toX - fromX);
        if (xChange <= 0D) {
            throw new IllegalArgumentException("from [" + fromX + " -> " + fromY + "] -> to [" + toX + " -> " + toY
                    + "] has negative or zero change per x: " + xChange);
        }
        final double toAdjY = getY(toY);
        this.logAvgChangeYperX = Doubles.log(toAdjY / fromY) / xChange;
    }

    private void validateNotNanOrZero(final String name, final double value) {
        if (Doubles.isNaN(value)) {
            throw new IllegalArgumentException(name + " should not be NaN");
        }
        if (value == 0D) {
            throw new IllegalArgumentException(name + " should not be zero");
        }
    }

    /**
     * logAvgChangeYperX = LOG(toY/fromY) / (toX - fromX)
     * 
     * logDetrendedAdjustment = LOG(curY/fromY) - logAvgChangeYperX*(curX-fromX)
     * 
     * detrendedY = fromY * EXP(logDetrendedProfit)
     */
    public double process(final double x, final double y) {

        final double changeInX = scaleChangeInX(x - fromX);

        final double logCurProfit = Doubles.log(y / fromY);
        final double logMinusProfit = logAvgChangeYperX * changeInX;
        final double logDetrendedProfit = logCurProfit - logMinusProfit;
        final double detrendedY = fromY * Math.exp(logDetrendedProfit);
        return detrendedY;
    }

    private double getY(final double y) {
        if (Doubles.isNegativeOrZero(y)) {
            throw new IllegalArgumentException("Current value [" + y
                    + "] is negative or zero. Please preprocess the data so this does not happen because we cannot create a logarithm of a negative value.");
        }
        return y;
    }

    /**
     * It might be needed to scale a change in X from e.g. milliseconds to minutes in order to make the detrending
     * algorithm numerically valid when using double as the underlying decimal implementation.
     */
    protected double scaleChangeInX(final double changeInX) {
        return changeInX;
    }

    public double getFromX() {
        return fromX;
    }

    public double getFromY() {
        return fromY;
    }

    public double getToX() {
        return toX;
    }

    public double getToY() {
        return toY;
    }

}
