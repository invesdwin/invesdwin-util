package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class BinaryOperation implements IParsedExpression {

    protected final IParsedExpression left;
    protected final IParsedExpression right;
    private final Op op;
    private boolean sealed = false;

    public BinaryOperation(final Op op, final IParsedExpression left, final IParsedExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public Op getOp() {
        return op;
    }

    public IParsedExpression getLeft() {
        return left;
    }

    public BinaryOperation setLeft(final IParsedExpression left) {
        return newBinaryOperation(op, left, right);
    }

    public IParsedExpression getRight() {
        return right;
    }

    public void seal() {
        sealed = true;
    }

    public boolean isSealed() {
        return sealed;
    }

    @Override
    public double evaluateDouble(final FDate key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.apply(a, b);
    }

    @Override
    public double evaluateDouble(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.apply(a, b);
    }

    @Override
    public double evaluateDouble() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.apply(a, b);
    }

    @Override
    public boolean evaluateBoolean(final FDate key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.apply(a, b) > 0D;
    }

    @Override
    public boolean evaluateBoolean(final int key) {
        final double a = left.evaluateDouble(key);
        final double b = right.evaluateDouble(key);

        return op.apply(a, b) > 0D;
    }

    @Override
    public boolean evaluateBoolean() {
        final double a = left.evaluateDouble();
        final double b = right.evaluateDouble();

        return op.apply(a, b) > 0D;
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

            if (newRight instanceof BinaryOperation) {
                final IParsedExpression childOp = trySimplifyRightSide(newLeft, newRight);
                if (childOp != null) {
                    //we can directly use the child instead of this one
                    return childOp;
                }
            }
        }

        return newBinaryOperation(op, newLeft, newRight);
    }

    private IParsedExpression newConstantExpression() {
        return new ConstantExpression(evaluateDouble());
    }

    protected BinaryOperation newBinaryOperation(final Op op, final IParsedExpression left,
            final IParsedExpression right) {
        return new BinaryOperation(op, left, right);
    }

    private IParsedExpression trySimplifyRightSide(final IParsedExpression newLeft, final IParsedExpression newRight) {
        final BinaryOperation childOp = (BinaryOperation) newRight;
        if (op != childOp.op) {
            return null;
        }

        // We have a sub-operation with the same operator, let's see if we can pre-compute some constants
        if (newLeft.isConstant()) {
            // Left side is constant, we therefore can combine constants. We can rely on the constant
            // being on the left side, since we reorder commutative operations (see above)
            if (childOp.left.isConstant()) {
                if (op == Op.ADD) {
                    return newBinaryOperation(op,
                            new ConstantExpression(newLeft.evaluateDouble() + childOp.left.evaluateDouble()),
                            childOp.right);
                }
                if (op == Op.MULTIPLY) {
                    return newBinaryOperation(op,
                            new ConstantExpression(newLeft.evaluateDouble() * childOp.left.evaluateDouble()),
                            childOp.right);
                }
            }
        }

        if (childOp.left.isConstant()) {
            // Since our left side is non constant, but the left side of the child expression is,
            // we push the constant up, to support further optimizations
            return newBinaryOperation(op, childOp.left, newBinaryOperation(op, newLeft, childOp.right));
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

    public enum Op {
        ADD(3, "+") {
            @Override
            protected double apply(final double a, final double b) {
                return a + b;
            }
        },
        SUBTRACT(3, "-") {
            @Override
            protected double apply(final double a, final double b) {
                return a - b;
            }
        },
        MULTIPLY(4, "*") {
            @Override
            protected double apply(final double a, final double b) {
                return a * b;
            }
        },
        DIVIDE(4, "/") {
            @Override
            protected double apply(final double a, final double b) {
                return Doubles.divide(a, b);
            }
        },
        MODULO(4, "%") {
            @Override
            protected double apply(final double a, final double b) {
                return a % b;
            }
        },
        POWER(5, "^") {
            @Override
            protected double apply(final double a, final double b) {
                return Math.pow(a, b);
            }
        },
        LT(2, "<") {
            @Override
            protected double apply(final double a, final double b) {
                return a < b ? 1D : 0D;
            }
        },
        LT_EQ(2, "<=") {
            @Override
            protected double apply(final double a, final double b) {
                return Doubles.compare(a, b) <= 0 ? 1D : 0D;
            }
        },
        EQ(2, "==") {
            @Override
            protected double apply(final double a, final double b) {
                return Doubles.compare(a, b) == 0 ? 1D : 0D;
            }
        },
        GT_EQ(2, ">=") {
            @Override
            protected double apply(final double a, final double b) {
                return Doubles.compare(a, b) >= 0 ? 1D : 0D;
            }
        },
        GT(2, ">") {
            @Override
            protected double apply(final double a, final double b) {
                return a > b ? 1D : 0D;
            }
        },
        NEQ(2, "!=") {
            @Override
            protected double apply(final double a, final double b) {
                return Doubles.compare(a, b) != 0 ? 1 : 0;
            }
        },
        AND(1, "&&") {
            @Override
            protected double apply(final double a, final double b) {
                throw new UnsupportedOperationException("use class " + AndOperation.class.getSimpleName());
            }
        },
        OR(1, "||") {
            @Override
            protected double apply(final double a, final double b) {
                throw new UnsupportedOperationException("use class " + OrOperation.class.getSimpleName());
            }
        },
        NOT(1, "!") {
            @Override
            protected double apply(final double a, final double b) {
                throw new UnsupportedOperationException("use class " + NotOperation.class.getSimpleName());
            }
        },
        CROSSES_ABOVE(1, "crosses above") {
            @Override
            protected double apply(final double a, final double b) {
                throw new UnsupportedOperationException("use class " + CrossesAboveOperation.class.getSimpleName());
            }
        },
        CROSSES_BELOW(1, "crosses below") {
            @Override
            protected double apply(final double a, final double b) {
                throw new UnsupportedOperationException("use class " + CrossesBelowOperation.class.getSimpleName());
            }
        };

        private final int priority;
        private String text;

        Op(final int priority, final String text) {
            this.priority = priority;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public int getPriority() {
            return priority;
        }

        protected abstract double apply(double a, double b);

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public boolean shouldPersist() {
        return left.shouldPersist() || right.shouldPersist();
    }

}
