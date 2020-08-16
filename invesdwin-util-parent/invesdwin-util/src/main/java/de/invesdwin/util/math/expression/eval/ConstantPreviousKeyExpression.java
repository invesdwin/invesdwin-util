package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class ConstantPreviousKeyExpression implements IParsedExpression {

    private final IParsedExpression expression;
    private final Integer index;
    private final IPreviousKeyFunction previousKeyFunction;

    public ConstantPreviousKeyExpression(final IParsedExpression expression, final Integer index,
            final IPreviousKeyFunction previousKeyFunction) {
        this.expression = expression;
        if (index < 0) {
            this.index = null;
        } else {
            this.index = index;
        }
        this.previousKeyFunction = previousKeyFunction;
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateDouble(expression, previousKey);
    }

    @Override
    public double evaluateDouble(final int key) {
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateDouble(expression, previousKey);
    }

    @Override
    public double evaluateDouble() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final IFDateProvider previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final int previousKey = previousKeyFunction.getPreviousKey(key, index);
        return previousKeyFunction.evaluateBooleanNullable(expression, previousKey);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        throw new UnsupportedOperationException("use time or int key instead");
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (index == null || index == 0 || expression.isConstant()) {
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
    public boolean shouldPersist() {
        return expression.shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return expression.shouldDraw();
    }

    @Override
    public IExpression[] getChildren() {
        return new IExpression[] { expression };
    }
}
