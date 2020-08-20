package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;

@Immutable
public class ExpressionVisitorSupport extends AExpressionVisitor {

    @Override
    protected boolean visitLogicalCombination(final IBinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitComparison(final IBinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitMath(final IBinaryOperation expression) {
        return false;
    }

    @Override
    protected void visitOther(final IExpression expression) {
    }

}
