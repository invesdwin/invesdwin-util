package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ABooleanNullableFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

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
    public double evaluateDouble(final IFDateProvider key) {
        return Doubles.booleanToDouble(evaluateBooleanNullable(key));
    }

    @Override
    public double evaluateDouble(final int key) {
        return Doubles.booleanToDouble(evaluateBooleanNullable(key));
    }

    @Override
    public double evaluateDouble() {
        return Doubles.booleanToDouble(evaluateBooleanNullable());
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        return Integers.booleanToInteger(evaluateBooleanNullable(key));
    }

    @Override
    public int evaluateInteger(final int key) {
        return Integers.booleanToInteger(evaluateBooleanNullable(key));
    }

    @Override
    public int evaluateInteger() {
        return Integers.booleanToInteger(evaluateBooleanNullable());
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final Boolean eval = function.eval(key, parameters);
        return eval;
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final Boolean eval = function.eval(key, parameters);
        return eval;
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final Boolean eval = function.eval(parameters);
        return eval;
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return Booleans.isTrue(evaluateBooleanNullable(key));
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return Booleans.isTrue(evaluateBooleanNullable(key));
    }

    @Override
    public boolean evaluateBoolean() {
        return Booleans.isTrue(evaluateBooleanNullable());
    }

    @Override
    protected AFunctionCall<ABooleanNullableFunction> newFunctionCall(final String context,
            final ABooleanNullableFunction function, final IParsedExpression[] parameters) {
        return new BooleanNullableFunctionCall(context, function, parameters);
    }

}
