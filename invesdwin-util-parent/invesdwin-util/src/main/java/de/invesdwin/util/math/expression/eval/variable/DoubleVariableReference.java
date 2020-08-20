package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.variable.IDoubleVariable;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleVariableReference extends AVariableReference<IDoubleVariable> {

    public DoubleVariableReference(final String context, final IDoubleVariable variable) {
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
        return Integers.checkedCastNoOverflow(variable.getValue(key));
    }

    @Override
    public int evaluateInteger(final int key) {
        return Integers.checkedCastNoOverflow(variable.getValue(key));
    }

    @Override
    public int evaluateInteger() {
        return Integers.checkedCastNoOverflow(variable.getValue());
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        return Doubles.toBooleanNullable(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return Doubles.toBooleanNullable(variable.getValue(key));
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return Doubles.toBooleanNullable(variable.getValue());
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return Doubles.toBoolean(variable.getValue(key));
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return Doubles.toBoolean(variable.getValue(key));
    }

    @Override
    public boolean evaluateBoolean() {
        return Doubles.toBoolean(variable.getValue());
    }

    @Override
    public AFunction asFunction() {
        return new DoubleVariableFunction(this);
    }
}
