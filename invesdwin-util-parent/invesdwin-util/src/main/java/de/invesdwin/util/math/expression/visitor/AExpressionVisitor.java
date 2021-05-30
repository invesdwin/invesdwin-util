package de.invesdwin.util.math.expression.visitor;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;

@Immutable
public abstract class AExpressionVisitor {

    public void process(final IExpression expression) {
        if (expression == null) {
            return;
        } else if (expression instanceof IBinaryOperation) {
            final IBinaryOperation cExpression = (IBinaryOperation) expression;
            final boolean visitChildren = processBinaryOperation(cExpression);
            if (visitChildren) {
                process(cExpression.getLeft());
                process(cExpression.getRight());
            }
        } else {
            visitOther(expression);
        }
    }

    protected boolean processBinaryOperation(final IBinaryOperation expression) {
        final boolean visitChildren;
        switch (expression.getOp()) {
        case AND:
        case PAND:
        case OR:
        case POR:
            visitChildren = visitLogicalCombination(expression);
            break;
        case CROSSES_ABOVE:
        case CROSSES_BELOW:
        case EQ:
        case GT:
        case GT_EQ:
        case LT:
        case LT_EQ:
        case NEQ:
            visitChildren = visitComparison(expression);
            break;
        case DIVIDE:
        case MODULO:
        case MULTIPLY:
        case NOT:
        case POWER:
        case SUBTRACT:
        case ADD:
            visitChildren = visitMath(expression);
            break;
        default:
            throw UnknownArgumentException.newInstance(Op.class, expression.getOp());
        }
        return visitChildren;
    }

    protected abstract boolean visitLogicalCombination(IBinaryOperation expression);

    protected abstract boolean visitComparison(IBinaryOperation expression);

    protected abstract boolean visitMath(IBinaryOperation expression);

    protected abstract void visitOther(IExpression expression);

}
