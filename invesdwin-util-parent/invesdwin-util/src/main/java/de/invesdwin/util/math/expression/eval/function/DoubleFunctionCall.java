package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
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
public class DoubleFunctionCall extends AFunctionCall<ADoubleFunction> {

    public DoubleFunctionCall(final String context, final ADoubleFunction function,
            final IParsedExpression[] parameters) {
        super(context, function, parameters);
    }

    public DoubleFunctionCall(final String context, final ADoubleFunction function, final IParsedExpression parameter) {
        super(context, function, parameter);
    }

    public DoubleFunctionCall(final String context, final ADoubleFunction function) {
        super(context, function);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return function.newEvaluateDoubleFDate(getContext(), parameters);
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        return function.newEvaluateDoubleKey(getContext(), parameters);
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        return function.newEvaluateDouble(getContext(), parameters);
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
        final IEvaluateDoubleFDate f = newEvaluateDoubleFDate();
        return key -> Doubles.toBoolean(f.evaluateDouble(key));
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
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
                return DoubleFunctionCall.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    protected AFunctionCall<ADoubleFunction> newFunctionCall(final String context, final ADoubleFunction function,
            final IParsedExpression[] parameters) {
        return new DoubleFunctionCall(context, function, parameters);
    }
}
