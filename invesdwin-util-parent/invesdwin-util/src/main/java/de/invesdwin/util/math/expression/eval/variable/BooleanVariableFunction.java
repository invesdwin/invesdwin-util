package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.function.ABooleanFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.tokenizer.ExpressionContextUtil;

@Immutable
public class BooleanVariableFunction extends ABooleanFunction {

    private final BooleanVariableReference variable;

    public BooleanVariableFunction(final BooleanVariableReference variable) {
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
    public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context, final IExpression[] args) {
        return variable.newEvaluateBooleanFDate();
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey(final String context, final IExpression[] args) {
        return variable.newEvaluateBooleanKey();
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean(final String context, final IExpression[] args) {
        return variable.newEvaluateBoolean();
    }

    @Override
    public boolean isNaturalFunction(final IExpression[] args) {
        return variable.isConstant();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String context = variable.getContext();
        ExpressionContextUtil.putContext(context, sb);
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
