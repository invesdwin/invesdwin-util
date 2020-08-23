package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.IntegerSimpleCrossesAboveOperation;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleSimpleCrossesAboveOperation extends DoubleBinaryOperation {

    protected final IPreviousKeyFunction previousKeyFunction;

    public DoubleSimpleCrossesAboveOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(Op.CROSSES_ABOVE, left, right);
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        //crosses above => left was below but went above right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public double evaluateDouble(final int key) {
        //crosses above => left was below but went above right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public int evaluateInteger(final int key) {
        //crosses above => left was below but went above right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        //crosses above => left was below but went above right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        //crosses above => left was below but went above right

        final double leftValue0 = left.evaluateDouble(key);
        final double rightValue0 = right.evaluateDouble(key);
        //left is above right
        if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
            final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
            final double leftValue1 = previousKeyFunction.evaluateDouble(left, previousKey);
            final double rightValue1 = previousKeyFunction.evaluateDouble(right, previousKey);
            //previous left is below or equal to previous right
            if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new DoubleSimpleCrossesAboveOperation(left, right, previousKeyFunction);
        } else if (simplifyType == ExpressionType.Integer) {
            return new IntegerSimpleCrossesAboveOperation(left, right, previousKeyFunction);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}