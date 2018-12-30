package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class ConstantExpression implements IParsedExpression {

    private final double doubleValue;
    private final boolean booleanValue;

    public ConstantExpression(final double value) {
        this.doubleValue = value;
        this.booleanValue = value > 0D;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        return doubleValue;
    }

    @Override
    public double evaluateDouble(final int key) {
        return doubleValue;
    }

    @Override
    public double evaluateDouble() {
        return doubleValue;
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        return booleanValue;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return booleanValue;
    }

    @Override
    public boolean evaluateBoolean() {
        return booleanValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(doubleValue);
    }

    @Override
    public IParsedExpression simplify() {
        return this;
    }

    @Override
    public String getContext() {
        return null;
    }
}
