package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleNotOperation extends DoubleBinaryOperation {

    public DoubleNotOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.NOT, left, right);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Doubles.booleanToDouble(check);
    }

    @Override
    public double evaluateDouble(final int key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Doubles.booleanToDouble(check);
    }

    @Override
    public double evaluateDouble() {
        final Boolean check = evaluateBooleanNullable();
        return Doubles.booleanToDouble(check);
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final Boolean check = right.evaluateBooleanNullable(key);
        if (check == null) {
            return null;
        } else if (check == Boolean.TRUE) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final Boolean check = right.evaluateBooleanNullable(key);
        if (check == null) {
            return null;
        } else if (check == Boolean.TRUE) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final Boolean check = right.evaluateBooleanNullable();
        if (check == null) {
            return null;
        } else if (check == Boolean.TRUE) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return !right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return !right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean() {
        return !right.evaluateBoolean();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    protected DoubleBinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new DoubleNotOperation(left, right);
    }

    @Override
    public String toString() {
        return "!" + getRight();
    }

}
