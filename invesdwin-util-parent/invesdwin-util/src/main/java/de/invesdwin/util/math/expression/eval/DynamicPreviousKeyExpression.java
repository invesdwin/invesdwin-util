package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
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
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DynamicPreviousKeyExpression implements IParsedExpression {

    private final IParsedExpression expression;
    private final IParsedExpression indexExpression;
    private final IPreviousKeyFunction previousKeyFunction;

    public DynamicPreviousKeyExpression(final IParsedExpression expression, final IParsedExpression indexExpression,
            final IPreviousKeyFunction previousKeyFunction) {
        this.expression = expression;
        this.indexExpression = indexExpression;
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public ExpressionType getType() {
        return expression.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateDoubleFDate expressionF = expression.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate prevF = previousKeyFunction.newEvaluateDoubleFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateDouble(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateDouble(previousKey);
            }
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateDoubleKey expressionF = expression.newEvaluateDoubleKey();
        final IEvaluateDoubleKey prevF = previousKeyFunction.newEvaluateDoubleKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateDouble(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateDouble(previousKey);
            }
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate expressionF = expression.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate prevF = previousKeyFunction.newEvaluateIntegerFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateInteger(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateInteger(previousKey);
            }
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateIntegerKey expressionF = expression.newEvaluateIntegerKey();
        final IEvaluateIntegerKey prevF = previousKeyFunction.newEvaluateIntegerKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateInteger(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateInteger(previousKey);
            }
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateBooleanNullableFDate expressionF = expression.newEvaluateBooleanNullableFDate();
        final IEvaluateBooleanNullableFDate prevF = previousKeyFunction.newEvaluateBooleanNullableFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateBooleanNullable(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBooleanNullable(previousKey);
            }
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateBooleanNullableKey expressionF = expression.newEvaluateBooleanNullableKey();
        final IEvaluateBooleanNullableKey prevF = previousKeyFunction.newEvaluateBooleanNullableKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateBooleanNullable(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBooleanNullable(previousKey);
            }
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateBooleanFDate expressionF = expression.newEvaluateBooleanFDate();
        final IEvaluateBooleanFDate prevF = previousKeyFunction.newEvaluateBooleanFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateBoolean(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBoolean(previousKey);
            }
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateBooleanKey expressionF = expression.newEvaluateBooleanKey();
        final IEvaluateBooleanKey prevF = previousKeyFunction.newEvaluateBooleanKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateBoolean(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBoolean(previousKey);
            }
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateGenericFDate<String> expressionF = expression.newEvaluateFalseReasonFDate();
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateFalseReasonFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateGenericKey<String> expressionF = expression.newEvaluateFalseReasonKey();
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateFalseReasonKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateGenericFDate<String> expressionF = expression.newEvaluateTrueReasonFDate();
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateTrueReasonFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateGenericKey<String> expressionF = expression.newEvaluateTrueReasonKey();
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateTrueReasonKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        final IEvaluateIntegerFDate indexF = indexExpression.newEvaluateIntegerFDate();
        final IEvaluateGenericFDate<String> expressionF = expression.newEvaluateNullReasonFDate();
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateNullReasonFDate(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        final IEvaluateIntegerKey indexF = indexExpression.newEvaluateIntegerKey();
        final IEvaluateGenericKey<String> expressionF = expression.newEvaluateNullReasonKey();
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateNullReasonKey(expression);
        return key -> {
            final int index = indexF.evaluateInteger(key);
            if (index <= 0) {
                return expressionF.evaluateGeneric(key);
            } else {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant() && indexExpression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (indexExpression.isConstant()) {
            final int index = indexExpression.newEvaluateInteger().evaluateInteger();
            return new ConstantPreviousKeyExpression(expression, index, previousKeyFunction).simplify();
        }
        if (expression.isConstant()) {
            return expression.simplify();
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return expression + "[" + indexExpression + "]";
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean shouldPersist() {
        return expression.shouldPersist() || indexExpression.shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return expression.shouldDraw();
    }

    @Override
    public IExpression[] getChildren() {
        return new IExpression[] { expression, indexExpression };
    }

}
