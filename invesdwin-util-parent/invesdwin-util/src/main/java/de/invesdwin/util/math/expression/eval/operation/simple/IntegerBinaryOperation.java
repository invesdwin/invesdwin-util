package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class IntegerBinaryOperation extends DoubleBinaryOperation {

    public IntegerBinaryOperation(final Op op, final IParsedExpression left, final IParsedExpression right) {
        super(op, left, right);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyInteger(a, b);
    }

    @Override
    public double evaluateDouble(final int key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyInteger(a, b);
    }

    @Override
    public double evaluateDouble() {
        final int a = left.evaluateInteger();
        final int b = right.evaluateInteger();

        return op.applyDouble(a, b);
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyInteger(a, b);
    }

    @Override
    public int evaluateInteger(final int key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyInteger(a, b);
    }

    @Override
    public int evaluateInteger() {
        final int a = left.evaluateInteger();
        final int b = right.evaluateInteger();

        return op.applyInteger(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final int a = left.evaluateInteger();
        final int b = right.evaluateInteger();

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyBoolean(a, b);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final int a = left.evaluateInteger(key);
        final int b = right.evaluateInteger(key);

        return op.applyBoolean(a, b);
    }

    @Override
    public boolean evaluateBoolean() {
        final int a = left.evaluateInteger();
        final int b = right.evaluateInteger();

        return op.applyBoolean(a, b);
    }

    @Override
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        throw new UnsupportedOperationException("already simplified");
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
