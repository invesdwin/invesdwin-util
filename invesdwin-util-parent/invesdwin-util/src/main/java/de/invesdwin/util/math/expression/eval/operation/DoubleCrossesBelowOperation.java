package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

// AvgTrueRange(10) crosses below AvgTrueRange(20)
// ATR(10)[0] < ATR(20)[0] && ATR(10)[1] >= ATR(20)[1];
@Immutable
public class DoubleCrossesBelowOperation extends DoubleBinaryOperation {

    private final IPreviousKeyFunction leftPreviousKeyFunction;
    private final IPreviousKeyFunction rightPreviousKeyFunction;

    public DoubleCrossesBelowOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction leftPreviousKeyFunction, final IPreviousKeyFunction rightPreviousKeyFunction) {
        super(Op.CROSSES_BELOW, left, right);
        this.leftPreviousKeyFunction = leftPreviousKeyFunction;
        this.rightPreviousKeyFunction = rightPreviousKeyFunction;
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
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
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
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
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
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
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
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
    public boolean evaluateBoolean(final IFDateProvider key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        //crosses below => left was above but went below right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is below right
        if (Doubles.isLessThan(leftValue0, rightValue0)) {
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = leftPreviousKeyFunction.evaluateDouble(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final double rightValue1 = rightPreviousKeyFunction.evaluateDouble(right, rightPreviousKey);
            //previous left is above or equal to previous right
            if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
    protected DoubleBinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new DoubleCrossesBelowOperation(left, right, leftPreviousKeyFunction, rightPreviousKeyFunction);
    }

    @Override
    public IParsedExpression simplify() {
        if (leftPreviousKeyFunction == rightPreviousKeyFunction) {
            return new DoubleSimpleCrossesBelowOperation(left, right, leftPreviousKeyFunction).simplify();
        } else {
            return super.simplify();
        }
    }

}
