package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.IntegerSimpleCrossesBelowOperation;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleSimpleCrossesBelowOperation extends DoubleBinaryOperation {

    protected final IPreviousKeyFunction previousKeyFunction;

    public DoubleSimpleCrossesBelowOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(Op.CROSSES_BELOW, left, right);
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1D;
                }
            }

            return 0D;
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1D;
                }
            }

            return 0D;
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        throw new UnsupportedOperationException("crosses below operation is only supported with time or int index");
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        //crosses below => left was above but went below right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is below right
            if (Doubles.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is above or equal to previous right
                if (Doubles.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
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
            return new DoubleSimpleCrossesBelowOperation(left, right, previousKeyFunction);
        } else if (simplifyType == ExpressionType.Integer) {
            return new IntegerSimpleCrossesBelowOperation(left, right, previousKeyFunction);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}
