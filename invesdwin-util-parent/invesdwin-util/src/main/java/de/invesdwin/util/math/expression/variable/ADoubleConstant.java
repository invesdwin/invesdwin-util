package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;

@Immutable
public abstract class ADoubleConstant extends AConstant implements IDoubleVariable {

    private final double value;

    public ADoubleConstant(final double value) {
        this.value = value;
    }

    @Override
    public final IEvaluateDouble newEvaluateDouble() {
        return () -> value;
    }

    @Override
    public final IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        return key -> value;
    }

    @Override
    public final IEvaluateDoubleKey newEvaluateDoubleKey() {
        return key -> value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + value;
    }

}
