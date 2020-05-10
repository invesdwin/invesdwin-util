package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.FDate;

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
    public double evaluateDouble(final FDate key) {
        final int index = indexExpression.evaluateInteger(key);
        if (index < 0) {
            throw new IllegalArgumentException("index should not be negative: " + index);
        }
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateDouble(expression, previousKey);
    }

    @Override
    public double evaluateDouble(final int key) {
        final int index = indexExpression.evaluateInteger(key);
        if (index < 0) {
            throw new IllegalArgumentException("index should not be negative: " + index);
        }
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateDouble(expression, previousKey);
    }

    @Override
    public double evaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public Boolean evaluateBooleanNullable(final FDate key) {
        final int index = indexExpression.evaluateInteger(key);
        if (index < 0) {
            throw new IllegalArgumentException("index should not be negative: " + index);
        }
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final int index = indexExpression.evaluateInteger(key);
        if (index < 0) {
            throw new IllegalArgumentException("index should not be negative: " + index);
        }
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant() && indexExpression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (indexExpression.isConstant()) {
            final int index = indexExpression.evaluateInteger();
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
