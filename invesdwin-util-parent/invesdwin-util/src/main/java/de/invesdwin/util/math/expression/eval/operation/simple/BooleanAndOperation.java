package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableAndOperation;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public class BooleanAndOperation extends BooleanNullableAndOperation {

    public BooleanAndOperation(final IParsedExpression left, final IParsedExpression right) {
        super(left, right);
    }

    @Override
    public double evaluateDouble(final IFDateProvider key) {
        final boolean check = evaluateBoolean(key);
        return Doubles.fromBoolean(check);
    }

    @Override
    public double evaluateDouble(final int key) {
        final boolean check = evaluateBoolean(key);
        return Doubles.fromBoolean(check);
    }

    @Override
    public double evaluateDouble() {
        final boolean check = evaluateBoolean();
        return Doubles.fromBoolean(check);
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        final boolean check = evaluateBoolean(key);
        return Integers.fromBoolean(check);
    }

    @Override
    public int evaluateInteger(final int key) {
        final boolean check = evaluateBoolean(key);
        return Integers.fromBoolean(check);
    }

    @Override
    public int evaluateInteger() {
        final boolean check = evaluateBoolean();
        return Integers.fromBoolean(check);
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        return evaluateBoolean(key);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        return evaluateBoolean(key);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        return evaluateBoolean();
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
