package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleSimpleCrossesBelowOperation;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class IntegerSimpleCrossesBelowOperation extends DoubleSimpleCrossesBelowOperation {

    public IntegerSimpleCrossesBelowOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(left, right, previousKeyFunction);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble(final int key) {
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public int evaluateInteger(final int key) {
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        //crosses below => left was above but went below right

        final int leftValue0 = left.evaluateInteger(key);
        final int rightValue0 = right.evaluateInteger(key);
        //left is below right
        if (Integers.isLessThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final int leftValue1 = previousKeyFunction.evaluateInteger(left, previousKey);
            final int rightValue1 = previousKeyFunction.evaluateInteger(right, previousKey);
            //previous left is above or equal to previous right
            if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
