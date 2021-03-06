package de.invesdwin.util.math.expression.eval.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.function.AIntegerFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.math.expression.tokenizer.ExpressionContextUtil;

@Immutable
public class IntegerVariableFunction extends AIntegerFunction {

    private final IntegerVariableReference variable;

    public IntegerVariableFunction(final IntegerVariableReference variable) {
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
    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context, final IExpression[] args) {
        return variable.newEvaluateIntegerFDate();
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey(final String context, final IExpression[] args) {
        return variable.newEvaluateIntegerKey();
    }

    @Override
    public IEvaluateInteger newEvaluateInteger(final String context, final IExpression[] args) {
        return variable.newEvaluateInteger();
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
    public Object getProperty(final String property) {
        return variable.getProperty(property);
    }

}
