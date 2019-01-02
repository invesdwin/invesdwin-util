package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class SimpleCrossesBelowOperation extends BinaryOperation {

    private final IPreviousKeyFunction previousKeyFunction;

    public SimpleCrossesBelowOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(Op.CROSSES_BELOW, left, right);
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = left.evaluateDouble(key);
        //left is below or equal to right
        if (leftValue0 <= rightValue0) {
            final FDate previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above previous right
            if (leftValue1 > rightValue1) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble(final int key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = left.evaluateDouble(key);
        //left is below or equal to right
        if (leftValue0 <= rightValue0) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above previous right
            if (leftValue1 > rightValue1) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = left.evaluateDouble(key);
        //left is below or equal to right
        if (leftValue0 <= rightValue0) {
            final FDate previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above previous right
            if (leftValue1 > rightValue1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = left.evaluateDouble(key);
        //left is below or equal to right
        if (leftValue0 <= rightValue0) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above previous right
            if (leftValue1 > rightValue1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    protected IParsedExpression newConstantExpression() {
        //expression will never be true
        return new ConstantExpression(0D);
    }

    @Override
    protected BinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new SimpleCrossesBelowOperation(left, right, previousKeyFunction);
    }

}
