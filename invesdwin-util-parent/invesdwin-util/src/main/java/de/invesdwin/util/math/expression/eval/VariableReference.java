package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.variable.IVariable;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class VariableReference implements IParsedExpression {

    private final String context;
    private final IVariable variable;

    public VariableReference(final String context, final IVariable variable) {
        this.context = context;
        this.variable = variable;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        return variable.getValue(key);
    }

    @Override
    public double evaluateDouble(final int key) {
        return variable.getValue(key);
    }

    @Override
    public double evaluateDouble() {
        return variable.getValue();
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        return variable.getValue() > 0D;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return variable.getValue() > 0D;
    }

    @Override
    public boolean evaluateBoolean() {
        return variable.getValue() > 0D;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (context != null) {
            sb.append(context);
            sb.append(":");
        }
        sb.append(variable.getExpressionName());
        return sb.toString();
    }

    @Override
    public boolean isConstant() {
        return variable.isConstant();
    }

    @Override
    public IParsedExpression simplify() {
        if (isConstant()) {
            return new ConstantExpression(evaluateDouble());
        }
        return this;
    }

    @Override
    public String getContext() {
        return context;
    }

    public IVariable getVariable() {
        return variable;
    }

    @Override
    public boolean shouldPersist() {
        return variable.shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return variable.shouldDraw();
    }

    @Override
    public IExpression[] getChildren() {
        return EMPTY_EXPRESSIONS;
    }

}
