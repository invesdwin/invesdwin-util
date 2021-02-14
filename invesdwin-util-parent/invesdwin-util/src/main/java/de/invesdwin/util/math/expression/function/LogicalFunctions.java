package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;

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
        public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
            final IEvaluateBooleanFDate checkF = args[0].newEvaluateBooleanFDate();
            final IEvaluateDoubleFDate trueF = args[1].newEvaluateDoubleFDate();
            final IEvaluateDoubleFDate falseF = args[2].newEvaluateDoubleFDate();
            return key -> {
                final boolean check = checkF.evaluateBoolean(key);
                if (check) {
                    return trueF.evaluateDouble(key);
                } else {
                    return falseF.evaluateDouble(key);
                }
            };
        }

        @Override
        public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
            final IEvaluateBooleanKey checkF = args[0].newEvaluateBooleanKey();
            final IEvaluateDoubleKey trueF = args[1].newEvaluateDoubleKey();
            final IEvaluateDoubleKey falseF = args[2].newEvaluateDoubleKey();
            return key -> {
                final boolean check = checkF.evaluateBoolean(key);
                if (check) {
                    return trueF.evaluateDouble(key);
                } else {
                    return falseF.evaluateDouble(key);
                }
            };
        }

        @Override
        public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
            final IEvaluateBoolean checkF = args[0].newEvaluateBoolean();
            final IEvaluateDouble trueF = args[1].newEvaluateDouble();
            final IEvaluateDouble falseF = args[2].newEvaluateDouble();
            return () -> {
                final boolean check = checkF.evaluateBoolean();
                if (check) {
                    return trueF.evaluateDouble();
                } else {
                    return falseF.evaluateDouble();
                }
            };
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
        public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context, final IExpression[] args) {
            final IEvaluateDoubleFDate argF = args[0].newEvaluateDoubleFDate();
            return key -> {
                final double a = argF.evaluateDouble(key);
                return Double.isNaN(a);
            };
        }

        @Override
        public IEvaluateBooleanKey newEvaluateBooleanKey(final String context, final IExpression[] args) {
            final IEvaluateDoubleKey argF = args[0].newEvaluateDoubleKey();
            return key -> {
                final double a = argF.evaluateDouble(key);
                return Double.isNaN(a);
            };
        }

        @Override
        public IEvaluateBoolean newEvaluateBoolean(final String context, final IExpression[] args) {
            final IEvaluateDouble argF = args[0].newEvaluateDouble();
            return () -> {
                final double a = argF.evaluateDouble();
                return Double.isNaN(a);
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
        public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context, final IExpression[] args) {
            final IEvaluateBooleanFDate argF = args[0].newEvaluateBooleanFDate();
            return key -> argF.evaluateBoolean(key);
        }

        @Override
        public IEvaluateBooleanKey newEvaluateBooleanKey(final String context, final IExpression[] args) {
            final IEvaluateBooleanKey argF = args[0].newEvaluateBooleanKey();
            return key -> argF.evaluateBoolean(key);
        }

        @Override
        public IEvaluateBoolean newEvaluateBoolean(final String context, final IExpression[] args) {
            final IEvaluateBoolean argF = args[0].newEvaluateBoolean();
            return () -> argF.evaluateBoolean();
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
        public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context, final IExpression[] args) {
            final IEvaluateBooleanNullableFDate argF = args[0].newEvaluateBooleanNullableFDate();
            return key -> {
                final Boolean a = argF.evaluateBooleanNullable(key);
                return Booleans.isFalse(a);
            };
        }

        @Override
        public IEvaluateBooleanKey newEvaluateBooleanKey(final String context, final IExpression[] args) {
            final IEvaluateBooleanNullableKey argF = args[0].newEvaluateBooleanNullableKey();
            return key -> {
                final Boolean a = argF.evaluateBooleanNullable(key);
                return Booleans.isFalse(a);
            };
        }

        @Override
        public IEvaluateBoolean newEvaluateBoolean(final String context, final IExpression[] args) {
            final IEvaluateBooleanNullable argF = args[0].newEvaluateBooleanNullable();
            return () -> {
                final Boolean a = argF.evaluateBooleanNullable();
                return Booleans.isFalse(a);
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
        public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate(final String context,
                final IExpression[] args) {
            final IEvaluateBooleanNullableFDate argF = args[0].newEvaluateBooleanNullableFDate();
            return key -> {
                final Boolean a = argF.evaluateBooleanNullable(key);
                if (a == null) {
                    return null;
                } else {
                    return !(a == Boolean.TRUE);
                }
            };
        }

        @Override
        public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey(final String context,
                final IExpression[] args) {
            final IEvaluateBooleanNullableKey argF = args[0].newEvaluateBooleanNullableKey();
            return key -> {
                final Boolean a = argF.evaluateBooleanNullable(key);
                if (a == null) {
                    return null;
                } else {
                    return !(a == Boolean.TRUE);
                }
            };
        }

        @Override
        public IEvaluateBooleanNullable newEvaluateBooleanNullable(final String context, final IExpression[] args) {
            final IEvaluateBooleanNullable argF = args[0].newEvaluateBooleanNullable();
            return () -> {
                final Boolean a = argF.evaluateBooleanNullable();
                if (a == null) {
                    return null;
                } else {
                    return !(a == Boolean.TRUE);
                }
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
            public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context, final IExpression[] args) {
                final IEvaluateIntegerFDate indexF = args[0].newEvaluateIntegerFDate();
                final IEvaluateDoubleFDate[] argsF = new IEvaluateDoubleFDate[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateDoubleFDate();
                }
                return key -> {
                    final int index = Integers.between(indexF.evaluateInteger(key) + 1, 0, argsF.length);
                    return argsF[index].evaluateDouble(key);
                };
            }

            @Override
            public IEvaluateDoubleKey newEvaluateDoubleKey(final String context, final IExpression[] args) {
                final IEvaluateIntegerKey indexF = args[0].newEvaluateIntegerKey();
                final IEvaluateDoubleKey[] argsF = new IEvaluateDoubleKey[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateDoubleKey();
                }
                return key -> {
                    final int index = Integers.between(indexF.evaluateInteger(key) + 1, 0, argsF.length);
                    return argsF[index].evaluateDouble(key);
                };
            }

            @Override
            public IEvaluateDouble newEvaluateDouble(final String context, final IExpression[] args) {
                final IEvaluateInteger indexF = args[0].newEvaluateInteger();
                final IEvaluateDouble[] argsF = new IEvaluateDouble[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateDouble();
                }
                return () -> {
                    final int index = Integers.between(indexF.evaluateInteger() + 1, 0, argsF.length);
                    return argsF[index].evaluateDouble();
                };
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
            public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context, final IExpression[] args) {
                final IEvaluateDoubleFDate thresholdF = args[0].newEvaluateDoubleFDate();
                final IEvaluateBooleanFDate[] argsF = new IEvaluateBooleanFDate[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateBooleanFDate();
                }
                return key -> {
                    final double threshold = thresholdF.evaluateDouble(key) * (args.length - 1);
                    double sum = 0D;
                    for (int i = 0; i < argsF.length; i++) {
                        if (argsF[i].evaluateBoolean(key)) {
                            sum++;
                            if (sum >= threshold) {
                                return true;
                            }
                        }
                    }
                    return false;
                };
            }

            @Override
            public IEvaluateBooleanKey newEvaluateBooleanKey(final String context, final IExpression[] args) {
                final IEvaluateDoubleKey thresholdF = args[0].newEvaluateDoubleKey();
                final IEvaluateBooleanKey[] argsF = new IEvaluateBooleanKey[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateBooleanKey();
                }
                return key -> {
                    final double threshold = thresholdF.evaluateDouble(key) * (args.length - 1);
                    double sum = 0D;
                    for (int i = 0; i < argsF.length; i++) {
                        if (argsF[i].evaluateBoolean(key)) {
                            sum++;
                            if (sum >= threshold) {
                                return true;
                            }
                        }
                    }
                    return false;
                };
            }

            @Override
            public IEvaluateBoolean newEvaluateBoolean(final String context, final IExpression[] args) {
                final IEvaluateDouble thresholdF = args[0].newEvaluateDouble();
                final IEvaluateBoolean[] argsF = new IEvaluateBoolean[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    argsF[i - 1] = args[i].newEvaluateBoolean();
                }
                return () -> {
                    final double threshold = thresholdF.evaluateDouble() * (args.length - 1);
                    double sum = 0D;
                    for (int i = 1; i < argsF.length; i++) {
                        if (argsF[i].evaluateBoolean()) {
                            sum++;
                            if (sum >= threshold) {
                                return true;
                            }
                        }
                    }
                    return false;
                };
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
