package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.function.ABooleanNullableFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;

@Immutable
public class BooleanNullableVariableFunction extends ABooleanNullableFunction {

    private final BooleanNullableVariableReference variable;

    public BooleanNullableVariableFunction(final BooleanNullableVariableReference variable) {
        this.variable = variable;
    }

    @Override
    public String getExpressionName() {
        return variable.getVariable().getExpressionName();
    }

    @Override
    public String getName() {
        return variable.getVariable().getName();
    }

    @Override
    public String getDescription() {
        return variable.getVariable().getDescription();
    }

    @Override
    public IFunctionParameterInfo getParameterInfo(final int index) {
        throw new ArrayIndexOutOfBoundsException(index);
    }

    @Override
    public int getNumberOfArguments() {
        return 0;
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate(final IExpression[] args) {
        return variable.newEvaluateBooleanNullableFDate();
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey(final IExpression[] args) {
        return variable.newEvaluateBooleanNullableKey();
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable(final IExpression[] args) {
        return variable.newEvaluateBooleanNullable();
    }

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return variable.isConstant();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String context = variable.getContext();
        if (context != null) {
            sb.append(context);
            sb.append(":");
        }
        sb.append(getExpressionName());
        return sb.toString();
    }

    @Override
    public ExpressionReturnType getReturnType() {
        return variable.getVariable().getReturnType();
    }

    @Override
    public boolean shouldPersist() {
        return variable.shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return variable.shouldDraw();
    }

}
