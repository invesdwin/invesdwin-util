package de.invesdwin.util.math.expression.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.eval.IParsedExpression;

@NotThreadSafe
public class DelegateParsedExpression extends ADelegateParsedExpression {

    private IParsedExpression delegate;

    public DelegateParsedExpression(final IParsedExpression delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final IParsedExpression delegate) {
        this.delegate = delegate;
    }

    @Override
    protected IParsedExpression getDelegate() {
        return delegate;
    }

}
