package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleCrossesAboveOperation;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

// AvgTrueRange(10) crosses above AvgTrueRange(20)
// ATR(10)[0] > ATR(20)[0] && ATR(10)[1] <= ATR(20)[1]
@Immutable
public class IntegerCrossesAboveOperation extends DoubleCrossesAboveOperation {

    public IntegerCrossesAboveOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction leftPreviousKeyFunction, final IPreviousKeyFunction rightPreviousKeyFunction) {
        super(left, right, leftPreviousKeyFunction, rightPreviousKeyFunction);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble(final int key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
    public int evaluateInteger(final IFDateProvider key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public int evaluateInteger(final int key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public int evaluateInteger() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        //crosses above => left was below but went above right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is above right
        if (Integers.isGreaterThan(leftValue0, rightValue0)) {
            final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = leftPreviousKeyFunction.evaluateInteger(left, leftPreviousKey);
            final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
            final int rightValue1 = rightPreviousKeyFunction.evaluateInteger(right, rightPreviousKey);
            //previous left is below or equal to previous right
            if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        throw new UnsupportedOperationException("already simplified");
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
