package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public final class LogicalFunctions {

    public static final ADoubleFunction IF = new ADoubleFunction() {

        @Override
        public String getExpressionName() {
            return "if";
        }

        @Override
        public int getNumberOfArguments() {
            return 3;
        }

        @Override
        public double eval(final IFDateProvider key, final IExpression[] args) {
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

    public static final ABooleanFunction ISNAN = new ABooleanFunction() {

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
        public boolean eval(final IFDateProvider key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            return Double.isNaN(a);
        }

        @Override
        public boolean eval(final int key, final IExpression[] args) {
            final double a = args[0].evaluateDouble(key);
            return Double.isNaN(a);
        }

        @Override
        public boolean eval(final IExpression[] args) {
            final double a = args[0].evaluateDouble();
            return Double.isNaN(a);
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

    public static final ABooleanFunction ISTRUE = new ABooleanFunction() {

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
        public boolean eval(final IFDateProvider key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            return Booleans.isTrue(a);
        }

        @Override
        public boolean eval(final int key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            return Booleans.isTrue(a);
        }

        @Override
        public boolean eval(final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable();
            return Booleans.isTrue(a);
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

    public static final ABooleanFunction ISFALSE = new ABooleanFunction() {

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
        public boolean eval(final IFDateProvider key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            return Booleans.isFalse(a);
        }

        @Override
        public boolean eval(final int key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            return Booleans.isFalse(a);
        }

        @Override
        public boolean eval(final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable();
            return Booleans.isFalse(a);
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

    public static final ABooleanNullableFunction NOT = new ABooleanNullableFunction() {

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
        public Boolean eval(final IFDateProvider key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            if (a == null) {
                return null;
            } else {
                return !(a == Boolean.TRUE);
            }
        }

        @Override
        public Boolean eval(final int key, final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable(key);
            if (a == null) {
                return null;
            } else {
                return !(a == Boolean.TRUE);
            }
        }

        @Override
        public Boolean eval(final IExpression[] args) {
            final Boolean a = args[0].evaluateBooleanNullable();
            if (a == null) {
                return null;
            } else {
                return !(a == Boolean.TRUE);
            }
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

    private LogicalFunctions() {
    }

    public static ADoubleFunction newMapFunction(final String expressionName) {
        return new ADoubleFunction() {

            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public int getNumberOfArguments() {
                return 3;
            }

            @Override
            public double eval(final IFDateProvider key, final IExpression[] args) {
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

    public static ABooleanFunction newVoteFunction(final String expressionName) {
        return new ABooleanFunction() {

            @Override
            public String getExpressionName() {
                return expressionName;
            }

            @Override
            public int getNumberOfArguments() {
                return 3;
            }

            @Override
            public boolean eval(final IFDateProvider key, final IExpression[] args) {
                final double threshold = args[0].evaluateDouble(key) * (args.length - 1);
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean(key)) {
                        sum++;
                        if (sum >= threshold) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean eval(final int key, final IExpression[] args) {
                final double threshold = args[0].evaluateDouble(key) * (args.length - 1);
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean(key)) {
                        sum++;
                        if (sum >= threshold) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean eval(final IExpression[] args) {
                final double threshold = args[0].evaluateDouble() * (args.length - 1);
                double sum = 0D;
                for (int i = 1; i < args.length; i++) {
                    if (args[i].evaluateBoolean()) {
                        sum++;
                        if (sum >= threshold) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return true;
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
