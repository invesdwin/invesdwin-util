package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.simple.IntegerBinaryOperation;
import de.invesdwin.util.time.fdate.IFDateProvider;

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
    public double evaluateDouble(final IFDateProvider key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyDouble(a, b);
    }

    @Override
    public double evaluateDouble(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyDouble(a, b);
    }

    @Override
    public double evaluateDouble() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.applyDouble(a, b);
    }

    @Override
    public int evaluateInteger(final IFDateProvider key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyInteger(a, b);
    }

    @Override
    public int evaluateInteger(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyInteger(a, b);
    }

    @Override
    public int evaluateInteger() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.applyInteger(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable(final IFDateProvider key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public Boolean evaluateBooleanNullable() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.applyBooleanNullable(a, b);
    }

    @Override
    public boolean evaluateBoolean(final IFDateProvider key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyBoolean(a, b);
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.applyBoolean(a, b);
    }

    @Override
    public boolean evaluateBoolean() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.applyBoolean(a, b);
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
        return new ConstantExpression(evaluateDouble());
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
                            new ConstantExpression(newLeft.evaluateDouble() + childOp.getLeft().evaluateDouble(),
                                    ExpressionType.determineType(getType(), newLeft, childOp.getLeft())),
                            childOp.getRight());
                }
                if (op == Op.MULTIPLY) {
                    return newBinaryOperation(
                            new ConstantExpression(newLeft.evaluateDouble() * childOp.getLeft().evaluateDouble(),
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
