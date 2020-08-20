package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class DoubleAndOperation extends DoubleBinaryOperation {

    public DoubleAndOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.AND, left, right);
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
        if (leftResult == Boolean.FALSE) {
            return Boolean.FALSE;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final Boolean leftResult = left.evaluateBooleanNullable(key);
        if (leftResult == Boolean.FALSE) {
            return Boolean.FALSE;
        } else {
            return right.evaluateBooleanNullable(key);
        }
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final Boolean leftResult = left.evaluateBooleanNullable();
        if (leftResult == Boolean.FALSE) {
            return Boolean.FALSE;
        } else {
            return right.evaluateBooleanNullable();
        }
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        return left.evaluateBoolean(key) && right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        return left.evaluateBoolean(key) && right.evaluateBoolean(key);
    }

    @Override
    public boolean evaluateBoolean() {
        return left.evaluateBoolean() && right.evaluateBoolean();
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
            if (leftResult == null || leftResult == Boolean.TRUE) {
                if (newRight.isConstant()) {
                    final Boolean rightResult = newRight.evaluateBooleanNullable();
                    if (rightResult != null) {
                        return new ConstantExpression(Doubles.booleanToDouble(rightResult),
                                ExpressionType.determineBooleanType(rightResult));
                    } else {
                        return new ConstantExpression(Doubles.booleanToDouble(leftResult),
                                ExpressionType.determineBooleanType(leftResult));
                    }
                } else {
                    return newRight;
                }
            } else {
                return new ConstantExpression(0D, ExpressionType.Boolean);
            }
        }
        if (newRight.isConstant()) {
            final Boolean rightResult = newRight.evaluateBooleanNullable();
            if (rightResult == null || rightResult == Boolean.TRUE) {
                if (newLeft.isConstant()) {
                    final Boolean leftResult = newLeft.evaluateBooleanNullable();
                    if (leftResult != null) {
                        return new ConstantExpression(Doubles.booleanToDouble(leftResult), getType());
                    } else {
                        return new ConstantExpression(Doubles.booleanToDouble(rightResult), getType());
                    }
                } else {
                    return newLeft;
                }
            } else {
                return new ConstantExpression(0D, getType());
            }
        }
        return simplify(newLeft, newRight);
    }

    @Override
    protected DoubleBinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new DoubleAndOperation(left, right);
    }

}
