package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunction;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class VariableFunction implements IFunction {

    private final String context;
    private final String name;
    private final VariableReference variable;

    public VariableFunction(final String context, final String name, final VariableReference variable) {
        this.context = context;
        this.name = name;
        this.variable = variable;
    }

    @Override
    public String getExpressionName() {
        return name;
    }

    @Override
    public String getName() {
        return variable.getVariable().getName();
    }

    @Override
    public String getDescription() {
        return variable.getVariable().getDescription();
    }

    @Override
    public IFunctionParameterInfo getParameterInfo(final int index) {
        throw new ArrayIndexOutOfBoundsException(index);
    }

    @Override
    public int getNumberOfArguments() {
        return 0;
    }

    @Override
    public double eval(final FDate key, final IExpression[] args) {
        return variable.evaluateDouble(key);
    }

    @Override
    public double eval(final int key, final IExpression[] args) {
        return variable.evaluateDouble(key);
    }

    @Override
    public double eval(final IExpression[] args) {
        return variable.evaluateDouble();
    }

    @Override
    public boolean isNaturalFunction() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (context != null) {
            sb.append(context);
            sb.append(":");
        }
        sb.append(name);
        return sb.toString();
    }

    @Override
    public ExpressionType getReturnType() {
        return variable.getVariable().getType();
    }

}
