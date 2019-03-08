package de.invesdwin.util.math.stream.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.IStreamAlgorithm;

/**
 * http://quant.stackexchange.com/questions/4286/detrending-price-data-for-analysis-of-signal-returns
 * 
 * http://www.automated-trading-system.com/detrending-for-trend-following/
 */
@NotThreadSafe
public class DecimalStreamRelativeDetrending<Y extends ADecimal<Y>>
        implements IStreamAlgorithm<DecimalPoint<Decimal, Y>, DecimalPoint<Decimal, Y>> {

    private final DecimalPoint<Decimal, Y> from;
    private final DecimalPoint<Decimal, Y> to;

    private final Decimal fromX;
    private final double fromY;
    private final double logAvgChangeYperX;

    public DecimalStreamRelativeDetrending(final DecimalPoint<Decimal, Y> from, final DecimalPoint<Decimal, Y> to) {
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
    public DecimalPoint<Decimal, Y> process(final DecimalPoint<Decimal, Y> value) {
        final Decimal curX = value.getX();
        final double curY = getY(value).getDefaultValue();

        final double changeInX = scaleChangeInX(curX.subtract(fromX)).getDefaultValue();

        final double logCurProfit = Math.log(curY / fromY);
        final double logMinusProfit = logAvgChangeYperX * changeInX;
        final double logDetrendedProfit = logCurProfit - logMinusProfit;
        final double detrendedY = fromY * Math.exp(logDetrendedProfit);
        return new DecimalPoint<Decimal, Y>(curX, value.getY().fromDefaultValue(detrendedY));
    }

    private Y getY(final DecimalPoint<Decimal, Y> value) {
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
    protected Decimal scaleChangeInX(final Decimal changeInX) {
        return changeInX;
    }

    public DecimalPoint<Decimal, Y> getFrom() {
        return from;
    }

    public DecimalPoint<Decimal, Y> getTo() {
        return to;
    }

}
