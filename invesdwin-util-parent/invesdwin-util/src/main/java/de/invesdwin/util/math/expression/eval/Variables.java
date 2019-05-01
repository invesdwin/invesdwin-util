package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.variable.AConstant;
import de.invesdwin.util.math.expression.variable.IVariable;

@Immutable
public final class Variables {

    public static final IVariable PI = new AConstant(Math.PI) {

        @Override
        public String getExpressionName() {
            return "pi";
        }

        @Override
        public String getName() {
            return "PI";
        }

        @Override
        public String getDescription() {
            return "The double value that is closer than any other to "
                    + "<i>pi</i>, the ratio of the circumference of a circle to its diameter: " + Math.PI;
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    public static final IVariable EULER = new AConstant(Math.E) {

        @Override
        public String getExpressionName() {
            return "euler";
        }

        @Override
        public String getName() {
            return "Euler";
        }

        @Override
        public String getDescription() {
            return "The double value that is closer than any other to "
                    + "<i>e</i>, the base of the natural logarithms: " + Math.E;
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    public static final IVariable NAN = new AConstant(Double.NaN) {

        @Override
        public String getExpressionName() {
            return "NaN";
        }

        @Override
        public String getName() {
            return "Missing Value";
        }

        @Override
        public String getDescription() {
            return "This denotes a missing value as defined by Double.NaN as a representation for \"Not a Number\" or \"NULL\"";
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    public static final IVariable NULL = new AConstant(Double.NaN) {

        @Override
        public String getExpressionName() {
            return "NULL";
        }

        @Override
        public String getName() {
            return NAN.getName();
        }

        @Override
        public String getDescription() {
            return NAN.getDescription();
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    public static final IVariable TRUE = new AConstant(1D) {

        @Override
        public String getExpressionName() {
            return "true";
        }

        @Override
        public String getName() {
            return "Boolean True";
        }

        @Override
        public String getDescription() {
            return "This is equal to 1 as a numerical value.";
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    public static final IVariable FALSE = new AConstant(0D) {

        @Override
        public String getExpressionName() {
            return "false";
        }

        @Override
        public String getName() {
            return "Boolean False";
        }

        @Override
        public String getDescription() {
            return "This is equal to 0 as a numerical value.";
        }

        @Override
        public ExpressionReturnType getType() {
            return ExpressionReturnType.Double;
        }

    };

    private Variables() {}

}
