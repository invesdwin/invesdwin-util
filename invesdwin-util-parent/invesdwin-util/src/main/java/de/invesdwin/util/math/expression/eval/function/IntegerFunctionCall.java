package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.AIntegerFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

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
    public int evaluateInteger(final IFDateProvider key) {
        return function.eval(key, parameters);
    }

    @Override
    public int evaluateInteger(final int key) {
        return function.eval(key, parameters);
    }

    @Override
    public int evaluateInteger() {
        return function.eval(parameters);
    }

    @Override
    public double evaluateDouble() {
        return function.eval(parameters);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        return function.eval(key, parameters);
    }

    @Override
    public double evaluateDouble(final int key) {
        return function.eval(key, parameters);
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final int eval = function.eval(key, parameters);
        return Integers.toBooleanNullable(eval);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final int eval = function.eval(key, parameters);
        return Integers.toBooleanNullable(eval);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final int eval = function.eval(parameters);
        return Integers.toBooleanNullable(eval);
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        final int eval = function.eval(key, parameters);
        return Integers.toBoolean(eval);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final int eval = function.eval(key, parameters);
        return Integers.toBoolean(eval);
    }

    @Override
    public boolean evaluateBoolean() {
        final int eval = function.eval(parameters);
        return Integers.toBoolean(eval);
    }

    @Override
    protected AFunctionCall<AIntegerFunction> newFunctionCall(final String context, final AIntegerFunction function,
            final IParsedExpression[] parameters) {
        return new IntegerFunctionCall(context, function, parameters);
    }
}
