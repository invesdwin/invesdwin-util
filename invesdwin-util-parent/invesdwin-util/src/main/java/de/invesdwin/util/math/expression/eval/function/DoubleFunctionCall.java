package de.invesdwin.util.math.expression.eval.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ADoubleFunction;
import de.invesdwin.util.time.fdate.IFDateProvider;

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
    public double evaluateDouble(final IFDateProvider key) {
        return function.eval(key, parameters);
    }

    @Override
    public double evaluateDouble(final int key) {
        return function.eval(key, parameters);
    }

    @Override
    public double evaluateDouble() {
        return function.eval(parameters);
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        return Integers.checkedCastNoOverflow(function.eval(key, parameters));
    }

    @Override
    public int evaluateInteger(final int key) {
        return Integers.checkedCastNoOverflow(function.eval(key, parameters));
    }

    @Override
    public int evaluateInteger() {
        return Integers.checkedCastNoOverflow(function.eval(parameters));
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final double eval = function.eval(key, parameters);
        return Doubles.toBooleanNullable(eval);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final double eval = function.eval(key, parameters);
        return Doubles.toBooleanNullable(eval);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final double eval = function.eval(parameters);
        return Doubles.toBooleanNullable(eval);
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        final double eval = function.eval(key, parameters);
        return Doubles.toBoolean(eval);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final double eval = function.eval(key, parameters);
        return Doubles.toBoolean(eval);
    }

    @Override
    public boolean evaluateBoolean() {
        final double eval = function.eval(parameters);
        return Doubles.toBoolean(eval);
    }

    @Override
    protected AFunctionCall<ADoubleFunction> newFunctionCall(final String context, final ADoubleFunction function,
            final IParsedExpression[] parameters) {
        return new DoubleFunctionCall(context, function, parameters);
    }
}
