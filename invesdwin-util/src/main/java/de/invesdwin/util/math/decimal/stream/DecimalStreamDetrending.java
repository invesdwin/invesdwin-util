package de.invesdwin.util.math.decimal.stream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamDetrending<Y extends ADecimal<Y>>
        implements IDecimalStreamAlgorithm<DecimalPoint<Decimal, Y>, DecimalPoint<Decimal, Y>> {

    private final DecimalPoint<Decimal, Y> from;
    private final DecimalPoint<Decimal, Y> to;
    private final Y avgChangeYperX;

    public DecimalStreamDetrending(final DecimalPoint<Decimal, Y> from, final DecimalPoint<Decimal, Y> to) {
        this.from = from;
        this.to = to;
        final Decimal xChange = to.getX().subtract(from.getX());
        Assertions.assertThat(xChange).isGreaterThan(Decimal.ZERO);
        this.avgChangeYperX = to.getY().subtract(from.getY()).divide(xChange);
    }

    /**
     * avgChangeYperX = (toY - fromY) / (toX - fromX)
     * 
     * detrendedY(x,y) = y - ((x - fromX) * avgChangeYperX)
     */
    @Override
    public DecimalPoint<Decimal, Y> process(final DecimalPoint<Decimal, Y> value) {
        final Decimal changeInX = value.getX().subtract(from.getX());
        final Y subtraction = avgChangeYperX.multiply(changeInX);
        final Y newY = value.getY().subtract(subtraction);
        return new DecimalPoint<Decimal, Y>(value.getX(), newY);
    }

    public DecimalPoint<Decimal, Y> getFrom() {
        return from;
    }

    public DecimalPoint<Decimal, Y> getTo() {
        return to;
    }

}
