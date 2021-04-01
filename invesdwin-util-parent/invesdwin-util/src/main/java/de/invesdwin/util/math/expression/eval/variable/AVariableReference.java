package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.tokenizer.ExpressionContextUtil;
import de.invesdwin.util.math.expression.variable.IVariable;

@Immutable
public abstract class AVariableReference<V extends IVariable> implements IParsedExpression {

    protected final String context;
    protected final V variable;

    public AVariableReference(final String context, final V variable) {
        this.context = context;
        this.variable = variable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        ExpressionContextUtil.putContext(context, sb);
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
            return new ConstantExpression(newEvaluateDouble().evaluateDouble());
        }
        return this;
    }

    @Override
    public String getContext() {
        return context;
    }

    public V getVariable() {
        return variable;
    }

    @Override
    public Object getProperty(final String property) {
        return variable.getProperty(property);
    }

    @Override
    public IExpression[] getChildren() {
        return EMPTY_EXPRESSIONS;
    }

    public abstract AFunction asFunction();

}
