package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IDoubleFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IDoubleFromIntegersBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IIntegerFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.eval.operation.lambda.IIntegerFromIntegersBinaryOp;

@Immutable
public enum Op {
    ADD(3, "+") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Integer;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.add(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.add(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.add(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.add(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IDoubleFromIntegersBinaryOp doubleFromIntegersF = newDoubleFromIntegers();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromIntegersF.applyDoubleFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IDoubleFromIntegersBinaryOp doubleFromIntegersF = newDoubleFromIntegers();
            return (a, b) -> Doubles.toBoolean(doubleFromIntegersF.applyDoubleFromIntegers(a, b));
        }

    },
    SUBTRACT(3, "-") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Integer;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.subtract(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.subtract(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.subtract(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.subtract(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Integers.toBooleanNullable(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Integers.toBoolean(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }
    },
    MULTIPLY(4, "*") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Integer;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.multiply(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.multiply(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.multiply(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.multiply(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBooleanNullable(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBoolean(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }
    },
    DIVIDE(4, "/") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.divide(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.divide(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.divide(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.divide(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBooleanNullable(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBoolean(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }
    },
    MODULO(4, "%") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Integer;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.modulo(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.modulo(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoubleF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoubleF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoubleF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoubleF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.modulo(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.modulo(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBooleanNullable(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IIntegerFromIntegersBinaryOp integerFromIntegersF = newIntegerFromIntegers();
            return (a, b) -> Doubles.toBoolean(integerFromIntegersF.applyIntegerFromIntegers(a, b));
        }
    },
    POWER(5, "^") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Double;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            return (a, b) -> Doubles.pow(a, b);
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            return (a, b) -> Integers.pow(a, b);
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            final IDoubleFromDoublesBinaryOp doubleFromDoublesF = newDoubleFromDoubles();
            return (a, b) -> Doubles.toBoolean(doubleFromDoublesF.applyDoubleFromDoubles(a, b));
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            return (a, b) -> Doubles.pow(a, b);
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            return (a, b) -> Integers.pow(a, b);
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IDoubleFromIntegersBinaryOp doubleFromIntegersF = newDoubleFromIntegers();
            return (a, b) -> Doubles.toBooleanNullable(doubleFromIntegersF.applyDoubleFromIntegers(a, b));
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            final IDoubleFromIntegersBinaryOp doubleFromIntegersF = newDoubleFromIntegers();
            return (a, b) -> Doubles.toBoolean(doubleFromIntegersF.applyDoubleFromIntegers(a, b));
        }
    },
    LT(2, "<") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.isLessThanNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.isLessThan(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> Integers.isLessThan(a, b);
        }
    },
    LT_EQ(2, "<=") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.isLessThanOrEqualToNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.isLessThanOrEqualTo(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> Integers.isLessThanOrEqualTo(a, b);
        }
    },
    EQ(2, "==") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.equalsNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.equals(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> Integers.equals(a, b);
        }
    },
    GT_EQ(2, ">=") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.isGreaterThanOrEqualToNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.isGreaterThanOrEqualTo(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> a >= b;
        }
    },
    GT(2, ">") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.isGreaterThanNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.isGreaterThan(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> Integers.isGreaterThan(a, b);
        }
    },
    NEQ(2, "!=") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            final IBooleanNullableFromDoublesBinaryOp booleanNullableFromDoublesF = newBooleanNullableFromDoubles();
            return (a, b) -> Doubles.fromBoolean(booleanNullableFromDoublesF.applyBooleanNullableFromDoubles(a, b));
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            final IBooleanFromDoublesBinaryOp booleanFromDoublesF = newBooleanFromDoubles();
            return (a, b) -> Integers.fromBoolean(booleanFromDoublesF.applyBooleanFromDoubles(a, b));
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            return (a, b) -> Doubles.notEqualsNullable(a, b);
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            return (a, b) -> Doubles.notEquals(a, b);
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Doubles.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> Integers.fromBoolean(booleanFromIntegersF.applyBooleanFromIntegers(a, b));
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            final IBooleanFromIntegersBinaryOp booleanFromIntegersF = newBooleanFromIntegers();
            return (a, b) -> booleanFromIntegersF.applyBooleanFromIntegers(a, b);
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            return (a, b) -> Integers.notEquals(a, b);
        }
    },
    AND(1, "&&") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.BooleanNullable;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Boolean)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }
    },
    OR(1, "||") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.BooleanNullable;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Boolean)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }
    },
    XOR(1, "XOR") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.BooleanNullable;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Boolean)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableXorOperation.class.getSimpleName());
        }
    },
    NOT(1, "!") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.BooleanNullable;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }
    },
    CROSSES_ABOVE(1, "crosses above") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }
    },
    CROSSES_BELOW(1, "crosses below") {
        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType getSimplifiedReturnType() {
            return ExpressionType.Boolean;
        }

        @Override
        public ExpressionType simplifyType(final ExpressionType left, final ExpressionType right) {
            if (left.isSmallerThanOrEqualTo(ExpressionType.Integer)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Integer)) {
                return ExpressionType.Integer;
            }
            return null;
        }

        @Override
        public IDoubleFromDoublesBinaryOp newDoubleFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromDoublesBinaryOp newIntegerFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromDoublesBinaryOp newBooleanFromDoubles() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IDoubleFromIntegersBinaryOp newDoubleFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IIntegerFromIntegersBinaryOp newIntegerFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public IBooleanFromIntegersBinaryOp newBooleanFromIntegers() {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }
    };

    private final int priority;
    private final String text;

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

    public abstract IDoubleFromDoublesBinaryOp newDoubleFromDoubles();

    public abstract IIntegerFromDoublesBinaryOp newIntegerFromDoubles();

    public abstract IBooleanNullableFromDoublesBinaryOp newBooleanNullableFromDoubles();

    public abstract IBooleanFromDoublesBinaryOp newBooleanFromDoubles();

    public abstract IDoubleFromIntegersBinaryOp newDoubleFromIntegers();

    public abstract IIntegerFromIntegersBinaryOp newIntegerFromIntegers();

    public abstract IBooleanNullableFromIntegersBinaryOp newBooleanNullableFromIntegers();

    public abstract IBooleanFromIntegersBinaryOp newBooleanFromIntegers();

    public abstract ExpressionType getReturnType();

    public abstract ExpressionType getSimplifiedReturnType();

    public ExpressionType simplifyType(final IParsedExpression left, final IParsedExpression right) {
        return simplifyType(left.getType(), right.getType());
    }

    public abstract ExpressionType simplifyType(ExpressionType left, ExpressionType right);

}