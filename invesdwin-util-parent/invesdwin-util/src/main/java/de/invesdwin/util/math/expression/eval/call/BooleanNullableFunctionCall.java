package de.invesdwin.util.math.expression.eval.call;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ABooleanNullableFunction;
import de.invesdwin.util.time.fdate.FDate;

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
    public double evaluateDouble(final FDate key) {
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
    public Boolean evaluateBooleanNullable(final FDate key) {
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
    protected AFunctionCall<ABooleanNullableFunction> newFunctionCall(final String context,
            final ABooleanNullableFunction function, final IParsedExpression[] parameters) {
        return new BooleanNullableFunctionCall(context, function, parameters);
    }

}
