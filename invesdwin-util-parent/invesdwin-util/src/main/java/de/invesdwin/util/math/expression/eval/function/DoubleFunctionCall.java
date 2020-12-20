package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

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
    protected AFunctionCall<ADoubleFunction> newFunctionCall(final String context, final ADoubleFunction function,
            final IParsedExpression[] parameters) {
        return new DoubleFunctionCall(context, function, parameters);
    }
}
