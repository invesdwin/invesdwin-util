package de.invesdwin.util.math.expression.multiple;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.ExpressionParser;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;

@NotThreadSafe
public final class NestedExpressionParser extends ExpressionParser {

    private MultipleExpressionParser parent;

    public NestedExpressionParser(final String expression) {
        super(expression);
    }

    void setParent(final MultipleExpressionParser parent) {
        this.parent = parent;
    }

    @Override
    public AVariableReference<?> getVariable(final String context, final String name) {
        final AVariableReference<?> variable = parent.getVariable(context, name);
        if (variable != null) {
            return variable;
        }
        return super.getVariable(context, name);
    }

    @Override
    public AFunction getFunction(final String context, final String name) {
        final AFunction function = parent.getFunction(context, name);
        if (function != null) {
            return function;
        }
        return super.getFunction(context, name);
    }

    @Override
    protected Op getCommaOp() {
        return parent.getCommaOp();
    }

    @Override
    protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
        return parent.getPreviousKeyFunction(context);
    }

    @Override
    protected String modifyContext(final String context) {
        return parent.modifyContext(context);
    }

    @Override
    protected IParsedExpression simplify(final IParsedExpression expression) {
        return parent.simplify(expression);
    }
}