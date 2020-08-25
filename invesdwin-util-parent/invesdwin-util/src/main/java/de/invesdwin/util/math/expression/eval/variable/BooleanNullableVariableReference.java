package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.function.AFunction;
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
import de.invesdwin.util.math.expression.variable.IBooleanNullableVariable;

@Immutable
public class BooleanNullableVariableReference extends AVariableReference<IBooleanNullableVariable> {

    public BooleanNullableVariableReference(final String context, final IBooleanNullableVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Doubles.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Doubles.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Doubles.fromBoolean(f.evaluateBooleanNullable());
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Integers.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Integers.fromBoolean(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Integers.fromBoolean(f.evaluateBooleanNullable());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateBooleanNullableFDate f = newEvaluateBooleanNullableFDate();
        return key -> Booleans.isTrue(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateBooleanNullableKey f = newEvaluateBooleanNullableKey();
        return key -> Booleans.isTrue(f.evaluateBooleanNullable(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateBooleanNullable f = newEvaluateBooleanNullable();
        return () -> Booleans.isTrue(f.evaluateBooleanNullable());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        return variable.newEvaluateBooleanNullableFDate();
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        return variable.newEvaluateBooleanNullableKey();
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        return variable.newEvaluateBooleanNullable();
    }

    @Override
    public AFunction asFunction() {
        return new BooleanNullableVariableFunction(this);
    }
}
