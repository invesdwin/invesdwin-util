package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.variable.IDoubleVariable;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleVariableReference extends AVariableReference<IDoubleVariable> {

    public DoubleVariableReference(final String context, final IDoubleVariable variable) {
        super(context, variable);
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
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        return Doubles.doubleToBoolean(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return Doubles.doubleToBoolean(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return Doubles.doubleToBoolean(variable.getValue());
    }

    @Override
    public AFunction asFunction() {
        return new DoubleVariableFunction(this);
    }
}
