package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleSimpleCrossesBelowOperation;
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
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class IntegerSimpleCrossesBelowOperation extends DoubleSimpleCrossesBelowOperation {

    public IntegerSimpleCrossesBelowOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(left, right, previousKeyFunction);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        //crosses below => left was above but went below right

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = previousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = previousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1D;
                }
            }

            return 0D;
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        //crosses below => left was above but went below right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = previousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = previousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = previousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = previousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        //crosses below => left was above but went below right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = previousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = previousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = previousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = previousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        //crosses below => left was above but went below right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = previousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = previousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevLeftF = previousKeyFunction.newEvaluateIntegerFDate(left);
        final IEvaluateIntegerFDate prevRightF = previousKeyFunction.newEvaluateIntegerFDate(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        //crosses below => left was above but went below right

        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevLeftF = previousKeyFunction.newEvaluateIntegerKey(left);
        final IEvaluateIntegerKey prevRightF = previousKeyFunction.newEvaluateIntegerKey(right);

        return key -> {
            final int leftValue0 = leftF.evaluateInteger(key);
            final int rightValue0 = rightF.evaluateInteger(key);
            //left is below right
            if (Integers.isLessThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final int leftValue1 = prevLeftF.evaluateInteger(previousKey);
                final int rightValue1 = prevRightF.evaluateInteger(previousKey);
                //previous left is above or equal to previous right
                if (Integers.isGreaterThanOrEqualTo(leftValue1, rightValue1)) {
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
