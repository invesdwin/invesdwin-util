package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

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
        public String getName() {
            return "sin";
        }
    };

    public static final IFunction SINH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sinh(a);
        }

        @Override
        public String getName() {
            return "sinh";
        }
    };

    public static final IFunction COS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cos(a);
        }

        @Override
        public String getName() {
            return "cos";
        }
    };

    public static final IFunction COSH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cosh(a);
        }

        @Override
        public String getName() {
            return "cosh";
        }
    };

    public static final IFunction TAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tan(a);
        }

        @Override
        public String getName() {
            return "tan";
        }
    };

    public static final IFunction TANH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tanh(a);
        }

        @Override
        public String getName() {
            return "tanh";
        }
    };

    public static final IFunction ABS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.abs(a);
        }

        @Override
        public String getName() {
            return "abs";
        }
    };

    public static final IFunction ASIN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.asin(a);
        }

        @Override
        public String getName() {
            return "asin";
        }
    };

    public static final IFunction ACOS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.acos(a);
        }

        @Override
        public String getName() {
            return "acos";
        }
    };

    public static final IFunction ATAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.atan(a);
        }

        @Override
        public String getName() {
            return "atan";
        }
    };

    public static final IFunction ATAN2 = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.atan2(a, b);
        }

        @Override
        public String getName() {
            return "atan2";
        }
    };

    public static final IFunction ROUND = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.round(a);
        }

        @Override
        public String getName() {
            return "round";
        }
    };

    public static final IFunction FLOOR = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.floor(a);
        }

        @Override
        public String getName() {
            return "floor";
        }
    };

    public static final IFunction CEIL = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.ceil(a);
        }

        @Override
        public String getName() {
            return "ceil";
        }
    };

    public static final IFunction POW = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.pow(a, b);
        }

        @Override
        public String getName() {
            return "pow";
        }
    };

    public static final IFunction SQRT = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sqrt(a);
        }

        @Override
        public String getName() {
            return "sqrt";
        }
    };

    public static final IFunction EXP = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.exp(a);
        }

        @Override
        public String getName() {
            return "exp";
        }
    };

    public static final IFunction LN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log(a);
        }

        @Override
        public String getName() {
            return "ln";
        }
    };

    public static final IFunction LOG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log10(a);
        }

        @Override
        public String getName() {
            return "log";
        }
    };

    public static final IFunction MIN = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.min(a, b);
        }

        @Override
        public String getName() {
            return "min";
        }
    };

    public static final IFunction MAX = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.max(a, b);
        }

        @Override
        public String getName() {
            return "max";
        }
    };

    public static final IFunction RND = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.random() * a;
        }

        @Override
        public String getName() {
            return "rnd";
        }
    };

    public static final IFunction SIGN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.signum(a);
        }

        @Override
        public String getName() {
            return "sign";
        }
    };

    public static final IFunction DEG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toDegrees(a);
        }

        @Override
        public String getName() {
            return "deg";
        }
    };

    public static final IFunction RAD = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toRadians(a);
        }

        @Override
        public String getName() {
            return "rad";
        }
    };

    public static final IFunction IF = new IFunction() {

        @Override
        public String getName() {
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
    };

    public static final IFunction ISNAN = new IFunction() {

        @Override
        public String getName() {
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
    };

    public static final IFunction NEGATE = new IFunction() {

        @Override
        public String getName() {
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
    };

    private Functions() {}

}
