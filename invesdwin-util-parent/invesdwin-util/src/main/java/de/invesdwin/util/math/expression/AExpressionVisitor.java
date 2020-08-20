package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;

@Immutable
public abstract class AExpressionVisitor {

    public void process(final IExpression expression) {
        final IExpression e;
        if (isDrawableOnly() && expression.getChildren().length == 1) {
            e = getDrawable(expression);
        } else {
            e = expression;
        }
        if (e == null) {
            return;
        } else if (e instanceof DoubleBinaryOperation) {
            final DoubleBinaryOperation cExpression = (DoubleBinaryOperation) e;
            final boolean visitChildren = processBinaryOperation(cExpression);
            if (visitChildren) {
                process(cExpression.getLeft());
                process(cExpression.getRight());
            }
        } else {
            visitOther(e);
        }
    }

    protected boolean processBinaryOperation(final DoubleBinaryOperation expression) {
        final boolean visitChildren;
        switch (expression.getOp()) {
        case AND:
        case OR:
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

    protected boolean isDrawableOnly() {
        return false;
    }

    protected abstract boolean visitLogicalCombination(DoubleBinaryOperation expression);

    protected abstract boolean visitComparison(DoubleBinaryOperation expression);

    protected abstract boolean visitMath(DoubleBinaryOperation expression);

    protected abstract void visitOther(IExpression expression);

    public static IExpression getDrawable(final IExpression expression) {
        if (expression.shouldDraw()) {
            return expression;
        }
        final IExpression[] children = expression.getChildren();
        if (children.length == 1) {
            final IExpression child = children[0];
            final IExpression drawable = getDrawable(child);
            if (drawable != null) {
                return drawable;
            }
        }
        return null;
    }
}
