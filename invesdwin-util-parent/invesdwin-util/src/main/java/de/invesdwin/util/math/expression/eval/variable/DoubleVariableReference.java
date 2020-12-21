package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.function.AFunction;
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
import de.invesdwin.util.math.expression.variable.IDoubleVariable;

@Immutable
public class DoubleVariableReference extends AVariableReference<IDoubleVariable> {

    public DoubleVariableReference(final String context, final IDoubleVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return variable.newEvaluateDoubleFDate(getContext());
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        return variable.newEvaluateDoubleKey(getContext());
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        return variable.newEvaluateDouble(getContext());
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateDoubleFDate f = newEvaluateDoubleFDate();
        return key -> Integers.checkedCastNoOverflow(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Integers.checkedCastNoOverflow(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Integers.checkedCastNoOverflow(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateDoubleFDate f = newEvaluateDoubleFDate();
        return key -> Doubles.toBooleanNullable(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Doubles.toBooleanNullable(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Doubles.toBooleanNullable(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateDouble f = newEvaluateDouble();
        return key -> Doubles.toBoolean(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Doubles.toBoolean(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Doubles.toBoolean(f.evaluateDouble());
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            if (Booleans.isFalse(f.evaluateBooleanNullable(key))) {
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
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
                return DoubleVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public AFunction asFunction() {
        return new DoubleVariableFunction(this);
    }
}
