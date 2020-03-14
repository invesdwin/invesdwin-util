package de.invesdwin.util.math.expression.eval;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.AFunction;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class Functions {

    public static final AFunction SIN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sin(a);
        }

        @Override
        public String getExpressionName() {
            return "sin";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction SINH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sinh(a);
        }

        @Override
        public String getExpressionName() {
            return "sinh";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction COS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cos(a);
        }

        @Override
        public String getExpressionName() {
            return "cos";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction COSH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.cosh(a);
        }

        @Override
        public String getExpressionName() {
            return "cosh";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction TAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tan(a);
        }

        @Override
        public String getExpressionName() {
            return "tan";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction TANH = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.tanh(a);
        }

        @Override
        public String getExpressionName() {
            return "tanh";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ABS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.abs(a);
        }

        @Override
        public String getExpressionName() {
            return "abs";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ASIN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.asin(a);
        }

        @Override
        public String getExpressionName() {
            return "asin";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ACOS = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.acos(a);
        }

        @Override
        public String getExpressionName() {
            return "acos";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ATAN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.atan(a);
        }

        @Override
        public String getExpressionName() {
            return "atan";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ATAN2 = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.atan2(a, b);
        }

        @Override
        public String getExpressionName() {
            return "atan2";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction ROUND = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.round(a);
        }

        @Override
        public String getExpressionName() {
            return "round";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction FLOOR = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.floor(a);
        }

        @Override
        public String getExpressionName() {
            return "floor";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction CEIL = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.ceil(a);
        }

        @Override
        public String getExpressionName() {
            return "ceil";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction POW = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.pow(a, b);
        }

        @Override
        public String getExpressionName() {
            return "pow";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction SQRT = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.sqrt(a);
        }

        @Override
        public String getExpressionName() {
            return "sqrt";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction EXP = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.exp(a);
        }

        @Override
        public String getExpressionName() {
            return "exp";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction LN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log(a);
        }

        @Override
        public String getExpressionName() {
            return "ln";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction LOG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.log10(a);
        }

        @Override
        public String getExpressionName() {
            return "log";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction MIN = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.min(a, b);
        }

        @Override
        public String getExpressionName() {
            return "min";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
                        return "value2";
                    }

                    @Override
                    public String getName() {
                        return "Value 2";
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
            return "Minimum";
        }

        @Override
        public String getDescription() {
            return "Returns the smaller of two double values. That is, the result is the value closer to negative infinity. "
                    + "If the arguments have the same value, the result is that same value. If either value is NaN, then the result is NaN. "
                    + "Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero. "
                    + "If one argument is positive zero and the other is negative zero, the result is negative zero.";
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

    public static final AFunction MAX = new ABinaryFunction() {
        @Override
        protected double eval(final double a, final double b) {
            return Math.max(a, b);
        }

        @Override
        public String getExpressionName() {
            return "max";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
                        return "value2";
                    }

                    @Override
                    public String getName() {
                        return "Value 2";
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
            return "Maximum";
        }

        @Override
        public String getDescription() {
            return "Returns the greater of two double values. That is, the result is the argument closer to positive infinity. "
                    + "If the arguments have the same value, the result is that same value. If either value is NaN, then the result is NaN. "
                    + "Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero. "
                    + "If one argument is positive zero and the other negative zero, the result is positive zero.";
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

    public static final AFunction BETWEEN = new ATernaryFunction() {
        @Override
        protected double eval(final double a, final double b, final double c) {
            return Doubles.between(a, b, c);
        }

        @Override
        public String getExpressionName() {
            return "between";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction RANDOM = newRandomFunction("random");
    public static final AFunction RND = newRandomFunction("rnd");
    public static final AFunction RNG = newRandomFunction("rng");

    public static final AFunction SIGN = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.signum(a);
        }

        @Override
        public String getExpressionName() {
            return "sign";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction DEG = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toDegrees(a);
        }

        @Override
        public String getExpressionName() {
            return "deg";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction RAD = new AUnaryFunction() {
        @Override
        protected double eval(final double a) {
            return Math.toRadians(a);
        }

        @Override
        public String getExpressionName() {
            return "rad";
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction IF = new AFunction() {

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
        public boolean isNaturalFunction(final IExpression[] args) {
            return true;
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Double;
        }

        @Override
        public IFunctionParameterInfo getParameterInfo(final int index) {
            switch (index) {
            case 0:
                return new IFunctionParameterInfo() {

                    @Override
                    public String getType() {
                        return ExpressionReturnType.Boolean.toString();
                    }

                    @Override
                    public String getExpressionName() {
                        return "condition";
                    }

                    @Override
                    public String getName() {
                        return "Condition";
                    }

                    @Override
                    public String getDescription() {
                        return "The boolean expression to evaluate. A value greater than 0 means true.";
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
                        return "then";
                    }

                    @Override
                    public String getName() {
                        return "Then";
                    }

                    @Override
                    public String getDescription() {
                        return "The return value when the if condition is true.";
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
                        return "else";
                    }

                    @Override
                    public String getName() {
                        return "Else";
                    }

                    @Override
                    public String getDescription() {
                        return "The return value when the if condition is not true.";
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
            return "If Condition-Then-Else";
        }

        @Override
        public String getDescription() {
            return "If the conditional evaluation is true, then the second argument is returned, otherwise the third argument is returned.";
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

    public static final AFunction MAP = newMapFunction("map");
    public static final AFunction SELECT = newMapFunction("select");
    public static final AFunction ARRAY = newMapFunction("array");
    public static final AFunction DECIDE = newMapFunction("decide");

    public static final AFunction VOTE = newMapFunction("vote");
    public static final AFunction ENSEMBLE = newMapFunction("ensemble");
    public static final AFunction THRESHOLD = newMapFunction("threshold");
    public static final AFunction MAJORITY = newMapFunction("majority");
    public static final AFunction FUZZY = newMapFunction("fuzzy");

    public static final AFunction ISNAN = new AFunction() {

        @Override
        public String getExpressionName() {
            return "isNaN";
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
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Boolean;
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
            return "Is Not a Number (NaN)";
        }

        @Override
        public String getDescription() {
            return "Evaluates to true when the argument is equal to NaN which denotes a missing value.";
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

    public static final AFunction ISTRUE = new AFunction() {

        @Override
        public String getExpressionName() {
            return "isTrue";
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
        public double eval(final FDate key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean();
            if (a) {
                return 1D;
            } else {
                return 0D;
            }
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Boolean;
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
                    return "condition";
                }

                @Override
                public String getName() {
                    return "Condition";
                }

                @Override
                public String getDescription() {
                    return "The boolean expression to evaluate.";
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
            return "Is True";
        }

        @Override
        public String getDescription() {
            return "A value greater than 0 means true.";
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

    public static final AFunction ISFALSE = new AFunction() {

        @Override
        public String getExpressionName() {
            return "isFalse";
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
        public double eval(final FDate key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean();
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Boolean;
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
                    return "condition";
                }

                @Override
                public String getName() {
                    return "Condition";
                }

                @Override
                public String getDescription() {
                    return "The boolean expression to evaluate.";
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
            return "Is False";
        }

        @Override
        public String getDescription() {
            return "A value less than or equal to 0 means true.";
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

    public static final AFunction NEGATE = new AFunction() {

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
        public double eval(final FDate key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            return -a;
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            return -a;
        }

        @Override
        public double eval(final IExpression[] args) {
            final double a = args[0].evaluateDouble();
            return -a;
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Boolean;
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
        public boolean shouldPersist() {
            return false;
        }

        @Override
        public boolean shouldDraw() {
            return true;
        }
    };

    public static final AFunction NOT = new AFunction() {

        @Override
        public String getExpressionName() {
            return "not";
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
        public double eval(final FDate key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final int key, final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean(key);
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public double eval(final IExpression[] args) {
            final boolean a = args[0].evaluateBoolean();
            if (a) {
                return 0D;
            } else {
                return 1D;
            }
        }

        @Override
        public ExpressionReturnType getReturnType() {
            return ExpressionReturnType.Boolean;
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
                    return "condition";
                }

                @Override
                public String getName() {
                    return "Condition";
                }

                @Override
                public String getDescription() {
                    return "The boolean expression to negate.";
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
            return "Boolean Negation (Not)";
        }

        @Override
        public String getDescription() {
            return "True will become false and false will become true.";
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

    private Functions() {
    }

    private static AFunction newRandomFunction(final String expressionName) {
        return new AFunction() {
            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public ExpressionReturnType getReturnType() {
                return ExpressionReturnType.Double;
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
                        return "maxValue";
                    }

                    @Override
                    public String getName() {
                        return "Max Value";
                    }

                    @Override
                    public String getDescription() {
                        return "A value to multiply the random value with in order to define the maximum result (exclusive), 0 (inclusive) is the minimum value.";
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
            public double eval(final FDate key, final IExpression[] args) {
                if (args.length == 0) {
                    return Math.random();
                } else {
                    final double a = args[0].evaluateDouble(key);
                    return Math.random() * a;
                }
            }

            @Override
            public double eval(final int key, final IExpression[] args) {
                if (args.length == 0) {
                    return Math.random();
                } else {
                    final double a = args[0].evaluateDouble(key);
                    return Math.random() * a;
                }
            }

            @Override
            public double eval(final IExpression[] args) {
                if (args.length == 0) {
                    return Math.random();
                } else {
                    final double a = args[0].evaluateDouble();
                    return Math.random() * a;
                }
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return false;
            }
        };
    }

    private static AFunction newMapFunction(final String expressionName) {
        return new AFunction() {

            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public int getNumberOfArguments() {
                return 3;
            }

            @Override
            public double eval(final FDate key, final IExpression[] args) {
                final int index = args[0].evaluateInteger(key);
                return args[index].evaluateDouble(key);
            }

            @Override
            public double eval(final int key, final IExpression[] args) {
                final int index = args[0].evaluateInteger(key);
                return args[index].evaluateDouble(key);
            }

            @Override
            public double eval(final IExpression[] args) {
                final int index = args[0].evaluateInteger();
                return args[index].evaluateDouble();
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return true;
            }

            @Override
            public ExpressionReturnType getReturnType() {
                return ExpressionReturnType.Double;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                switch (index) {
                case 0:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Boolean.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "index";
                        }

                        @Override
                        public String getName() {
                            return "Index";
                        }

                        @Override
                        public String getDescription() {
                            return "This index defines which one of the following values should be used. "
                                    + "Starting at index 0 and ending at valueCount-1. If the index is below 0, then 0 is used. If the index is above the last value index, then the last value is used.";
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
                            return "valueAtIndex0";
                        }

                        @Override
                        public String getName() {
                            return "Value At Index 0";
                        }

                        @Override
                        public String getDescription() {
                            return "This is the first available value for index 0.";
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
                            return "valueAtIndexN";
                        }

                        @Override
                        public String getName() {
                            return "Value At Index N";
                        }

                        @Override
                        public String getDescription() {
                            return "Further values with incremented index 1..N.";
                        }

                        @Override
                        public boolean isOptional() {
                            return true;
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
                return "Map";
            }

            @Override
            public String getDescription() {
                return "With this map function one can access alternative values depending on an index. "
                        + "The index (and any indexed value) can be determined by a dynamic expression. Example: map(random*3, 10, 20, 10*3)";
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

    private static AFunction newVoteFunction(final String expressionName) {
        return new AFunction() {

            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public int getNumberOfArguments() {
                return 3;
            }

            @Override
            public double eval(final FDate key, final IExpression[] args) {
                final double threshold = args[0].evaluateDouble(key) * args.length - 1;
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean(key)) {
                        sum++;
                    }
                }
                if (sum <= threshold) {
                    return 1D;
                } else {
                    return 0D;
                }
            }

            @Override
            public double eval(final int key, final IExpression[] args) {
                final double threshold = args[0].evaluateDouble(key) * args.length - 1;
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean(key)) {
                        sum++;
                    }
                }
                if (sum <= threshold) {
                    return 1D;
                } else {
                    return 0D;
                }
            }

            @Override
            public double eval(final IExpression[] args) {
                final double threshold = args[0].evaluateDouble() * args.length - 1;
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean()) {
                        sum++;
                    }
                }
                if (sum <= threshold) {
                    return 1D;
                } else {
                    return 0D;
                }
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return true;
            }

            @Override
            public ExpressionReturnType getReturnType() {
                return ExpressionReturnType.Double;
            }

            @Override
            public IFunctionParameterInfo getParameterInfo(final int index) {
                switch (index) {
                case 0:
                    return new IFunctionParameterInfo() {

                        @Override
                        public String getType() {
                            return ExpressionReturnType.Boolean.toString();
                        }

                        @Override
                        public String getExpressionName() {
                            return "threshold";
                        }

                        @Override
                        public String getName() {
                            return "Threshold";
                        }

                        @Override
                        public String getDescription() {
                            return "This is the majority vote threshold expressed as a value between 0 and 1. E.g. 0.6 means that at least 60% of the conditions need to be true for the vote to succeed.";
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
                            return "condition1";
                        }

                        @Override
                        public String getName() {
                            return "Condition 1";
                        }

                        @Override
                        public String getDescription() {
                            return "This is the first condition to participate in the vote.";
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
                            return "conditionN";
                        }

                        @Override
                        public String getName() {
                            return "Condition N";
                        }

                        @Override
                        public String getDescription() {
                            return "Further conditions to participate in the vote.";
                        }

                        @Override
                        public boolean isOptional() {
                            return true;
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
                return "Vote";
            }

            @Override
            public String getDescription() {
                return "This function can be used to do a majority vote on multiple conditions. "
                        + "This can also be used to define ensemble strategies. "
                        + "Example: vote(0.6, close[0] > close[1], ema(25) > ema(5), vix > 10)";
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
