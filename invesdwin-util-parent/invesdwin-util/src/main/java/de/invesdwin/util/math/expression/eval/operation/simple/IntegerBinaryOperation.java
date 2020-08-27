// CHECKSTYLE:OFF
package de.invesdwin.util.math.expression.eval.operation.simple;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IDoubleFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IIntegerFromIntegersBinaryOp;
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
public class IntegerBinaryOperation extends DoubleBinaryOperation {

    public IntegerBinaryOperation(final Op op, final IParsedExpression left, final IParsedExpression right) {
        super(op, left, right);
    }

    @Override
    public IEvaluateDoubleFDate newEvaluateDoubleFDate() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
            final double result = opF.applyDoubleFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateDoubleKey newEvaluateDoubleKey() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
            final double result = opF.applyDoubleFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateDouble newEvaluateDouble() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
            final double result = opF.applyDoubleFromIntegers(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IDoubleFromIntegersBinaryOp opF = op.newDoubleFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return opF.applyDoubleFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateIntegerFDate newEvaluateIntegerFDate() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
            final int result = opF.applyIntegerFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateIntegerKey newEvaluateIntegerKey() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
            final int result = opF.applyIntegerFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.multiply(a, b);
                };
            case POWER:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateInteger newEvaluateInteger() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
            final int result = opF.applyIntegerFromIntegers(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThan(a, b));
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isGreaterThanOrEqualTo(a, b));
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThan(a, b));
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.isLessThanOrEqualTo(a, b));
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.equals(a, b));
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.fromBoolean(Integers.notEquals(a, b));
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.add(a, b);
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.subtract(a, b);
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.modulo(a, b);
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.divide(a, b);
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.multiply(a, b);
                };
            case POWER:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.pow(a, b);
                };
            default:
                final IIntegerFromIntegersBinaryOp opF = op.newIntegerFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return opF.applyIntegerFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
            final Boolean result = opF.applyBooleanNullableFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
            final Boolean result = opF.applyBooleanNullableFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanNullable newEvaluateBooleanNullable() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
            final Boolean result = opF.applyBooleanNullableFromIntegers(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBooleanNullable(Integers.multiply(a, b));
                };
            default:
                final IBooleanNullableFromIntegersBinaryOp opF = op.newBooleanNullableFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return opF.applyBooleanNullableFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanFDate newEvaluateBooleanFDate() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
            final boolean result = opF.applyBooleanFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerFDate leftF = left.newEvaluateIntegerFDate();
            final IEvaluateIntegerFDate rightF = right.newEvaluateIntegerFDate();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBooleanKey newEvaluateBooleanKey() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
            final boolean result = opF.applyBooleanFromIntegers(a, b);
            return key -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateIntegerKey leftF = left.newEvaluateIntegerKey();
            final IEvaluateIntegerKey rightF = right.newEvaluateIntegerKey();
            switch (op) {
            case GT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.equals(a, b);
                };
            case NEQ:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return key -> {
                    final int a = leftF.evaluateInteger(key);
                    final int b = rightF.evaluateInteger(key);
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public IEvaluateBoolean newEvaluateBoolean() {
        if (left.isConstant() && right.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
            final boolean result = opF.applyBooleanFromIntegers(a, b);
            return () -> {
                return result;
            };
        } else if (left.isConstant()) {
            final int a = left.newEvaluateInteger().evaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return () -> {
                    final int b = rightF.evaluateInteger();
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else if (right.isConstant()) {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final int b = right.newEvaluateInteger().evaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        } else {
            final IEvaluateInteger leftF = left.newEvaluateInteger();
            final IEvaluateInteger rightF = right.newEvaluateInteger();
            switch (op) {
            case GT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThan(a, b);
                };
            case GT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isGreaterThanOrEqualTo(a, b);
                };
            case LT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThan(a, b);
                };
            case LT_EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.isLessThanOrEqualTo(a, b);
                };
            case EQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.equals(a, b);
                };
            case NEQ:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.notEquals(a, b);
                };
            case ADD:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.add(a, b));
                };
            case SUBTRACT:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.subtract(a, b));
                };
            case MODULO:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.modulo(a, b));
                };
            case DIVIDE:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.divide(a, b));
                };
            case MULTIPLY:
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return Integers.toBoolean(Integers.multiply(a, b));
                };
            default:
                final IBooleanFromIntegersBinaryOp opF = op.newBooleanFromIntegers();
                return () -> {
                    final int a = leftF.evaluateInteger();
                    final int b = rightF.evaluateInteger();
                    return opF.applyBooleanFromIntegers(a, b);
                };
            }
        }
    }

    @Override
    public ExpressionType getType() {
        return op.getSimplifiedReturnType();
    }

}
