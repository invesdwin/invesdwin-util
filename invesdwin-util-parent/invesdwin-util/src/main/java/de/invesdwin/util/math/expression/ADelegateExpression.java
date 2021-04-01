package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateGeneric;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
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
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        return getDelegate().newEvaluateFalseReason();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        return getDelegate().newEvaluateFalseReasonFDate();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        return getDelegate().newEvaluateFalseReasonKey();
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        return getDelegate().newEvaluateTrueReason();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        return getDelegate().newEvaluateTrueReasonFDate();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        return getDelegate().newEvaluateTrueReasonKey();
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        return getDelegate().newEvaluateNullReason();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        return getDelegate().newEvaluateNullReasonFDate();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        return getDelegate().newEvaluateNullReasonKey();
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
    public Object getProperty(final String property) {
        return getDelegate().getProperty(property);
    }

    @Override
    public IExpression[] getChildren() {
        return getDelegate().getChildren();
    }

    @Override
    public IParsedExpression asParsedExpression() {
        return getDelegate().asParsedExpression();
    }

}
