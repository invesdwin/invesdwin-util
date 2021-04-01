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
public class ConstantPreviousKeyExpression implements IParsedExpression {

    private final IParsedExpression expression;
    private final int index;
    private final IPreviousKeyFunction previousKeyFunction;

    public ConstantPreviousKeyExpression(final IParsedExpression expression, final int index,
            final IPreviousKeyFunction previousKeyFunction) {
        this.expression = expression;
        this.index = index;
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public ExpressionType getType() {
        return expression.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateDoubleFDate prevF = previousKeyFunction.newEvaluateDoubleFDate(expression);
        if (index < 0) {
            return key -> Double.NaN;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateDouble(previousKey);
            };
        }
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateDoubleKey prevF = previousKeyFunction.newEvaluateDoubleKey(expression);
        if (index < 0) {
            return key -> Double.NaN;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateDouble(previousKey);
            };
        }
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateIntegerFDate prevF = previousKeyFunction.newEvaluateIntegerFDate(expression);
        if (index < 0) {
            return key -> 0;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateInteger(previousKey);
            };
        }
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateIntegerKey prevF = previousKeyFunction.newEvaluateIntegerKey(expression);
        if (index < 0) {
            return key -> 0;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateInteger(previousKey);
            };
        }
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateBooleanNullableFDate prevF = previousKeyFunction.newEvaluateBooleanNullableFDate(expression);
        if (index < 0) {
            return key -> null;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBooleanNullable(previousKey);
            };
        }
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateBooleanNullableKey prevF = previousKeyFunction.newEvaluateBooleanNullableKey(expression);
        if (index < 0) {
            return key -> null;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBooleanNullable(previousKey);
            };
        }
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateBooleanFDate prevF = previousKeyFunction.newEvaluateBooleanFDate(expression);
        if (index < 0) {
            return key -> false;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBoolean(previousKey);
            };
        }
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanKey prevF = previousKeyFunction.newEvaluateBooleanKey(expression);
        if (index < 0) {
            return key -> false;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateBoolean(previousKey);
            };
        }
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateFalseReasonFDate(expression);
        if (index < 0) {
            return key -> toString();
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateFalseReasonKey(expression);
        if (index < 0) {
            return key -> toString();
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateTrueReasonFDate(expression);
        if (index < 0) {
            return key -> null;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateTrueReasonKey(expression);
        if (index < 0) {
            return key -> null;
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        final IEvaluateGenericFDate<String> prevF = previousKeyFunction.newEvaluateNullReasonFDate(expression);
        if (index < 0) {
            return key -> toString();
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        final IEvaluateGenericKey<String> prevF = previousKeyFunction.newEvaluateNullReasonKey(expression);
        if (index < 0) {
            return key -> toString();
        } else if (index == 0) {
            return prevF;
        } else {
            return key -> {
                final int previousKey = previousKeyFunction.getPreviousKey(key, index);
                return prevF.evaluateGeneric(previousKey);
            };
        }
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (index < 0) {
            return ConstantExpression.NaN;
        } else if (index == 0 || expression.isConstant()) {
            return expression.simplify();
        }
        return this;
    }

    @Override
    public String toString() {
        return expression + "[" + index + "]";
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public Object getProperty(final String property) {
        return null;
    }

    @Override
    public IExpression[] getChildren() {
        return new IExpression[] { expression };
    }
}
