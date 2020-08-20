package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.eval.IParsedExpression;

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
        public double applyDouble(final double a, final double b) {
            return a + b;
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return applyDouble((double) a, (double) b);
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.toBoolean(applyDouble(a, b));
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
        public double applyDouble(final double a, final double b) {
            return a - b;
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return a - b;
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return a - b;
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Integers.toBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.toBoolean(applyInteger(a, b));
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
        public double applyDouble(final double a, final double b) {
            return a * b;
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return applyDouble((double) a, (double) b);
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Doubles.toBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.toBoolean(applyInteger(a, b));
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
        public double applyDouble(final double a, final double b) {
            return Doubles.divide(a, b);
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.divide(a, b);
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.divide(a, b);
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Doubles.toBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.toBoolean(applyInteger(a, b));
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
        public double applyDouble(final double a, final double b) {
            return a % b;
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return applyInteger(a, b);
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return a % b;
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Doubles.toBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.toBoolean(applyInteger(a, b));
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
        public double applyDouble(final double a, final double b) {
            return Math.pow(a, b);
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.toBoolean(applyDouble(a, b));
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Math.pow(a, b);
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.checkedCastNoOverflow(applyDouble(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return Doubles.toBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.toBoolean(applyDouble(a, b));
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.isLessThan(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.isLessThan(a, b);
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.isLessThanOrEqualTo(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.isLessThanOrEqualTo(a, b);
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.equals(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.equals(a, b);
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.isGreaterThanOrEqualTo(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.isGreaterThanOrEqualTo(a, b);
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.isGreaterThan(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.isGreaterThan(a, b);
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
        public double applyDouble(final double a, final double b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.notEquals(a, b);
        }

        @Override
        public double applyDouble(final int a, final int b) {
            return Doubles.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.fromBoolean(applyBoolean(a, b));
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            return applyBoolean(a, b);
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.notEquals(a, b);
        }
    },
    AND(1, "&&") {
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
            if (left.isSmallerThanOrEqualTo(ExpressionType.Boolean)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableAndOperation.class.getSimpleName());
        }
    },
    OR(1, "||") {
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
            if (left.isSmallerThanOrEqualTo(ExpressionType.Boolean)
                    && right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableOrOperation.class.getSimpleName());
        }
    },
    NOT(1, "!") {
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
            if (right.isSmallerThanOrEqualTo(ExpressionType.Boolean)) {
                return ExpressionType.Boolean;
            }
            return null;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + BooleanNullableNotOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
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
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesAboveOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
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
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleCrossesBelowOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
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

    public abstract double applyDouble(double a, double b);

    public abstract int applyInteger(double a, double b);

    public abstract Boolean applyBooleanNullable(double a, double b);

    public abstract boolean applyBoolean(double a, double b);

    public abstract double applyDouble(int a, int b);

    public abstract int applyInteger(int a, int b);

    public abstract Boolean applyBooleanNullable(int a, int b);

    public abstract boolean applyBoolean(int a, int b);

    public abstract ExpressionType getReturnType();

    public abstract ExpressionType getSimplifiedReturnType();

    public ExpressionType simplifyType(final IParsedExpression left, final IParsedExpression right) {
        return simplifyType(left.getType(), right.getType());
    }

    public abstract ExpressionType simplifyType(ExpressionType left, ExpressionType right);

}