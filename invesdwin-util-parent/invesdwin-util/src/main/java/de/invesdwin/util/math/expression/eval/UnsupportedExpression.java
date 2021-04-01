package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
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

@Immutable
public final class UnsupportedExpression implements IParsedExpression {

    public static final UnsupportedExpression INSTANCE = new UnsupportedExpression();

    private UnsupportedExpression() {
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IParsedExpression simplify() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(final String property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IExpression[] getChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionType getType() {
        throw new UnsupportedOperationException();
    }

}
