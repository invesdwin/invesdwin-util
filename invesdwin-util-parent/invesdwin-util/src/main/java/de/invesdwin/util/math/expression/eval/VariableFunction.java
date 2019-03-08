package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunction;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class VariableFunction implements IFunction {

    private final String context;
    private final String name;
    private final IParsedExpression variable;

    public VariableFunction(final String context, final String name, final IParsedExpression variable) {
        this.context = context;
        this.name = name;
        this.variable = variable;
    }

    @Override
    public String getName() {
        return name;
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

}
