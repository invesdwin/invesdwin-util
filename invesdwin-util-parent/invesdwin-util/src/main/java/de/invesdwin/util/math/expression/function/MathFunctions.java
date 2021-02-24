package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.eval.function.ADoubleBinaryFunction;
import de.invesdwin.util.math.expression.eval.function.ADoubleTernaryFunction;
import de.invesdwin.util.math.expression.eval.function.ADoubleUnaryFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.random.RandomGenerators;

@Immutable
public final class MathFunctions {

    public static final ADoubleFunction SIN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.sin(a);
        }

        @Override
        public String getExpressionName() {
            return "sin";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "angle";
                }

                @Override
                public String getName() {
                    return "Angle";
                }

                @Override
                public String getDescription() {
                    return "An angle, in radians.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Trigonometric Sine";
        }

        @Override
        public String getDescription() {
            return "Returns the trigonometric sine of an angle.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction SINH = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sinh(a);
        }

        @Override
        public String getExpressionName() {
            return "sinh";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The number whose hyperbolic sine is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Hyperbolic Sine";
        }

        @Override
        public String getDescription() {
            return "Returns the hyperbolic sine of an angle. "
                    + "The hyperbolic sine of x is defined to be (ex - e-x)/2 where e is Euler's number.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction COS = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.cos(a);
        }

        @Override
        public String getExpressionName() {
            return "cos";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "angleRadians";
                }

                @Override
                public String getName() {
                    return "Angle Radians";
                }

                @Override
                public String getDescription() {
                    return "An angle, in radians.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Trignonometric Cosine";
        }

        @Override
        public String getDescription() {
            return "Returns the trigonometric cosine of an angle.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction COSH = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cosh(a);
        }

        @Override
        public String getExpressionName() {
            return "cosh";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The number whose hyperbolic cosine is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Hyperbolic Cosine";
        }

        @Override
        public String getDescription() {
            return "Returns the hyperbolic cosine of a double value. "
                    + "The hyperbolic cosine of x is defined to be (ex + e-x)/2 where e is Euler's number.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction TAN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tan(a);
        }

        @Override
        public String getExpressionName() {
            return "tan";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "angleRadians";
                }

                @Override
                public String getName() {
                    return "Angle Radians";
                }

                @Override
                public String getDescription() {
                    return "An angle, in radians.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Trigonometric Tangent";
        }

        @Override
        public String getDescription() {
            return "Returns the trigonometric tangent of an angle.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction TANH = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tanh(a);
        }

        @Override
        public String getExpressionName() {
            return "tanh";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The number whose hyperbolic tangent is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Hyperbolic Tangent";
        }

        @Override
        public String getDescription() {
            return "Returns the hyperbolic tangent of a double value. "
                    + "The hyperbolic tangent of x is defined to be (ex - e-x)/(ex + e-x), in other words, sinh(x)/cosh(x). "
                    + "Note that the absolute value of the exact tanh is always less than 1.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ABS = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.abs(a);
        }

        @Override
        public String getExpressionName() {
            return "abs";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The argument whose absolute value is to be determined.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Absolute";
        }

        @Override
        public String getDescription() {
            return "Returns the absolute value of a double value. If the argument is not negative, the argument is returned. "
                    + "If the argument is negative, the negation of the argument is returned.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ASIN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.asin(a);
        }

        @Override
        public String getExpressionName() {
            return "asin";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The value whose arc sine is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Arc Sine";
        }

        @Override
        public String getDescription() {
            return "Returns the arc sine of a value; the returned angle is in the range -pi/2 through pi/2.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ACOS = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.acos(a);
        }

        @Override
        public String getExpressionName() {
            return "acos";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The value whose arc cosine is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Arc Cosine";
        }

        @Override
        public String getDescription() {
            return "Returns the arc cosine of a value; the returned angle is in the range 0.0 through pi.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ATAN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.atan(a);
        }

        @Override
        public String getExpressionName() {
            return "atan";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The value whose arc tangent is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Arc Tangent";
        }

        @Override
        public String getDescription() {
            return "Returns the arc tangent of a value; the returned angle is in the range -pi/2 through pi/2.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ATAN2 = new ADoubleBinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.atan2(a, b);
        }

        @Override
        public String getExpressionName() {
            return "atan2";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            switch (index) {
            case 0:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "y";
                    }

                    @Override
                    public String getName() {
                        return "Y";
                    }

                    @Override
                    public String getDescription() {
                        return "The ordinate coordinate.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            case 1:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "x";
                    }

                    @Override
                    public String getName() {
                        return "X";
                    }

                    @Override
                    public String getDescription() {
                        return "The abscissa coordinate.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            default:
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }

        @Override
        public String getName() {
            return "Angle Theta";
        }

        @Override
        public String getDescription() {
            return "Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta). "
                    + "This method computes the phase theta by computing an arc tangent of y/x in the range of -pi to pi.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction ROUND = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.round(a);
        }

        @Override
        public String getExpressionName() {
            return "round";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "A floating-point value to be rounded to a long.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Round";
        }

        @Override
        public String getDescription() {
            return "Returns the closest long to the argument, with ties rounding to positive infinity.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction FLOOR = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.floor(a);
        }

        @Override
        public String getExpressionName() {
            return "floor";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Floor";
        }

        @Override
        public String getDescription() {
            return "Returns the largest (closest to positive infinity) double value that is less than "
                    + "or equal to the argument and is equal to a mathematical integer.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction CEIL = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.ceil(a);
        }

        @Override
        public String getExpressionName() {
            return "ceil";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Ceiling";
        }

        @Override
        public String getDescription() {
            return "Returns the smallest (closest to negative infinity) double value that is greater than "
                    + "or equal to the argument and is equal to a mathematical integer.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction POW = new ADoubleBinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Doubles.pow(a, b);
        }

        @Override
        public String getExpressionName() {
            return "pow";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            switch (index) {
            case 0:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "base";
                    }

                    @Override
                    public String getName() {
                        return "Base";
                    }

                    @Override
                    public String getDescription() {
                        return "The base.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            case 1:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "exponent";
                    }

                    @Override
                    public String getName() {
                        return "Exponent";
                    }

                    @Override
                    public String getDescription() {
                        return "The exponent.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            default:
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }

        @Override
        public String getName() {
            return "Power";
        }

        @Override
        public String getDescription() {
            return "Returns the value of the first argument raised to the power of the second argument.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction SQRT = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.sqrt(a);
        }

        @Override
        public String getExpressionName() {
            return "sqrt";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }

            };
        }

        @Override
        public String getName() {
            return "Square Root";
        }

        @Override
        public String getDescription() {
            return "Returns the correctly rounded positive square root of a double value.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction SQUARE = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.square(a);
        }

        @Override
        public String getExpressionName() {
            return "square";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }

            };
        }

        @Override
        public String getName() {
            return "Square";
        }

        @Override
        public String getDescription() {
            return "Returns the square of the given value: value^2";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction EXP = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.exp(a);
        }

        @Override
        public String getExpressionName() {
            return "exp";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "exponent";
                }

                @Override
                public String getName() {
                    return "Exponent";
                }

                @Override
                public String getDescription() {
                    return "The exponent to raise e to.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Natural Exponential";
        }

        @Override
        public String getDescription() {
            return "Returns Euler's number e raised to the power of a double value.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction LN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.log(a);
        }

        @Override
        public String getExpressionName() {
            return "ln";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Natural Logarithm";
        }

        @Override
        public String getDescription() {
            return "Returns the natural logarithm (base e) of a double value.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction LOG = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Doubles.log10(a);
        }

        @Override
        public String getExpressionName() {
            return "log";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Base 10 Logarithm";
        }

        @Override
        public String getDescription() {
            return "Returns the base 10 logarithm of a double value.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction SIGN = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.signum(a);
        }

        @Override
        public String getExpressionName() {
            return "sign";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The floating-point value whose signum is to be returned.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Signum";
        }

        @Override
        public String getDescription() {
            return "Returns the signum function of the argument; zero if the argument is zero, "
                    + "1.0 if the argument is greater than zero, -1.0 if the argument is less than zero.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction DEG = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toDegrees(a);
        }

        @Override
        public String getExpressionName() {
            return "deg";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "angleRadians";
                }

                @Override
                public String getName() {
                    return "Angle Radians";
                }

                @Override
                public String getDescription() {
                    return "An angle, in radians.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Degrees";
        }

        @Override
        public String getDescription() {
            return "Converts an angle measured in radians to an approximately equivalent angle measured in degrees. "
                    + "The conversion from radians to degrees is generally inexact; users should not expect cos(rad(90.0)) to exactly equal 0.0.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction RAD = new ADoubleUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toRadians(a);
        }

        @Override
        public String getExpressionName() {
            return "rad";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "angleDegrees";
                }

                @Override
                public String getName() {
                    return "Angle Degrees";
                }

                @Override
                public String getDescription() {
                    return "An angle, in degrees.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Radians";
        }

        @Override
        public String getDescription() {
            return "Converts an angle measured in degrees to an approximately equivalent angle measured in radians. "
                    + "The conversion from degrees to radians is generally inexact.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction NEGATE = new ADoubleFunction() {

        @Override
        public String getExpressionName() {
            return "negate";
        }

        @Override
        public boolean isNaturalFunction(final IExpression[] args) {
            return true;
        }

        @Override
        public int getNumberOfArguments() {
            return 1;
        }

        @Override
        public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
            final IEvaluateDoubleFDate aF = args[0].newEvaluateDoubleFDate();
            return key -> {
                final double a = aF.evaluateDouble(key);
                return -a;
            };
        }

        @Override
        public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
            final IEvaluateDoubleKey aF = args[0].newEvaluateDoubleKey();
            return key -> {
                final double a = aF.evaluateDouble(key);
                return -a;
            };
        }

        @Override
        public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
            final IEvaluateDouble aF = args[0].newEvaluateDouble();
            return () -> {
                final double a = aF.evaluateDouble();
                return -a;
            };
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            if (index != 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return new IFunctionParameterInfo() {

                @Override
                public String getType() {
                    return ExpressionReturnType.Double.toString();
                }

                @Override
                public String getExpressionName() {
                    return "value";
                }

                @Override
                public String getName() {
                    return "Value";
                }

                @Override
                public String getDescription() {
                    return "The value to negate.";
                }

                @Override
                public boolean isOptional() {
                    return false;
                }

                @Override
                public boolean isVarArgs() {
                    return false;
                }

                @Override
                public String getDefaultValue() {
                    return null;
                }
            };
        }

        @Override
        public String getName() {
            return "Numerical Negation";
        }

        @Override
        public String getDescription() {
            return "A positive number will become negative, a negative value will become positive. 0 will stay 0.";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final ADoubleFunction NORMALIZE_VALUE = new ADoubleTernaryFunction() {
        @Override
        protected double eval(final double a, final double b, final double c) {
            return Doubles.normalize(a, b, c);
        }

        @Override
        public String getExpressionName() {
            return "normalizeValue";
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            switch (index) {
            case 0:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "value";
                    }

                    @Override
                    public String getName() {
                        return "Value";
                    }

                    @Override
                    public String getDescription() {
                        return "An argument.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            case 1:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "min";
                    }

                    @Override
                    public String getName() {
                        return "Min";
                    }

                    @Override
                    public String getDescription() {
                        return "The minimum allowed value.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            case 2:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "max";
                    }

                    @Override
                    public String getName() {
                        return "Max";
                    }

                    @Override
                    public String getDescription() {
                        return "The maximum allowed value.";
                    }

                    @Override
                    public boolean isOptional() {
                        return false;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            default:
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }

        @Override
        public String getName() {
            return "Normalize Value";
        }

        @Override
        public String getDescription() {
            return "This normalizes a series to a value between -1 and 1 using this formula: "
                    + "<ul><li>2 * (value - min / (max - min) - 1D</li></ul>";
        }

        @Override
        public boolean shouldCompress() {
            return true;
        }

        @Override
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    private MathFunctions() {
    }

    public static ADoubleFunction newRandomFunction(final String expressionName) {
        return new ADoubleFunction() {
            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                if (index != 0) {
                    throw new ArrayIndexOutOfBoundsException(index);
                }
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Double.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "seed";
                    }

                    @Override
                    public String getName() {
                        return "Seed";
                    }

                    @Override
                    public String getDescription() {
                        return "A seed to initialize the random generator with";
                    }

                    @Override
                    public boolean isOptional() {
                        return true;
                    }

                    @Override
                    public boolean isVarArgs() {
                        return false;
                    }

                    @Override
                    public String getDefaultValue() {
                        return null;
                    }
                };
            }

            @Override
            public String getName() {
                return "Random";
            }

            @Override
            public String getDescription() {
                return "Returns a double value with a positive sign, greater than or equal to 0.0 and less than 1.0. This value is multiplied by the argument.";
            }

            @Override
            public boolean shouldCompress() {
                return true;
            }

            @Override
            public boolean shouldPersist() {
                return false;
            }

            @Override
            public boolean shouldDraw() {
                return true;
            }

            @Override
            public int getNumberOfArguments() {
                return 1;
            }

            @Override
            public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
                if (args.length == 0) {
                    final RandomGenerator random = RandomGenerators.newDefaultRandom();
                    return key -> random.nextDouble();
                } else {
                    final IEvaluateDouble aF = args[0].newEvaluateDouble();
                    final long seed = (long) aF.evaluateDouble();
                    final RandomGenerator random = RandomGenerators.newDefaultRandom(seed);
                    return key -> {
                        return random.nextDouble();
                    };
                }
            }

            @Override
            public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
                if (args.length == 0) {
                    final RandomGenerator random = RandomGenerators.newDefaultRandom();
                    return key -> random.nextDouble();
                } else {
                    final IEvaluateDouble aF = args[0].newEvaluateDouble();
                    final long seed = (long) aF.evaluateDouble();
                    final RandomGenerator random = RandomGenerators.newDefaultRandom(seed);
                    return key -> {
                        return random.nextDouble();
                    };
                }
            }

            @Override
            public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
                if (args.length == 0) {
                    final RandomGenerator random = RandomGenerators.newDefaultRandom();
                    return () -> random.nextDouble();
                } else {
                    final IEvaluateDouble aF = args[0].newEvaluateDouble();
                    final long seed = (long) aF.evaluateDouble();
                    final RandomGenerator random = RandomGenerators.newDefaultRandom(seed);
                    return () -> {
                        return random.nextDouble();
                    };
                }
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return false;
            }
        };
    }

    public static ADoubleFunction newMinimumFunction(final String name) {
        return new ADoubleFunction() {

            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                switch (index) {
                case 0:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "value1";
                        }

                        @Override
                        public String getName() {
                            return "Value 1";
                        }

                        @Override
                        public String getDescription() {
                            return "An argument.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return false;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                case 1:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "valueN";
                        }

                        @Override
                        public String getName() {
                            return "Value N";
                        }

                        @Override
                        public String getDescription() {
                            return "Another agument.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return true;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                default:
                    throw new ArrayIndexOutOfBoundsException(index);
                }
            }

            @Override
            public String getName() {
                return "Minimum";
            }

            @Override
            public String getDescription() {
                return "Returns the smaller of two or more double values. That is, the result is the value closer to negative infinity. "
                        + "NaN is ignored.";
            }

            @Override
            public boolean shouldCompress() {
                return true;
            }

            @Override
            public boolean shouldPersist() {
                return false;
            }

            @Override
            public boolean shouldDraw() {
                return true;
            }

            @Override
            public int getNumberOfArguments() {
                return 2;
            }

            @Override
            public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
                final IEvaluateDoubleFDate[] argsF = new IEvaluateDoubleFDate[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDoubleFDate();
                }
                return key -> {
                    double min = argsF[0].evaluateDouble(key);
                    for (int i = 1; i < argsF.length; i++) {
                        min = Doubles.min(min, argsF[i].evaluateDouble(key));
                    }
                    return min;
                };
            }

            @Override
            public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
                final IEvaluateDoubleKey[] argsF = new IEvaluateDoubleKey[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDoubleKey();
                }
                return key -> {
                    double min = argsF[0].evaluateDouble(key);
                    for (int i = 1; i < argsF.length; i++) {
                        min = Doubles.min(min, argsF[i].evaluateDouble(key));
                    }
                    return min;
                };
            }

            @Override
            public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
                final IEvaluateDouble[] argsF = new IEvaluateDouble[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDouble();
                }
                return () -> {
                    double min = argsF[0].evaluateDouble();
                    for (int i = 1; i < argsF.length; i++) {
                        min = Doubles.min(min, argsF[i].evaluateDouble());
                    }
                    return min;
                };
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return true;
            }
        };
    }

    public static ADoubleFunction newMaximumFunction(final String name) {
        return new ADoubleFunction() {

            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                switch (index) {
                case 0:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "value1";
                        }

                        @Override
                        public String getName() {
                            return "Value 1";
                        }

                        @Override
                        public String getDescription() {
                            return "An argument.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return false;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                case 1:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "valueN";
                        }

                        @Override
                        public String getName() {
                            return "Value N";
                        }

                        @Override
                        public String getDescription() {
                            return "Another agument.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return true;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                default:
                    throw new ArrayIndexOutOfBoundsException(index);
                }
            }

            @Override
            public String getName() {
                return "Maximum";
            }

            @Override
            public String getDescription() {
                return "Returns the greater of two or more double values. That is, the result is the argument closer to positive infinity. "
                        + "NaN is ignored.";
            }

            @Override
            public boolean shouldCompress() {
                return true;
            }

            @Override
            public boolean shouldPersist() {
                return false;
            }

            @Override
            public boolean shouldDraw() {
                return true;
            }

            @Override
            public int getNumberOfArguments() {
                return 2;
            }

            @Override
            public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
                final IEvaluateDoubleFDate[] argsF = new IEvaluateDoubleFDate[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDoubleFDate();
                }
                return key -> {
                    double max = argsF[0].evaluateDouble(key);
                    for (int i = 1; i < argsF.length; i++) {
                        max = Doubles.max(max, argsF[i].evaluateDouble(key));
                    }
                    return max;
                };
            }

            @Override
            public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
                final IEvaluateDoubleKey[] argsF = new IEvaluateDoubleKey[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDoubleKey();
                }
                return key -> {
                    double max = argsF[0].evaluateDouble(key);
                    for (int i = 1; i < argsF.length; i++) {
                        max = Doubles.max(max, argsF[i].evaluateDouble(key));
                    }
                    return max;
                };
            }

            @Override
            public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
                final IEvaluateDouble[] argsF = new IEvaluateDouble[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsF[i] = args[i].newEvaluateDouble();
                }
                return () -> {
                    double max = argsF[0].evaluateDouble();
                    for (int i = 1; i < argsF.length; i++) {
                        max = Doubles.max(max, argsF[i].evaluateDouble());
                    }
                    return max;
                };
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return true;
            }
        };
    }

    public static ADoubleTernaryFunction newBetweenFunction(final String name) {
        return new ADoubleTernaryFunction() {
            @Override
            protected double eval(final double a, final double b, final double c) {
                return Doubles.between(a, b, c);
            }

            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                switch (index) {
                case 0:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "value";
                        }

                        @Override
                        public String getName() {
                            return "Value";
                        }

                        @Override
                        public String getDescription() {
                            return "An argument.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return false;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                case 1:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "min";
                        }

                        @Override
                        public String getName() {
                            return "Min";
                        }

                        @Override
                        public String getDescription() {
                            return "The minimum allowed value.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return false;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                case 2:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Double.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "max";
                        }

                        @Override
                        public String getName() {
                            return "Max";
                        }

                        @Override
                        public String getDescription() {
                            return "The maximum allowed value.";
                        }

                        @Override
                        public boolean isOptional() {
                            return false;
                        }

                        @Override
                        public boolean isVarArgs() {
                            return false;
                        }

                        @Override
                        public String getDefaultValue() {
                            return null;
                        }
                    };
                default:
                    throw new ArrayIndexOutOfBoundsException(index);
                }
            }

            @Override
            public String getName() {
                return "Between";
            }

            @Override
            public String getDescription() {
                return "Returns the value that is less or equal to max and higher or equal to min. "
                        + "Thus ensuring maximum and minimum thresholds at the same time and returning "
                        + "the maximum or minimum when one of them is breached.";
            }

            @Override
            public boolean shouldCompress() {
                return true;
            }

            @Override
            public boolean shouldPersist() {
                return false;
            }

            @Override
            public boolean shouldDraw() {
                return true;
            }
        };
    }

}
