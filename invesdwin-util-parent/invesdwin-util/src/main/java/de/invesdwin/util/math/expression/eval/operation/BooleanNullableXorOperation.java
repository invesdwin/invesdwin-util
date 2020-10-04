package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.BooleanXorOperation;
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
public class BooleanNullableXorOperation extends DoubleBinaryOperation {

    public BooleanNullableXorOperation(final IParsedExpression left, final IParsedExpression right) {
        super(Op.XOR, left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean check = f.evaluateBooleanNullable(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> {
            final Boolean check = f.evaluateBooleanNullable();
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            final boolean check = f.evaluateBoolean();
            return Integers.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateBooleanNullableFDate leftF = left.newEvaluateBooleanNullableFDate();
        final IEvaluateBooleanNullableFDate rightF = right.newEvaluateBooleanNullableFDate();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            return Booleans.xor(leftResult, rightResult);
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateBooleanNullableKey leftF = left.newEvaluateBooleanNullableKey();
        final IEvaluateBooleanNullableKey rightF = right.newEvaluateBooleanNullableKey();
        return key -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable(key);
            final Boolean rightResult = rightF.evaluateBooleanNullable(key);
            return Booleans.xor(leftResult, rightResult);
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateBooleanNullable leftF = left.newEvaluateBooleanNullable();
        final IEvaluateBooleanNullable rightF = right.newEvaluateBooleanNullable();
        return () -> {
            final Boolean leftResult = leftF.evaluateBooleanNullable();
            final Boolean rightResult = rightF.evaluateBooleanNullable();
            return Booleans.xor(leftResult, rightResult);
        };
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateBooleanFDate leftF = left.newEvaluateBooleanFDate();
        final IEvaluateBooleanFDate rightF = right.newEvaluateBooleanFDate();
        return key -> Booleans.xor(leftF.evaluateBoolean(key), rightF.evaluateBoolean(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
        final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
        return key -> Booleans.xor(leftF.evaluateBoolean(key), rightF.evaluateBoolean(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateBoolean leftF = left.newEvaluateBoolean();
        final IEvaluateBoolean rightF = right.newEvaluateBoolean();
        return () -> Booleans.xor(leftF.evaluateBoolean(), rightF.evaluateBoolean());
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public IParsedExpression simplify() {
        final IParsedExpression newLeft = left.simplify();
        final IParsedExpression newRight = right.simplify();
        return simplify(newLeft, newRight);
    }

    @Override
    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new BooleanNullableXorOperation(left, right);
        } else if (simplifyType == ExpressionType.Boolean) {
            return new BooleanXorOperation(left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

}
