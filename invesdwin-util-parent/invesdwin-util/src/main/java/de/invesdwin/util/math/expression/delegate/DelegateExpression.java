package de.invesdwin.util.math.expression.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.IExpression;

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
    public IExpression getDelegate() {
        return delegate;
    }

}
