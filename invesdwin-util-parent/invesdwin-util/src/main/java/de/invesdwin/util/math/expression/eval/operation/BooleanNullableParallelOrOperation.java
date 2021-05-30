package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.BooleanConstantExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.BooleanParallelOrOperation;
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

@Immutable
public class BooleanNullableParallelOrOperation extends DoubleBinaryOperation {

    public BooleanNullableParallelOrOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.POR, left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            final Boolean check = f.evaluateBooleanNullable();
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            final boolean check = f.evaluateBoolean();
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
        final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            if (leftResult == null) {
                return rightResult;
            } else if (leftResult.booleanValue()) {
                return Boolean.TRUE;
            } else if (rightResult == null) {
                return Boolean.FALSE;
            } else {
                return rightResult;
            }
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
        final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            if (leftResult == null) {
                return rightResult;
            } else if (leftResult.booleanValue()) {
                return Boolean.TRUE;
            } else if (rightResult == null) {
                return Boolean.FALSE;
            } else {
                return rightResult;
            }
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
        final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
        return () -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable();
            final Boolean rightResult = rightF.evaluateBooleanNullable();
            if (leftResult == null) {
                return rightResult;
            } else if (leftResult.booleanValue()) {
                return Boolean.TRUE;
            } else if (rightResult == null) {
                return Boolean.FALSE;
            } else {
                return rightResult;
            }
        };
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
        final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            if (leftResult == null) {
                return Booleans.isTrue(rightResult);
            } else if (leftResult.booleanValue()) {
                return true;
            } else if (rightResult == null) {
                return false;
            } else {
                return rightResult.booleanValue();
            }
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
        final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            if (leftResult == null) {
                return Booleans.isTrue(rightResult);
            } else if (leftResult.booleanValue()) {
                return true;
            } else if (rightResult == null) {
                return false;
            } else {
                return rightResult.booleanValue();
            }
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
        final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
        return () -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable();
            final Boolean rightResult = rightF.evaluateBooleanNullable();
            if (leftResult == null) {
                return Booleans.isTrue(rightResult);
            } else if (leftResult.booleanValue()) {
                return true;
            } else if (rightResult == null) {
                return false;
            } else {
                return rightResult.booleanValue();
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateGenericKey<String> leftF = left.newEvaluateTrueReasonKey();
        final IEvaluateGenericKey<String> rightF = right.newEvaluateTrueReasonKey();
        return key -> {
            final String leftStr = leftF.evaluateGeneric(key);
            final String rightStr = rightF.evaluateGeneric(key);
            if (leftStr != null) {
                return leftStr;
            }
            return rightStr;
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateGeneric<String> leftF = left.newEvaluateTrueReason();
        final IEvaluateGeneric<String> rightF = right.newEvaluateTrueReason();
        return () -> {
            final String leftStr = leftF.evaluateGeneric();
            final String rightStr = rightF.evaluateGeneric();
            if (leftStr != null) {
                return leftStr;
            }
            return rightStr;
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateGenericFDate<String> leftF = left.newEvaluateTrueReasonFDate();
        final IEvaluateGenericFDate<String> rightF = right.newEvaluateTrueReasonFDate();
        return key -> {
            final String leftStr = leftF.evaluateGeneric(key);
            final String rightStr = rightF.evaluateGeneric(key);
            if (leftStr != null) {
                return leftStr;
            }
            return rightStr;
        };
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public IParsedExpression simplify() {
        final IParsedExpression newLeft = left.simplify();
        final IParsedExpression newRight = right.simplify();
        if (newLeft.isConstant()) {
            final Boolean leftResult = newLeft.newEvaluateBooleanNullable().evaluateBooleanNullable();
            if (Booleans.isTrue(leftResult)) {
                return BooleanConstantExpression.TRUE;
            } else {
                if (newRight.isConstant()) {
                    final Boolean rightResult = newRight.newEvaluateBooleanNullable().evaluateBooleanNullable();
                    if (rightResult != null) {
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
                    }
                } else {
                    return newRight;
                }
            }
        }
        if (newRight.isConstant()) {
            final Boolean rightResult = newRight.newEvaluateBooleanNullable().evaluateBooleanNullable();
            if (Booleans.isTrue(rightResult)) {
                return BooleanConstantExpression.TRUE;
            } else {
                if (newLeft.isConstant()) {
                    final Boolean leftResult = newLeft.newEvaluateBooleanNullable().evaluateBooleanNullable();
                    if (leftResult != null) {
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    }
                } else {
                    return newLeft;
                }
            }
        }
        return simplify(newLeft, newRight);
    }

    @Override
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new BooleanNullableParallelOrOperation(left, right);
        } else if (simplifyType == ExpressionType.Boolean) {
            return new BooleanParallelOrOperation(left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}
