package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.BooleanAndOperation;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class BooleanNullableAndOperation extends DoubleBinaryOperation {

    public BooleanNullableAndOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.AND, left, right);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Doubles.fromBoolean(check);
    }

    @Override
    public double evaluateDouble(final int key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Doubles.fromBoolean(check);
    }

    @Override
    public double evaluateDouble() {
        final Boolean check = evaluateBooleanNullable();
        return Doubles.fromBoolean(check);
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Integers.fromBoolean(check);
    }

    @Override
    public int evaluateInteger(final int key) {
        final Boolean check = evaluateBooleanNullable(key);
        return Integers.fromBoolean(check);
    }

    @Override
    public int evaluateInteger() {
        final Boolean check = evaluateBooleanNullable();
        return Integers.fromBoolean(check);
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
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
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
                        return new ConstantExpression(Doubles.fromBoolean(leftResult),
                                ExpressionType.determineSmallestBooleanType(leftResult));
                    } else {
                        return new ConstantExpression(Doubles.fromBoolean(rightResult),
                                ExpressionType.determineSmallestBooleanType(rightResult));
                    }
                } else {
                    return newLeft;
                }
            } else {
                return new ConstantExpression(0D, ExpressionType.Boolean);
            }
        }
        return simplify(newLeft, newRight);
    }

    @Override
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new BooleanNullableAndOperation(left, right);
        } else if (simplifyType == ExpressionType.Boolean) {
            return new BooleanAndOperation(left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}