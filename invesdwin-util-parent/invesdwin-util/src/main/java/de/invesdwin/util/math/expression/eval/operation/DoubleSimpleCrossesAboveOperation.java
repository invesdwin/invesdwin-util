package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.IntegerSimpleCrossesAboveOperation;
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
import de.invesdwin.util.math.expression.lambda.IEvaluateGeneric;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.date.IFDateProvider;

@Immutable
public class DoubleSimpleCrossesAboveOperation extends DoubleBinaryOperation {

    protected final IPreviousKeyFunction previousKeyFunction;

    public DoubleSimpleCrossesAboveOperation(final IParsedExpression left, final IParsedExpression right,
            final IPreviousKeyFunction previousKeyFunction) {
        super(Op.CROSSES_ABOVE, left, right);
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        //crosses above => left was below but went above right

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1D;
                }
            }

            return 0D;
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        //crosses above => left was below but went above right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return 1;
                }
            }

            return 0;
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        //crosses above => left was below but went above right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        //crosses above => left was below but went above right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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

        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevLeftF = previousKeyFunction.newEvaluateDoubleFDate(left);
        final IEvaluateDoubleFDate prevRightF = previousKeyFunction.newEvaluateDoubleFDate(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
                    return true;
                }
            }

            return false;
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        //crosses above => left was below but went above right

        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevLeftF = previousKeyFunction.newEvaluateDoubleKey(left);
        final IEvaluateDoubleKey prevRightF = previousKeyFunction.newEvaluateDoubleKey(right);

        return key -> {
            final double leftValue0 = leftF.evaluateDouble(key);
            final double rightValue0 = rightF.evaluateDouble(key);
            //left is above right
            if (Doubles.isGreaterThan(leftValue0, rightValue0)) {
                final int previousKey = previousKeyFunction.getPreviousKey(key, 1);
                final double leftValue1 = prevLeftF.evaluateDouble(previousKey);
                final double rightValue1 = prevRightF.evaluateDouble(previousKey);
                //previous left is below or equal to previous right
                if (Doubles.isLessThanOrEqualTo(leftValue1, rightValue1)) {
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
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (!f.evaluateBoolean(key)) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            if (!f.evaluateBoolean()) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            if (!f.evaluateBoolean(key)) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            if (f.evaluateBoolean()) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return DoubleSimpleCrossesAboveOperation.this.toString();
            } else {
                return null;
            }
        };
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
