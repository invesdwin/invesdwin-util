package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.tokenizer.ExpressionContextUtil;

@NotThreadSafe
public abstract class AFunctionCall<F extends AFunction> implements IParsedExpression {

    protected final String context;
    protected final IParsedExpression[] parameters;
    protected final F function;

    public AFunctionCall(final String context, final F function, final IParsedExpression[] parameters) {
        this.context = context;
        this.function = function;
        this.parameters = parameters;
    }

    public AFunctionCall(final String context, final F function, final IParsedExpression parameter) {
        this.context = context;
        this.function = function;
        this.parameters = new IParsedExpression[] { parameter };
    }

    public AFunctionCall(final String context, final F function) {
        this.context = context;
        this.function = function;
        this.parameters = EMPTY_ARRAY;
    }

    @Override
    public IParsedExpression simplify() {
        final AFunctionCall<F> simplifiedParameters = simplifyParameters();
        if (!function.isNaturalFunction(parameters)) {
            return simplifiedParameters;
        }
        for (int i = 0; i < simplifiedParameters.parameters.length; i++) {
            if (!simplifiedParameters.parameters[i].isConstant()) {
                return simplifiedParameters;
            }
        }
        return new ConstantExpression(newEvaluateDouble().evaluateDouble());
    }

    private AFunctionCall<F> simplifyParameters() {
        if (parameters.length == 0) {
            return this;
        } else {
            final IParsedExpression[] simplifiedParameters = new IParsedExpression[parameters.length];
            for (int i = 0; i < simplifiedParameters.length; i++) {
                simplifiedParameters[i] = parameters[i].simplify();
            }
            return newFunctionCall(context, function, simplifiedParameters);
        }
    }

    protected abstract AFunctionCall<F> newFunctionCall(String context, F function, IParsedExpression[] parameters);

    public F getFunction() {
        return function;
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
        ExpressionContextUtil.putContext(context, sb);
        sb.append(function.getExpressionName());
        if (parameters.length > 0) {
            sb.append("(");
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(parameters[i].toString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public Object getProperty(final String property) {
        return function.getProperty(property);
    }

    @Override
    public IExpression[] getChildren() {
        return parameters;
    }

    @Override
    public ExpressionType getType() {
        return function.getType();
    }

}
