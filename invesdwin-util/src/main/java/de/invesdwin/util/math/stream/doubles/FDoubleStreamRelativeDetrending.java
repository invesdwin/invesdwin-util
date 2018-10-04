package de.invesdwin.util.math.stream.doubles;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.doubles.AFDouble;
import de.invesdwin.util.math.doubles.FDouble;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

/**
 * http://quant.stackexchange.com/questions/4286/detrending-price-data-for-analysis-of-signal-returns
 * 
 * http://www.automated-trading-system.com/detrending-for-trend-following/
 */
@NotThreadSafe
public class FDoubleStreamRelativeDetrending<Y extends AFDouble<Y>>
        implements IStreamAlgorithm<FDoublePoint<FDouble, Y>, FDoublePoint<FDouble, Y>> {

    private final FDoublePoint<FDouble, Y> from;
    private final FDoublePoint<FDouble, Y> to;

    private final FDouble fromX;
    private final double fromY;
    private final double logAvgChangeYperX;

    public FDoubleStreamRelativeDetrending(final FDoublePoint<FDouble, Y> from, final FDoublePoint<FDouble, Y> to) {
        this.from = from;
        this.to = to;
        this.fromX = from.getX();
        final double xChange = scaleChangeInX(to.getX().subtract(fromX)).getDefaultValue();
        if (xChange <= 0D) {
            throw new IllegalArgumentException(
                    "from [" + from + "] -> to [" + to + "] has negative change per x: " + xChange);
        }
        this.fromY = getY(from).getDefaultValue();
        final double toAdjY = getY(to).getDefaultValue();
        this.logAvgChangeYperX = Math.log(toAdjY / fromY) / xChange;
    }

    /**
     * logAvgChangeYperX = LOG(toY/fromY) / (toX - fromX)
     * 
     * logDetrendedAdjustment = LOG(curY/fromY) - logAvgChangeYperX*(curX-fromX)
     * 
     * detrendedY = fromY * EXP(logDetrendedProfit)
     */
    @Override
    public FDoublePoint<FDouble, Y> process(final FDoublePoint<FDouble, Y> value) {
        final FDouble curX = value.getX();
        final double curY = getY(value).getDefaultValue();

        final double changeInX = scaleChangeInX(curX.subtract(fromX)).getDefaultValue();

        final double logCurProfit = Math.log(curY / fromY);
        final double logMinusProfit = logAvgChangeYperX * changeInX;
        final double logDetrendedProfit = logCurProfit - logMinusProfit;
        final double detrendedY = fromY * Math.exp(logDetrendedProfit);
        return new FDoublePoint<FDouble, Y>(curX, value.getY().fromDefaultValue(detrendedY));
    }

    private Y getY(final FDoublePoint<FDouble, Y> value) {
        final Y curY = value.getY();
        if (curY.isNegativeOrZero()) {
            throw new IllegalArgumentException("Current value [" + value
                    + "] is negative or zero. Please preprocess the data so this does not happen because we cannot create a logarithm of a negative value.");
        }
        return curY;
    }

    /**
     * It might be needed to scale a change in X from e.g. milliseconds to minutes in order to make the detrending
     * algorithm numerically valid when using double as the underlying decimal implementation.
     */
    protected FDouble scaleChangeInX(final FDouble changeInX) {
        return changeInX;
    }

    public FDoublePoint<FDouble, Y> getFrom() {
        return from;
    }

    public FDoublePoint<FDouble, Y> getTo() {
        return to;
    }

}
