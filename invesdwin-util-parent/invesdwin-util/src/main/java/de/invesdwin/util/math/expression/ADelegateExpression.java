package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public abstract class ADelegateExpression implements IExpression {

    protected abstract IExpression getDelegate();

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        return getDelegate().evaluateDouble(key);
    }

    @Override
    public double evaluateDouble(final int key) {
        return getDelegate().evaluateDouble(key);
    }

    @Override
    public double evaluateDouble() {
        return getDelegate().evaluateDouble();
    }

    @Override
    public boolean evaluateBoolean() {
        return getDelegate().evaluateBoolean();
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return getDelegate().evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return getDelegate().evaluateBoolean(key);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return getDelegate().evaluateBooleanNullable();
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        return getDelegate().evaluateBooleanNullable(key);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return getDelegate().evaluateBooleanNullable(key);
    }

    @Override
    public boolean isConstant() {
        return getDelegate().isConstant();
    }

    @Override
    public String getContext() {
        return getDelegate().getContext();
    }

    @Override
    public boolean shouldPersist() {
        return getDelegate().shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return getDelegate().shouldDraw();
    }

    @Override
    public IExpression[] getChildren() {
        return getDelegate().getChildren();
    }

}
