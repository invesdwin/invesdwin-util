package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.AIntegerFunction;
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
public class IntegerFunctionCall extends AFunctionCall<AIntegerFunction> {

    public IntegerFunctionCall(final String context, final AIntegerFunction function,
            final IParsedExpression[] parameters) {
        super(context, function, parameters);
    }

    public IntegerFunctionCall(final String context, final AIntegerFunction function,
            final IParsedExpression parameter) {
        super(context, function, parameter);
    }

    public IntegerFunctionCall(final String context, final AIntegerFunction function) {
        super(context, function);
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        return function.newEvaluateIntegerFDate(getContext(), parameters);
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        return function.newEvaluateIntegerKey(getContext(), parameters);
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        return function.newEvaluateInteger(getContext(), parameters);
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> f.evaluateInteger();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> f.evaluateInteger(key);
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> f.evaluateInteger(key);
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> Integers.toBooleanNullable(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> Integers.toBooleanNullable(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> Integers.toBooleanNullable(f.evaluateInteger());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> Integers.toBoolean(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> Integers.toBoolean(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> Integers.toBoolean(f.evaluateInteger());
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateFalseReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (!f.evaluateBoolean(key)) {
                return IntegerFunctionCall.this.toString();
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
                return IntegerFunctionCall.this.toString();
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
                return IntegerFunctionCall.this.toString();
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
                return IntegerFunctionCall.this.toString();
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
                return IntegerFunctionCall.this.toString();
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
                return IntegerFunctionCall.this.toString();
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
    protected AFunctionCall<AIntegerFunction> newFunctionCall(final String context, final AIntegerFunction function,
            final IParsedExpression[] parameters) {
        return new IntegerFunctionCall(context, function, parameters);
    }
}
