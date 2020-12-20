package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

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
import de.invesdwin.util.math.expression.variable.IIntegerVariable;

@Immutable
public class IntegerVariableReference extends AVariableReference<IIntegerVariable> {

    public IntegerVariableReference(final String context, final IIntegerVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> f.evaluateInteger(key);
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> f.evaluateInteger(key);
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> f.evaluateInteger();
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        return variable.newEvaluateIntegerFDate(getContext());
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        return variable.newEvaluateIntegerKey(getContext());
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        return variable.newEvaluateInteger(getContext());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> Integers.toBooleanNullable(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> Integers.toBooleanNullable(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> Integers.toBooleanNullable(f.evaluateInteger());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateIntegerFDate f = newEvaluateIntegerFDate();
        return key -> Integers.toBoolean(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateIntegerKey f = newEvaluateIntegerKey();
        return key -> Integers.toBoolean(f.evaluateInteger(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateInteger f = newEvaluateInteger();
        return () -> Integers.toBoolean(f.evaluateInteger());
    }

    @Override
    public AFunction asFunction() {
        return new IntegerVariableFunction(this);
    }
}
