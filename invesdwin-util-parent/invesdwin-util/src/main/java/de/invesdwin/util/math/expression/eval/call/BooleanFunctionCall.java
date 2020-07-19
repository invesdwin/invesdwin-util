package de.invesdwin.util.math.expression.eval.call;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.function.ABooleanFunction;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class BooleanFunctionCall extends AFunctionCall<ABooleanFunction> {

    public BooleanFunctionCall(final String context, final ABooleanFunction function,
            final IParsedExpression[] parameters) {
        super(context, function, parameters);
    }

    public BooleanFunctionCall(final String context, final ABooleanFunction function,
            final IParsedExpression parameter) {
        super(context, function, parameter);
    }

    public BooleanFunctionCall(final String context, final ABooleanFunction function) {
        super(context, function);
    }

    @Override
    public double evaluateDouble(final FDate key) {
        return Doubles.booleanToDouble(evaluateBoolean(key));
    }

    @Override
    public double evaluateDouble(final int key) {
        return Doubles.booleanToDouble(evaluateBoolean(key));
    }

    @Override
    public double evaluateDouble() {
        return Doubles.booleanToDouble(evaluateBoolean());
    }

    @Override
    public Boolean evaluateBooleanNullable(final FDate key) {
        return evaluateBoolean(key);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return evaluateBoolean(key);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return evaluateBoolean();
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        final boolean eval = function.eval(key, parameters);
        return eval;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final boolean eval = function.eval(key, parameters);
        return eval;
    }

    @Override
    public boolean evaluateBoolean() {
        final boolean eval = function.eval(parameters);
        return eval;
    }

    @Override
    protected AFunctionCall<ABooleanFunction> newFunctionCall(final String context, final ABooleanFunction function,
            final IParsedExpression[] parameters) {
        return new BooleanFunctionCall(context, function, parameters);
    }

}
