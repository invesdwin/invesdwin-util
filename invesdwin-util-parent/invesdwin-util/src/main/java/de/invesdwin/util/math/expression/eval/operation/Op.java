package de.invesdwin.util.math.expression.eval.operation;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionType;

@Immutable
public enum Op {
    ADD(3, "+") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
        }
    },
    SUBTRACT(3, "-") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Integers.integerToBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Integers.integerToBoolean(applyInteger(a, b));
        }
    },
    MULTIPLY(4, "*") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Doubles.doubleToBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.doubleToBoolean(applyInteger(a, b));
        }
    },
    DIVIDE(4, "/") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Doubles.doubleToBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.doubleToBoolean(applyInteger(a, b));
        }
    },
    MODULO(4, "%") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Doubles.doubleToBooleanNullable(applyInteger(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.doubleToBoolean(applyInteger(a, b));
        }
    },
    POWER(5, "^") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Double;
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
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
            return Doubles.doubleToBooleanNullable(applyDouble(a, b));
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            return Doubles.doubleToBoolean(applyDouble(a, b));
        }
    },
    LT(2, "<") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final double a, final double b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
            return Doubles.booleanToDouble(applyBoolean(a, b));
        }

        @Override
        public int applyInteger(final int a, final int b) {
            return Integers.booleanToInteger(applyBoolean(a, b));
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleAndOperation.class.getSimpleName());
        }
    },
    OR(1, "||") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleOrOperation.class.getSimpleName());
        }
    },
    NOT(1, "!") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Boolean;
        }

        @Override
        public double applyDouble(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final double a, final double b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public double applyDouble(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public int applyInteger(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public Boolean applyBooleanNullable(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }

        @Override
        public boolean applyBoolean(final int a, final int b) {
            throw new UnsupportedOperationException("use class " + DoubleNotOperation.class.getSimpleName());
        }
    },
    CROSSES_ABOVE(1, "crosses above") {
        @Override
        public ExpressionType getType() {
            return ExpressionType.Boolean;
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
        public ExpressionType getType() {
            return ExpressionType.Boolean;
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

    public abstract double applyDouble(double a, double b);

    public abstract int applyInteger(double a, double b);

    public abstract Boolean applyBooleanNullable(double a, double b);

    public abstract boolean applyBoolean(double a, double b);

    public abstract double applyDouble(int a, int b);

    public abstract int applyInteger(int a, int b);

    public abstract Boolean applyBooleanNullable(int a, int b);

    public abstract boolean applyBoolean(int a, int b);

    public abstract ExpressionType getType();

}