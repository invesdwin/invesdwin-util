package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class ConstantPreviousKeyExpression implements IParsedExpression {

    private final IParsedExpression expression;
    private final int index;
    private final IPreviousKeyFunction previousKeyFunction;

    public ConstantPreviousKeyExpression(final IParsedExpression expression, final int index,
            final IPreviousKeyFunction previousKeyFunction) {
        this.expression = expression;
        if (index < 0) {
            throw new IllegalArgumentException("index should not be negative: " + index);
        }
        this.index = index;
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateDouble(previousKey);
    }

    @Override
    public double evaluateDouble(final int key) {
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateDouble(previousKey);
    }

    @Override
    public double evaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateBoolean(previousKey);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateBoolean(previousKey);
    }

    @Override
    public boolean evaluateBoolean() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (index == 0 || expression.isConstant()) {
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

}
