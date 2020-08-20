package de.invesdwin.util.math.expression.eval.operation;

import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;

public interface IBinaryOperation extends IParsedExpression {

    Op getOp();

    IParsedExpression getLeft();

    IBinaryOperation setLeft(IParsedExpression left);

    IParsedExpression getRight();

    void seal();

    boolean isSealed();

    static IBinaryOperation validateComparativeOperation(final IExpression condition) {
        if (!(condition instanceof IBinaryOperation)) {
            throw new IllegalArgumentException(
                    "condition needs to be a " + IBinaryOperation.class.getSimpleName() + ": " + condition);
        }
        final IBinaryOperation binaryOperation = (IBinaryOperation) condition;
        switch (binaryOperation.getOp()) {
        case EQ:
        case NEQ:
        case GT:
        case GT_EQ:
        case LT:
        case LT_EQ:
            break;
        default:
            throw new IllegalArgumentException("Comparative " + IBinaryOperation.class.getSimpleName()
                    + " needs to be one of [" + Op.EQ + "," + Op.NEQ + "," + Op.GT + "," + Op.GT_EQ + "," + Op.LT + ","
                    + Op.LT_EQ + "]: " + binaryOperation.getOp());
        }
        return binaryOperation;
    }

}
