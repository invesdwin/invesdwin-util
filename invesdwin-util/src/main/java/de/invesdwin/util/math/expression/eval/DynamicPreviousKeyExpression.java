package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.IPreviousKeyFunction;
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
        final int index = Integers.checkedCast(indexExpression.evaluateDouble(key));
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateDouble(previousKey);
    }

    @Override
    public double evaluateDouble(final int key) {
        final int index = Integers.checkedCast(indexExpression.evaluateDouble(key));
        final int previousKey = key - index;
        return expression.evaluateDouble(previousKey);
    }

    @Override
    public double evaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        final int index = Integers.checkedCast(indexExpression.evaluateDouble(key));
        final FDate previousKey = previousKeyFunction.getPreviousKey(key, index);
        return expression.evaluateBoolean(previousKey);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final int index = Integers.checkedCast(indexExpression.evaluateDouble(key));
        final int previousKey = key - index;
        return expression.evaluateBoolean(previousKey);
    }

    @Override
    public boolean evaluateBoolean() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant() && indexExpression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (indexExpression.isConstant()) {
            final int index = Integers.checkedCast(indexExpression.evaluateDouble(null));
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

}
