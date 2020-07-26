package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateExpression extends ADelegateExpression {

    private IExpression delegate;

    public DelegateExpression(final IExpression delegate) {
        this.delegate = delegate;
    }

    public void setDelegate(final IExpression delegate) {
        this.delegate = delegate;
    }

    @Override
    protected IExpression getDelegate() {
        return delegate;
    }

}
