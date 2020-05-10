package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class ConstantExpression implements IParsedExpression {

    private final double doubleValue;
    private final Boolean booleanValue;

    public ConstantExpression(final double value) {
        this.doubleValue = value;
        this.booleanValue = Doubles.doubleToBoolean(value);
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
    public Boolean evaluateBooleanNullable(final FDate key) {
        return booleanValue;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return booleanValue;
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return booleanValue;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return new Decimal(doubleValue).toString();
    }

    @Override
    public IParsedExpression simplify() {
        return this;
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ConstantExpression) {
            final ConstantExpression cObj = (ConstantExpression) obj;
            return doubleValue == cObj.doubleValue;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ConstantExpression.class.hashCode() + Double.hashCode(doubleValue);
    }

    @Override
    public boolean shouldPersist() {
        return false;
    }

    @Override
    public boolean shouldDraw() {
        return true;
    }

    @Override
    public IExpression[] getChildren() {
        return EMPTY_EXPRESSIONS;
    }

}
