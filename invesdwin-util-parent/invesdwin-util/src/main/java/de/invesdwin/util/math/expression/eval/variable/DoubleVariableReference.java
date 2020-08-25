package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

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
import de.invesdwin.util.math.expression.variable.IDoubleVariable;

@Immutable
public class DoubleVariableReference extends AVariableReference<IDoubleVariable> {

    public DoubleVariableReference(final String context, final IDoubleVariable variable) {
        super(context, variable);
    }

    @Override
    public ExpressionType getType() {
        return variable.getType();
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return variable.newEvaluateDoubleFDate();
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        return variable.newEvaluateDoubleKey();
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        return variable.newEvaluateDouble();
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateDoubleFDate f = newEvaluateDoubleFDate();
        return key -> Integers.checkedCastNoOverflow(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Integers.checkedCastNoOverflow(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Integers.checkedCastNoOverflow(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateDoubleFDate f = newEvaluateDoubleFDate();
        return key -> Doubles.toBooleanNullable(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Doubles.toBooleanNullable(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Doubles.toBooleanNullable(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateDouble f = newEvaluateDouble();
        return key -> Doubles.toBoolean(f.evaluateDouble());
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateDoubleKey f = newEvaluateDoubleKey();
        return key -> Doubles.toBoolean(f.evaluateDouble(key));
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateDouble f = newEvaluateDouble();
        return () -> Doubles.toBoolean(f.evaluateDouble());
    }

    @Override
    public AFunction asFunction() {
        return new DoubleVariableFunction(this);
    }
}
