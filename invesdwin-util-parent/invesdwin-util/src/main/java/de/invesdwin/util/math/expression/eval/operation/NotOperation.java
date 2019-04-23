package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class NotOperation extends BinaryOperation {

    public NotOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.NOT, left, right);
    }

    @Override
    public double evaluateDouble(final FDate key) {
        final boolean check = right.evaluateBoolean(key);
        if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public double evaluateDouble(final int key) {
        final boolean check = right.evaluateBoolean(key);
        if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public double evaluateDouble() {
        final boolean check = right.evaluateBoolean();
        if (check) {
            return 0D;
        } else {
            return 1D;
        }
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        final boolean check = right.evaluateBoolean(key);
        return !check;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final boolean check = right.evaluateBoolean(key);
        return !check;
    }

    @Override
    public boolean evaluateBoolean() {
        final boolean check = right.evaluateBoolean();
        return !check;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    protected IParsedExpression newConstantExpression() {
        //expression will never be true
        return new ConstantExpression(0D);
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
