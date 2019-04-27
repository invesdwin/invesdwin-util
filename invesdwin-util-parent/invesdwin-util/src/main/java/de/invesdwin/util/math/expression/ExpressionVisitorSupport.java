package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.operation.BinaryOperation;

@Immutable
public class ExpressionVisitorSupport extends AExpressionVisitor {

    @Override
    protected boolean visitLogicalCombination(final BinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitComparison(final BinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitMath(final BinaryOperation expression) {
        return false;
    }

    @Override
    protected void visitOther(final IExpression expression) {}

}
