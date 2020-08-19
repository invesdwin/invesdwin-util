package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class OrOperation extends BinaryOperation {

    public OrOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.OR, left, right);
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
        final Boolean leftResult = left.evaluateBooleanNullable(key);
        if (leftResult == Boolean.TRUE) {
            return Boolean.TRUE;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final Boolean leftResult = left.evaluateBooleanNullable(key);
        if (leftResult == Boolean.TRUE) {
            return Boolean.TRUE;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final Boolean leftResult = left.evaluateBooleanNullable();
        if (leftResult == Boolean.TRUE) {
            return Boolean.TRUE;
        } else {
            return right.evaluateBooleanNullable();
        }
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        final boolean leftResult = left.evaluateBoolean(key);
        if (leftResult) {
            return true;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final boolean leftResult = left.evaluateBoolean(key);
        if (leftResult) {
            return true;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public boolean evaluateBoolean() {
        final boolean leftResult = left.evaluateBoolean();
        if (leftResult) {
            return true;
        } else {
            return right.evaluateBooleanNullable();
        }
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public IParsedExpression simplify() {
        final IParsedExpression newLeft = left.simplify();
        final IParsedExpression newRight = right.simplify();
        if (newLeft.isConstant()) {
            final Boolean leftResult = newLeft.evaluateBooleanNullable();
            if (leftResult != null && leftResult == Boolean.TRUE) {
                return new ConstantExpression(1D);
            } else {
                if (newRight.isConstant()) {
                    final Boolean rightResult = newRight.evaluateBooleanNullable();
                    if (rightResult != null) {
                        return new ConstantExpression(Doubles.booleanToDouble(rightResult));
                    } else {
                        return new ConstantExpression(Doubles.booleanToDouble(leftResult));
                    }
                } else {
                    return newRight;
                }
            }
        }
        if (newRight.isConstant()) {
            final Boolean rightResult = newRight.evaluateBooleanNullable();
            if (rightResult != null && rightResult == Boolean.TRUE) {
                return new ConstantExpression(1D);
            } else {
                if (newLeft.isConstant()) {
                    final Boolean leftResult = newLeft.evaluateBooleanNullable();
                    if (leftResult != null) {
                        return new ConstantExpression(Doubles.booleanToDouble(leftResult));
                    } else {
                        return new ConstantExpression(Doubles.booleanToDouble(rightResult));
                    }
                } else {
                    return newLeft;
                }
            }
        }
        return simplify(newLeft, newRight);
    }

    @Override
    protected BinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new OrOperation(left, right);
    }

}
