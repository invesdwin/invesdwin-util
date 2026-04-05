package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.visitor.ExpressionProperties;

@Immutable
public final class Variables {

    public static final IVariable PI = new ADoubleConstant(Doubles.PI) {

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
                    + "<i>pi</i>, the ratio of the circumference of a circle to its diameter: " + Doubles.PI;
        }

        @Override
        public Object getProperty(final String property) {
            return null;
        }

    };

    public static final IVariable EULER = new ADoubleConstant(Doubles.E) {

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
                    + "<i>e</i>, the base of the natural logarithms: " + Doubles.E;
        }

        @Override
        public Object getProperty(final String property) {
            return null;
        }

    };

    public static final IVariable NAN = new ADoubleConstant(Double.NaN) {

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
            return "This denotes a missing value as defined by Double.NaN as a representation for \"Not a Number\" or \"NULL\". It evaluates to FALSE in boolean expressions.";
        }

        @Override
        public Object getProperty(final String property) {
            switch (property) {
            case ExpressionProperties.DRAW:
                return false;
            default:
                return null;
            }
        }

    };

    public static final IVariable NULL = new ADoubleConstant(Double.NaN) {

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
        public Object getProperty(final String property) {
            switch (property) {
            case ExpressionProperties.DRAW:
                return false;
            default:
                return null;
            }
        }

    };

    public static final IVariable TRUE = new ABooleanConstant(true) {

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
        public Object getProperty(final String property) {
            return null;
        }

    };

    public static final IVariable FALSE = new ABooleanConstant(false) {

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
        public Object getProperty(final String property) {
            return null;
        }

    };

    private Variables() {}

}
