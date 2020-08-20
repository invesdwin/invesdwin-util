package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@NotThreadSafe
public abstract class ADelegateExpression implements IExpression {

    protected abstract IExpression getDelegate();

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        return getDelegate().newEvaluateBoolean();
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        return getDelegate().newEvaluateBooleanFDate();
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        return getDelegate().newEvaluateBooleanKey();
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        return getDelegate().newEvaluateBooleanNullable();
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        return getDelegate().newEvaluateBooleanNullableFDate();
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        return getDelegate().newEvaluateBooleanNullableKey();
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        return getDelegate().newEvaluateDouble();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return getDelegate().newEvaluateDoubleFDate();
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        return getDelegate().newEvaluateDoubleKey();
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        return getDelegate().newEvaluateInteger();
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        return getDelegate().newEvaluateIntegerFDate();
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        return getDelegate().newEvaluateIntegerKey();
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
