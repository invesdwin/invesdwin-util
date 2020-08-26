package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleCrossesAboveOperation;
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

// AvgTrueRange(10) crosses above AvgTrueRange(20)
// ATR(10)[0] > ATR(20)[0] && ATR(10)[1] <= ATR(20)[1]
@Immutable
public class IntegerCrossesAboveOperation extends DoubleCrossesAboveOperation {

    public IntegerCrossesAboveOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction leftPreviousKeyFunction, final IPreviousKeyFunction rightPreviousKeyFunction) {
        super(left, right, leftPreviousKeyFunction, rightPreviousKeyFunction);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        //crosses above => left was below but went above right

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = rightPreviousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1D;
                }
            }

            return 0D;
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        //crosses above => left was below but went above right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = rightPreviousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses above => left was below but went above right

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = rightPreviousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        //crosses above => left was below but went above right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = rightPreviousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses above => left was below but went above right

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = rightPreviousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        //crosses above => left was below but went above right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = rightPreviousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
        //crosses above => left was below but went above right

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = rightPreviousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final IFDateProvider rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        //crosses above => left was below but went above right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = leftPreviousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = rightPreviousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is above right
            if (Integers.isGreaterThan(leftValue0, rightValue0)) {
                final int leftPreviousKey = leftPreviousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(leftPreviousKey);
                final int rightPreviousKey = rightPreviousKeyFunction.getPreviousKey(key, 1);
                final int rightValue1 = prevRightF.evaluateInteger(rightPreviousKey);
                //previous left is below or equal to previous right
                if (Integers.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
