package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IFunction;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class FunctionCall implements IParsedExpression {

    private static final IParsedExpression[] EMPTY_PARAMETERS = new IParsedExpression[0];

    private final String context;
    private final IParsedExpression[] parameters;
    private final IFunction function;

    public FunctionCall(final String context, final IFunction function, final IParsedExpression[] parameters) {
        this.context = context;
        this.function = function;
        this.parameters = parameters;
    }

    public FunctionCall(final String context, final IFunction function, final IParsedExpression parameter) {
        this.context = context;
        this.function = function;
        this.parameters = new IParsedExpression[] { parameter };
    }

    public FunctionCall(final String context, final IFunction function) {
        this.context = context;
        this.function = function;
        this.parameters = EMPTY_PARAMETERS;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        return function.eval(key, parameters);
    }

    @Override
    public double evaluateDouble(final int key) {
        return function.eval(key, parameters);
    }

    @Override
    public double evaluateDouble() {
        return function.eval(parameters);
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        return function.eval(key, parameters) > 0;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return function.eval(key, parameters) > 0;
    }

    @Override
    public boolean evaluateBoolean() {
        return function.eval(parameters) > 0;
    }

    @Override
    public IParsedExpression simplify() {
        if (!function.isNaturalFunction()) {
            return this;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].isConstant()) {
                return this;
            }
        }
        return new ConstantExpression(evaluateDouble());
    }

    public IParsedExpression[] getParameters() {
        return parameters;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (context != null) {
            sb.append(context);
            sb.append(":");
        }
        sb.append(function.getName());
        sb.append("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameters[i].toString());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String getContext() {
        return null;
    }

}
