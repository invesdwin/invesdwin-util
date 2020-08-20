package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.BooleanNotOperation;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class BooleanNullableNotOperation extends DoubleBinaryOperation {

    public BooleanNullableNotOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.NOT, left, right);
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
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new BooleanNullableNotOperation(left, right);
        } else if (simplifyType == ExpressionType.Boolean) {
            return new BooleanNotOperation(left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

    @Override
    public String toString() {
        return "!" + getRight();
    }

}
