package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
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
public abstract class ARedirectedDoubleExpression implements IParsedExpression {

    @Override
    public abstract IEvaluateDoubleFDate newEvaluateDoubleFDate();

    @Override
    public abstract IEvaluateDoubleKey newEvaluateDoubleKey();

    @Override
    public abstract IEvaluateDouble newEvaluateDouble();

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateDoubleFDate valueFDate = newEvaluateDoubleFDate();
        return key -> Integers.checkedCastNoOverflow(valueFDate.evaluateDouble(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateDoubleKey valueKey = newEvaluateDoubleKey();
        return key -> Integers.checkedCastNoOverflow(valueKey.evaluateDouble(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateDouble value = newEvaluateDouble();
        return () -> Integers.checkedCastNoOverflow(value.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateDoubleFDate valueFDate = newEvaluateDoubleFDate();
        return key -> Doubles.toBooleanNullable(valueFDate.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateDoubleKey valueKey = newEvaluateDoubleKey();
        return key -> Doubles.toBooleanNullable(valueKey.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateDouble value = newEvaluateDouble();
        return () -> Doubles.toBooleanNullable(value.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateDoubleFDate valueFDate = newEvaluateDoubleFDate();
        return key -> Doubles.toBoolean(valueFDate.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateDoubleKey valueKey = newEvaluateDoubleKey();
        return key -> Doubles.toBoolean(valueKey.evaluateDouble(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateDouble value = newEvaluateDouble();
        return () -> Doubles.toBoolean(value.evaluateDouble());
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable())) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable())) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Booleans.isTrue(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Objects.isNull(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            if (Objects.isNull(f.evaluateBooleanNullable())) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            if (Objects.isNull(f.evaluateBooleanNullable(key))) {
                return ARedirectedDoubleExpression.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public abstract String toString();

    @Override
    public IParsedExpression simplify() {
        return this;
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public Object getProperty(final String property) {
        return null;
    }

    @Override
    public IExpression[] getChildren() {
        return EMPTY_EXPRESSIONS;
    }

}
