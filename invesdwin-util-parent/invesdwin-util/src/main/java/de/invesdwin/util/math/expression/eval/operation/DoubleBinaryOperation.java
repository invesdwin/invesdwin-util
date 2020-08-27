// CHECKSTYLE:OFF
package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
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

/**
 * We flatten the operations here so that they become simpler for the JIT so they get inlined/optimized more
 * aggressively.
 */
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
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
            final double result = opF.applyDoubleFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
            final double result = opF.applyDoubleFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
            final double result = opF.applyDoubleFromDoubles(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.pow(a, b);
                };
            default:
                final IDoubleFromDoublesBinaryOp opF = op.newDoubleFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return opF.applyDoubleFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
            final int result = opF.applyIntegerFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
            final int result = opF.applyIntegerFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
            final int result = opF.applyIntegerFromDoubles(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.fromBoolean(Doubles.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromDoublesBinaryOp opF = op.newIntegerFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return opF.applyIntegerFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
            final Boolean result = opF.applyBooleanNullableFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
            final Boolean result = opF.applyBooleanNullableFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
            final Boolean result = opF.applyBooleanNullableFromDoubles(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBooleanNullable(Doubles.multiply(a, b));
                };
            default:
                final IBooleanNullableFromDoublesBinaryOp opF = op.newBooleanNullableFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return opF.applyBooleanNullableFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
            final boolean result = opF.applyBooleanFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleFDate leftF = left.newEvaluateDoubleFDate();
            final IEvaluateDoubleFDate rightF = right.newEvaluateDoubleFDate();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
            final boolean result = opF.applyBooleanFromDoubles(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDoubleKey leftF = left.newEvaluateDoubleKey();
            final IEvaluateDoubleKey rightF = right.newEvaluateDoubleKey();
            switch (op) {
            case GT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return key -> {
                    final double a = leftF.evaluateDouble(key);
                    final double b = rightF.evaluateDouble(key);
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        if (left.isConstant() && right.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
            final boolean result = opF.applyBooleanFromDoubles(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final double a = left.newEvaluateDouble().evaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return () -> {
                    final double b = rightF.evaluateDouble();
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final double b = right.newEvaluateDouble().evaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        } else {
            final IEvaluateDouble leftF = left.newEvaluateDouble();
            final IEvaluateDouble rightF = right.newEvaluateDouble();
            switch (op) {
            case GT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return Doubles.toBoolean(Doubles.multiply(a, b));
                };
            default:
                final IBooleanFromDoublesBinaryOp opF = op.newBooleanFromDoubles();
                return () -> {
                    final double a = leftF.evaluateDouble();
                    final double b = rightF.evaluateDouble();
                    return opF.applyBooleanFromDoubles(a, b);
                };
            }
        }
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
