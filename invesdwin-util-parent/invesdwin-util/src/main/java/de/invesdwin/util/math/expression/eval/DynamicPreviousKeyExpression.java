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
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateInteger(key);
        } else {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateInteger(expression, previousKey);
        }
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateInteger(key);
        } else {
            final int previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateInteger(expression, previousKey);
        }
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateBooleanNullable(key);
        } else {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
        }
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateBooleanNullable(key);
        } else {
            final int previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
        }
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateBoolean(key);
        } else {
            final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateBoolean(expression, previousKey);
        }
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final int index = indexExpression.evaluateInteger(key);
        if (index <= 0) {
            return expression.evaluateBoolean(key);
        } else {
            final int previousKey = previousKeyFunction.getPreviousKey(key, index);
            return previousKeyFunction.evaluateBoolean(expression, previousKey);
        }
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
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
