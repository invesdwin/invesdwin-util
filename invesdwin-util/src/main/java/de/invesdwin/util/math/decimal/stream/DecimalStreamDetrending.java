package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

/**
 * http://quant.stackexchange.com/questions/4286/detrending-price-data-for-analysis-of-signal-returns
 * 
 * http://www.automated-trading-system.com/detrending-for-trend-following/
 */
@NotThreadSafe
public class DecimalStreamDetrending<Y extends ADecimal<Y>>
        implements IDecimalStreamAlgorithm<DecimalPoint<Decimal, Y>, DecimalPoint<Decimal, Y>> {

    private final DecimalPoint<Decimal, Y> from;
    private final DecimalPoint<Decimal, Y> to;

    private final Decimal fromX;
    private final Y fromY;
    private final Y logAvgChangeYperX;

    public DecimalStreamDetrending(final DecimalPoint<Decimal, Y> from, final DecimalPoint<Decimal, Y> to) {
        this.from = from;
        this.to = to;
        this.fromX = from.getX();
        final Decimal xChange = scaleChangeInX(to.getX().subtract(fromX));
        Assertions.assertThat(xChange).isGreaterThan(Decimal.ZERO);
        this.fromY = from.getY();
        this.logAvgChangeYperX = to.getY().divide(fromY).log().divide(xChange);
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
        final Y curY = value.getY();

        final Decimal changeInX = scaleChangeInX(curX.subtract(fromX));

        final Y logCurProfit = curY.divide(fromY).log();
        final Y logMinusProfit = logAvgChangeYperX.multiply(changeInX);
        final Y logDetrendedProfit = logCurProfit.subtract(logMinusProfit);
        final Y detrendedY = fromY.multiply(logDetrendedProfit.exp());
        return new DecimalPoint<Decimal, Y>(curX, detrendedY);
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
