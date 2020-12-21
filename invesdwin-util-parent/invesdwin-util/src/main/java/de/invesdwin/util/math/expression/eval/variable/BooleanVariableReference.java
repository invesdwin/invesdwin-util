package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

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
import de.invesdwin.util.math.expression.variable.IBooleanVariable;

@Immutable
public class BooleanVariableReference extends AVariableReference<IBooleanVariable> {

    public BooleanVariableReference(final String context, final IBooleanVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        return variable.newEvaluateBooleanFDate(getContext());
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        return variable.newEvaluateBooleanKey(getContext());
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        return variable.newEvaluateBoolean(getContext());
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> Doubles.fromBoolean(f.evaluateBoolean(key));
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> Doubles.fromBoolean(f.evaluateBoolean(key));
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> Doubles.fromBoolean(f.evaluateBoolean());
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> Integers.fromBoolean(f.evaluateBoolean(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> Integers.fromBoolean(f.evaluateBoolean(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> Integers.fromBoolean(f.evaluateBoolean());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> f.evaluateBoolean(key);
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> f.evaluateBoolean(key);
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> f.evaluateBoolean();
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (!f.evaluateBoolean(key)) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateFalseReason() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            if (!f.evaluateBoolean()) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateFalseReasonFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            if (!f.evaluateBoolean(key)) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            if (f.evaluateBoolean()) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return BooleanVariableReference.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        //not nullable
        return () -> null;
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        //not nullable
        return key -> null;
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        //not nullable
        return key -> null;
    }

    @Override
    public AFunction asFunction() {
        return new BooleanVariableFunction(this);
    }
}
