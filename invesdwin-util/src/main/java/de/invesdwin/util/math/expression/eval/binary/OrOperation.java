package de.invesdwin.util.math.expression.eval.binary;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class OrOperation extends BinaryOperation {

    public OrOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.OR, left, right);
    }

    @Override
    public double evaluateDouble(final FDate key) {
        final boolean check = left.evaluateBoolean(key) || right.evaluateBoolean(key);
        if (check) {
            return 1D;
        } else {
            return -1D;
        }
    }

    @Override
    public double evaluateDouble(final int key) {
        final boolean check = left.evaluateBoolean(key) || right.evaluateBoolean(key);
        if (check) {
            return 1D;
        } else {
            return -1D;
        }
    }

    @Override
    public double evaluateDouble() {
        final boolean check = left.evaluateBoolean() || right.evaluateBoolean();
        if (check) {
            return 1D;
        } else {
            return -1D;
        }
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        return left.evaluateBoolean(key) || right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return left.evaluateBoolean(key) || right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean() {
        return left.evaluateBoolean() || right.evaluateBoolean();
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
        return new OrOperation(left, right);
    }

}
