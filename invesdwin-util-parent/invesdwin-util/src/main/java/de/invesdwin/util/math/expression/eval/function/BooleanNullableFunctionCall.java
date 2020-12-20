package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ABooleanNullableFunction;
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
public class BooleanNullableFunctionCall extends AFunctionCall<ABooleanNullableFunction> {

    public BooleanNullableFunctionCall(final String context, final ABooleanNullableFunction function,
            final IParsedExpression[] parameters) {
        super(context, function, parameters);
    }

    public BooleanNullableFunctionCall(final String context, final ABooleanNullableFunction function,
            final IParsedExpression parameter) {
        super(context, function, parameter);
    }

    public BooleanNullableFunctionCall(final String context, final ABooleanNullableFunction function) {
        super(context, function);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Doubles.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Doubles.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Doubles.fromBoolean(f.evaluateBooleanNullable());
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Integers.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Integers.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Integers.fromBoolean(f.evaluateBooleanNullable());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        return function.newEvaluateBooleanNullableFDate(getContext(), parameters);
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        return function.newEvaluateBooleanNullableKey(getContext(), parameters);
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        return function.newEvaluateBooleanNullable(getContext(), parameters);
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Booleans.isTrue(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Booleans.isTrue(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Booleans.isTrue(f.evaluateBooleanNullable());
    }

    @Override
    protected AFunctionCall<ABooleanNullableFunction> newFunctionCall(final String context,
            final ABooleanNullableFunction function, final IParsedExpression[] parameters) {
        return new BooleanNullableFunctionCall(context, function, parameters);
    }

}
