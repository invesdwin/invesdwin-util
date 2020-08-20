package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;

@Immutable
public class ExpressionVisitorSupport extends AExpressionVisitor {

    @Override
    protected boolean visitLogicalCombination(final DoubleBinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitComparison(final DoubleBinaryOperation expression) {
        return false;
    }

    @Override
    protected boolean visitMath(final DoubleBinaryOperation expression) {
        return false;
    }

    @Override
    protected void visitOther(final IExpression expression) {}

}
