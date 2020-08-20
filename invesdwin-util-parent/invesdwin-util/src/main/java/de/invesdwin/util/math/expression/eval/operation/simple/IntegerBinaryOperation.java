package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IDoubleFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IIntegerFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@Immutable
public class IntegerBinaryOperation extends DoubleBinaryOperation {

    public IntegerBinaryOperation(final Op op, final IParsedExpression left, final IParsedExpression right) {
        super(op, left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyDoubleFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyDoubleFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateInteger leftF = left.newEvaluateInteger();
        final IEvaluateInteger rightF = right.newEvaluateInteger();
        final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
        return () -> {
            final int a = leftF.evaluateInteger();
            final int b = rightF.evaluateInteger();
            return opF.applyDoubleFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyIntegerFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyIntegerFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateInteger leftF = left.newEvaluateInteger();
        final IEvaluateInteger rightF = right.newEvaluateInteger();
        final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
        return () -> {
            final int a = leftF.evaluateInteger();
            final int b = rightF.evaluateInteger();
            return opF.applyIntegerFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyBooleanNullableFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyBooleanNullableFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateInteger leftF = left.newEvaluateInteger();
        final IEvaluateInteger rightF = right.newEvaluateInteger();
        final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
        return () -> {
            final int a = leftF.evaluateInteger();
            final int b = rightF.evaluateInteger();
            return opF.applyBooleanNullableFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
        final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
        final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyBooleanFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
        final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
        final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
        return key -> {
            final int a = leftF.evaluateInteger(key);
            final int b = rightF.evaluateInteger(key);
            return opF.applyBooleanFromIntegers(a, b);
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateInteger leftF = left.newEvaluateInteger();
        final IEvaluateInteger rightF = right.newEvaluateInteger();
        final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
        return () -> {
            final int a = leftF.evaluateInteger();
            final int b = rightF.evaluateInteger();
            return opF.applyBooleanFromIntegers(a, b);
        };
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
