package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableParallelAndOperation;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateGeneric;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@Immutable
public class BooleanParallelAndOperation extends BooleanNullableParallelAndOperation {

    public BooleanParallelAndOperation(final IParsedExpression left, final IParsedExpression right) {
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
        return key -> {
            final boolean left = leftF.evaluateBoolean(key);
            final boolean right = rightF.evaluateBoolean(key);
            return left && right;
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanKey leftF = left.newEvaluateBooleanKey();
        final IEvaluateBooleanKey rightF = right.newEvaluateBooleanKey();
        return key -> {
            final boolean left = leftF.evaluateBoolean(key);
            final boolean right = rightF.evaluateBoolean(key);
            return left && right;
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateBoolean leftF = left.newEvaluateBoolean();
        final IEvaluateBoolean rightF = right.newEvaluateBoolean();
        return () -> {
            final boolean left = leftF.evaluateBoolean();
            final boolean right = rightF.evaluateBoolean();
            return left && right;
        };
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateTrueReasonKey() {
        final IEvaluateBooleanKey f = newEvaluateBooleanKey();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return BooleanParallelAndOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateTrueReason() {
        final IEvaluateBoolean f = newEvaluateBoolean();
        return () -> {
            if (f.evaluateBoolean()) {
                return BooleanParallelAndOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateTrueReasonFDate() {
        final IEvaluateBooleanFDate f = newEvaluateBooleanFDate();
        return key -> {
            if (f.evaluateBoolean(key)) {
                return BooleanParallelAndOperation.this.toString();
            } else {
                return null;
            }
        };
    }

    @Override
    public IEvaluateGeneric<String> newEvaluateNullReason() {
        //not nullable
        return () -> null;
    }

    @Override
    public IEvaluateGenericFDate<String> newEvaluateNullReasonFDate() {
        //not nullable
        return key -> null;
    }

    @Override
    public IEvaluateGenericKey<String> newEvaluateNullReasonKey() {
        //not nullable
        return key -> null;
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
