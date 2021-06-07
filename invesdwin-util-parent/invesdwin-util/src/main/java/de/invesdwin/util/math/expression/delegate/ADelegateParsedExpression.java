package de.invesdwin.util.math.expression.delegate;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;

@Immutable
public abstract class ADelegateParsedExpression extends ADelegateExpression implements IParsedExpression {

    @Override
    public IParsedExpression simplify() {
        return getDelegate();
    }

    @Override
    public ExpressionType getType() {
        return getDelegate().getType();
    }

    @Override
    protected abstract IParsedExpression getDelegate();

}
