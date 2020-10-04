package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableXorOperation;
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
public class BooleanXorOperation extends BooleanNullableXorOperation {

    public BooleanXorOperation(final IParsedExpression left, final IParsedExpression right) {
        super(left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            final boolean check = f.evaluateBoolean(key);
            return Doubles.fromBoolean(check);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            final boolean check = f.evaluateBoolean();
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
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> f.evaluateBoolean(key);
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> f.evaluateBoolean(key);
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> f.evaluateBoolean();
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
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
