package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.variable.IIntegerVariable;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class IntegerVariableReference extends AVariableReference<IIntegerVariable> {

    public IntegerVariableReference(final String context, final IIntegerVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        return variable.getValue(key);
    }

    @Override
    public double evaluateDouble(final int key) {
        return variable.getValue(key);
    }

    @Override
    public double evaluateDouble() {
        return variable.getValue();
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        return variable.getValue(key);
    }

    @Override
    public int evaluateInteger(final int key) {
        return variable.getValue(key);
    }

    @Override
    public int evaluateInteger() {
        return variable.getValue();
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        return Integers.integerToBooleanNullable(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return Integers.integerToBooleanNullable(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return Integers.integerToBooleanNullable(variable.getValue());
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return Integers.integerToBoolean(variable.getValue(key));
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return Integers.integerToBoolean(variable.getValue(key));
    }

    @Override
    public boolean evaluateBoolean() {
        return Integers.integerToBoolean(variable.getValue());
    }

    @Override
    public AFunction asFunction() {
        return new IntegerVariableFunction(this);
    }
}
