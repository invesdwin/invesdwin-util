package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class NotOperation extends BinaryOperation {

    public NotOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.NOT, left, right);
    }

    @Override
    public double evaluateDouble(final FDate key) {
        final Boolean check = evaluateBooleanNullable(key);
        if (check == null) {
            return Double.NaN;
        } else if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public double evaluateDouble(final int key) {
        final Boolean check = evaluateBooleanNullable(key);
        if (check == null) {
            return Double.NaN;
        } else if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public double evaluateDouble() {
        final Boolean check = evaluateBooleanNullable();
        if (check == null) {
            return Double.NaN;
        } else if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public Boolean evaluateBooleanNullable(final FDate key) {
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
    public boolean isConstant() {
        return false;
    }

    @Override
    protected BinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new NotOperation(left, right);
    }

    @Override
    public String toString() {
        return "!" + getRight();
    }

}
