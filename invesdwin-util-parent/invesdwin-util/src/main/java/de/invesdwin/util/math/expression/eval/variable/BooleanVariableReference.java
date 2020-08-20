package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.variable.IBooleanVariable;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class BooleanVariableReference extends AVariableReference<IBooleanVariable> {

    public BooleanVariableReference(final String context, final IBooleanVariable variable) {
        super(context, variable);
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
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
    public double evaluateDouble(final IFDateProvider key) {
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
    public int evaluateInteger(final IFDateProvider key) {
        return Integers.booleanToInteger(evaluateBoolean(key));
    }

    @Override
    public int evaluateInteger(final int key) {
        return Integers.booleanToInteger(evaluateBoolean(key));
    }

    @Override
    public int evaluateInteger() {
        return Integers.booleanToInteger(evaluateBoolean());
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
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
        return new BooleanVariableFunction(this);
    }
}
