package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IDoubleFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IIntegerFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.simple.IntegerBinaryOperation;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

@Immutable
public class DoubleBinaryOperation implements IBinaryOperation {

    protected final Op op;
    protected final IParsedExpression left;
    protected final IParsedExpression right;
    private boolean sealed = false;

    public DoubleBinaryOperation(final Op op, final IParsedExpression left, final IParsedExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Op getOp() {
        return op;
    }

    @Override
    public IParsedExpression getLeft() {
        return left;
    }

    @Override
    public IBinaryOperation setLeft(final IParsedExpression left) {
        return newBinaryOperation(left, right);
    }

    @Override
    public IParsedExpression getRight() {
        return right;
    }

    @Override
    public void seal() {
        sealed = true;
    }

    @Override
    public boolean isSealed() {
        return sealed;
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyDoubleFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyDoubleFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        final IEvaluateDouble leftF = left.newEvaluateDouble();
        final IEvaluateDouble rightF = right.newEvaluateDouble();
        final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
        return () -> {
            final double a = leftF.evaluateDouble();
            final double b = rightF.evaluateDouble();
            return opF.applyDoubleFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyIntegerFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyIntegerFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        final IEvaluateDouble leftF = left.newEvaluateDouble();
        final IEvaluateDouble rightF = right.newEvaluateDouble();
        final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
        return () -> {
            final double a = leftF.evaluateDouble();
            final double b = rightF.evaluateDouble();
            return opF.applyIntegerFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyBooleanNullableFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyBooleanNullableFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        final IEvaluateDouble leftF = left.newEvaluateDouble();
        final IEvaluateDouble rightF = right.newEvaluateDouble();
        final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
        return () -> {
            final double a = leftF.evaluateDouble();
            final double b = rightF.evaluateDouble();
            return opF.applyBooleanNullableFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
        final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
        final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyBooleanFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
        final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
        final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
        return key -> {
            final double a = leftF.evaluateDouble(key);
            final double b = rightF.evaluateDouble(key);
            return opF.applyBooleanFromDoubles(a, b);
        };
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        final IEvaluateDouble leftF = left.newEvaluateDouble();
        final IEvaluateDouble rightF = right.newEvaluateDouble();
        final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
        return () -> {
            final double a = leftF.evaluateDouble();
            final double b = rightF.evaluateDouble();
            return opF.applyBooleanFromDoubles(a, b);
        };
    }

    @Override
    public IParsedExpression simplify() {
        final IParsedExpression newLeft = left.simplify();
        final IParsedExpression newRight = right.simplify();
        return simplify(newLeft, newRight);
    }

    protected IParsedExpression simplify(final IParsedExpression simplifiedLeft,
            final IParsedExpression simplifiedRight) {
        IParsedExpression newLeft = simplifiedLeft;
        IParsedExpression newRight = simplifiedRight;
        // First of all we check of both sides are constant. If true, we can directly evaluate the result...
        if (newLeft.isConstant() && newRight.isConstant()) {
            return newConstantExpression();
        }
        // + and * are commutative and associative, therefore we can reorder operands as we desire
        if (op == Op.ADD || op == Op.MULTIPLY) {
            // We prefer the have the constant part at the left side, re-order if it is the other way round.
            // This simplifies further optimizations as we can concentrate on the left side
            if (newRight.isConstant()) {
                final IParsedExpression tmp = newRight;
                newRight = newLeft;
                newLeft = tmp;
            }

            if (newRight instanceof IBinaryOperation) {
                final IParsedExpression childOp = trySimplifyRightSide(newLeft, newRight);
                if (childOp != null) {
                    //we can directly use the child instead of this one
                    return childOp;
                }
            }
        }

        return newBinaryOperation(newLeft, newRight);
    }

    private IParsedExpression newConstantExpression() {
        return new ConstantExpression(newEvaluateDouble().evaluateDouble());
    }

    protected IBinaryOperation newBinaryOperation(final IParsedExpression left, final IParsedExpression right) {
        final ExpressionType simplifyType = op.simplifyType(left, right);
        if (simplifyType == null) {
            return new DoubleBinaryOperation(op, left, right);
        } else if (simplifyType == ExpressionType.Integer) {
            return new IntegerBinaryOperation(op, left, right);
        } else {
            throw UnknownArgumentException.newInstance(ExpressionType.class, simplifyType);
        }
    }

    private IParsedExpression trySimplifyRightSide(final IParsedExpression newLeft, final IParsedExpression newRight) {
        final IBinaryOperation childOp = (IBinaryOperation) newRight;
        if (op != childOp.getOp()) {
            return null;
        }

        // We have a sub-operation with the same operator, let's see if we can pre-compute some constants
        if (newLeft.isConstant()) {
            // Left side is constant, we therefore can combine constants. We can rely on the constant
            // being on the left side, since we reorder commutative operations (see above)
            if (childOp.getLeft().isConstant()) {
                if (op == Op.ADD) {
                    return newBinaryOperation(
                            new ConstantExpression(
                                    newLeft.newEvaluateDouble().evaluateDouble()
                                            + childOp.getLeft().newEvaluateDouble().evaluateDouble(),
                                    ExpressionType.determineType(getType(), newLeft, childOp.getLeft())),
                            childOp.getRight());
                }
                if (op == Op.MULTIPLY) {
                    return newBinaryOperation(
                            new ConstantExpression(
                                    newLeft.newEvaluateDouble().evaluateDouble()
                                            * childOp.getLeft().newEvaluateDouble().evaluateDouble(),
                                    ExpressionType.determineType(getType(), newLeft, childOp.getLeft())),
                            childOp.getRight());
                }
            }
        }

        if (childOp.getLeft().isConstant()) {
            // Since our left side is non constant, but the left side of the child expression is,
            // we push the constant up, to support further optimizations
            return newBinaryOperation(childOp.getLeft(), newBinaryOperation(newLeft, childOp.getRight()));
        }

        return null;
    }

    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean shouldPersist() {
        return left.shouldPersist() || right.shouldPersist();
    }

    @Override
    public boolean shouldDraw() {
        return left.shouldDraw() || right.shouldDraw();
    }

    @Override
    public IExpression[] getChildren() {
        return new IExpression[] { left, right };
    }

    @Override
    public ExpressionType getType() {
        return op.getReturnType();
    }

}
