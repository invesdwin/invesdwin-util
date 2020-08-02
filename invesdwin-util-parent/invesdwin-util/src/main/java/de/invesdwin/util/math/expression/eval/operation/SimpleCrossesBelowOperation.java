package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
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
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final FDate previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble(final int key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
    public Boolean evaluateBooleanNullable(final FDate key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final FDate previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    protected BinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new SimpleCrossesBelowOperation(left, right, previousKeyFunction);
    }

}
