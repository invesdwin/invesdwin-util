package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunction;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class Functions {

    public static final IFunction SIN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sin(a);
        }

        @Override
        public String getExpressionName() {
            return "sin";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction SINH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sinh(a);
        }

        @Override
        public String getExpressionName() {
            return "sinh";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction COS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cos(a);
        }

        @Override
        public String getExpressionName() {
            return "cos";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction COSH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cosh(a);
        }

        @Override
        public String getExpressionName() {
            return "cosh";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction TAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tan(a);
        }

        @Override
        public String getExpressionName() {
            return "tan";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction TANH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tanh(a);
        }

        @Override
        public String getExpressionName() {
            return "tanh";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ABS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.abs(a);
        }

        @Override
        public String getExpressionName() {
            return "abs";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ASIN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.asin(a);
        }

        @Override
        public String getExpressionName() {
            return "asin";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ACOS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.acos(a);
        }

        @Override
        public String getExpressionName() {
            return "acos";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ATAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.atan(a);
        }

        @Override
        public String getExpressionName() {
            return "atan";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ATAN2 = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.atan2(a, b);
        }

        @Override
        public String getExpressionName() {
            return "atan2";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }

    };

    public static final IFunction ROUND = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.round(a);
        }

        @Override
        public String getExpressionName() {
            return "round";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction FLOOR = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.floor(a);
        }

        @Override
        public String getExpressionName() {
            return "floor";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction CEIL = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.ceil(a);
        }

        @Override
        public String getExpressionName() {
            return "ceil";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction POW = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.pow(a, b);
        }

        @Override
        public String getExpressionName() {
            return "pow";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction SQRT = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sqrt(a);
        }

        @Override
        public String getExpressionName() {
            return "sqrt";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction EXP = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.exp(a);
        }

        @Override
        public String getExpressionName() {
            return "exp";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction LN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log(a);
        }

        @Override
        public String getExpressionName() {
            return "ln";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction LOG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log10(a);
        }

        @Override
        public String getExpressionName() {
            return "log";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction MIN = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.min(a, b);
        }

        @Override
        public String getExpressionName() {
            return "min";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction MAX = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.max(a, b);
        }

        @Override
        public String getExpressionName() {
            return "max";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction RND = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.random() * a;
        }

        @Override
        public String getExpressionName() {
            return "rnd";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction SIGN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.signum(a);
        }

        @Override
        public String getExpressionName() {
            return "sign";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction DEG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toDegrees(a);
        }

        @Override
        public String getExpressionName() {
            return "deg";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction RAD = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toRadians(a);
        }

        @Override
        public String getExpressionName() {
            return "rad";
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction IF = new IFunction() {

        @Override
        public String getExpressionName() {
            return "if";
        }

        @Override
        public int getNumberOfArguments() {
            return 3;
        }

        @Override
        public double eval(final FDate key, final IExpression[] args) {
            final boolean check = args[0].evaluateBoolean(key);
            if (check) {
                return args[1].evaluateDouble(key);
            } else {
                return args[2].evaluateDouble(key);
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final boolean check = args[0].evaluateBoolean(key);
            if (check) {
                return args[1].evaluateDouble(key);
            } else {
                return args[2].evaluateDouble(key);
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final boolean check = args[0].evaluateBoolean();
            if (check) {
                return args[1].evaluateDouble();
            } else {
                return args[2].evaluateDouble();
            }
        }

        @Override
        public boolean isNaturalFunction() {
            return false;
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Double;
        }
    };

    public static final IFunction ISNAN = new IFunction() {

        @Override
        public String getExpressionName() {
            return "isNaN";
        }

        @Override
        public boolean isNaturalFunction() {
            return true;
        }

        @Override
        public int getNumberOfArguments() {
            return 1;
        }

        @Override
        public double eval(final FDate key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            if (Double.isNaN(a)) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            if (Double.isNaN(a)) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final double a = args[0].evaluateDouble();
            if (Double.isNaN(a)) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }
    };

    public static final IFunction NEGATE = new IFunction() {

        @Override
        public String getExpressionName() {
            return "negate";
        }

        @Override
        public boolean isNaturalFunction() {
            return true;
        }

        @Override
        public int getNumberOfArguments() {
            return 1;
        }

        @Override
        public double eval(final FDate key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return -1D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return -1D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean();
            if (a) {
                return -1D;
            } else {
                return 1D;
            }
        }

        @Override
        public ExpressionType getReturnType() {
            return ExpressionType.Boolean;
        }
    };

    private Functions() {}

}
