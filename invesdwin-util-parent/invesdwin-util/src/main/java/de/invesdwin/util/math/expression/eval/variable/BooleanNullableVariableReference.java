package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.variable.IBooleanNullableVariable;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class BooleanNullableVariableReference extends AVariableReference<IBooleanNullableVariable> {

    public BooleanNullableVariableReference(final String context, final IBooleanNullableVariable variable) {
        super(context, variable);
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
    public boolean evaluateBoolean(final FDate key) {
        return variable.getValue(key);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return variable.getValue(key);
    }

    @Override
    public boolean evaluateBoolean() {
        return variable.getValue();
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
    public AFunction asFunction() {
        return new BooleanNullableVariableFunction(this);
    }
}
