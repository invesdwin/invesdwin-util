package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation.Op;

@Immutable
public abstract class AExpressionVisitor {

    public void process(final IExpression expression) {
        if (expression instanceof BinaryOperation) {
            final BinaryOperation cExpression = (BinaryOperation) expression;
            final boolean visitChildren;
            switch (cExpression.getOp()) {
            case AND:
            case OR:
                visitChildren = visitLogicalCombination(cExpression);
                break;
            case CROSSES_ABOVE:
            case CROSSES_BELOW:
            case EQ:
            case GT:
            case GT_EQ:
            case LT:
            case LT_EQ:
            case NEQ:
                visitChildren = visitComparison(cExpression);
                break;
            case DIVIDE:
            case MODULO:
            case MULTIPLY:
            case NOT:
            case POWER:
            case SUBTRACT:
            case ADD:
                visitChildren = visitMath(cExpression);
                break;
            default:
                throw UnknownArgumentException.newInstance(Op.class, cExpression.getOp());
            }
            if (visitChildren) {
                process(cExpression.getLeft());
                process(cExpression.getRight());
            }
        } else {
            visitOther(expression);
        }
    }

    protected abstract boolean visitLogicalCombination(BinaryOperation expression);

    protected abstract boolean visitComparison(BinaryOperation expression);

    protected abstract boolean visitMath(BinaryOperation expression);

    protected abstract void visitOther(IExpression expression);

    public static IExpression getFirstDrawable(final IExpression expression) {
        if (expression.shouldDraw()) {
            return expression;
        }
        final IExpression[] children = expression.getChildren();
        for (int i = 0; i < children.length; i++) {
            final IExpression child = children[i];
            final IExpression drawable = getFirstDrawable(child);
            if (drawable != null) {
                return drawable;
            }
        }
        return null;
    }
}
