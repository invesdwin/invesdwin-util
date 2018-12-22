package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.variable.Constant;
import de.invesdwin.util.math.expression.variable.IVariable;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class VariableReference implements IParsedExpression {

    private final IVariable var;

    public VariableReference(final IVariable var) {
        this.var = var;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        return var.getValue(key);
    }

    @Override
    public double evaluateDouble(final int key) {
        return var.getValue(key);
    }

    @Override
    public double evaluateDouble() {
        return var.getValue();
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        return var.getValue() > 0D;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return var.getValue() > 0D;
    }

    @Override
    public boolean evaluateBoolean() {
        return var.getValue() > 0D;
    }

    @Override
    public String toString() {
        return var.getName();
    }

    @Override
    public boolean isConstant() {
        return var instanceof Constant;
    }

    @Override
    public IParsedExpression simplify() {
        if (isConstant()) {
            return new ConstantExpression(evaluateDouble());
        }
        return this;
    }

}
