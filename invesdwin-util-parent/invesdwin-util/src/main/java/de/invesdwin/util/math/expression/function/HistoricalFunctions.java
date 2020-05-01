package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class HistoricalFunctions {

    private HistoricalFunctions() {
    }

    public static IFunctionFactory newOnceFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
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
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Once (On Historical Change Only)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the condition just became true: !condition[1] && condition[0]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final boolean cur = condition.evaluateBoolean(key);
                        if (!cur) {
                            return 0D;
                        }
                        final int prevKey = previousKeyFunction.getPreviousKey(key, 1);
                        final boolean prev = condition.evaluateBoolean(prevKey);
                        if (prev) {
                            return 0D;
                        }
                        return 1D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final boolean cur = condition.evaluateBoolean(key);
                        if (!cur) {
                            return 0D;
                        }
                        final FDate prevKey = previousKeyFunction.getPreviousKey(key, 1);
                        final boolean prev = condition.evaluateBoolean(prevKey);
                        if (prev) {
                            return 0D;
                        }
                        return 1D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Stable (Historical AND)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the condition is stable over a range of previous keys: condition[0] && condition[1] && ... && condition[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final IExpression condition = args[1];
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = condition.evaluateBoolean(curKey);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final IExpression condition = args[1];
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = condition.evaluateBoolean(curKey);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableLeftFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        BinaryOperation.validateComparativeOperation(args[1]);
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                                    return "The binary boolean expression to evaluate. A value greater than 0 means true.";
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
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Stable Left (Historical AND)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the binary condition (greater, less, equal, etc]) is stable over a range of previous keys on the left side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] && condition.left[1] > condition.right[0] && ... && condition.left[n-1] > condition.right[0]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableRightFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        BinaryOperation.validateComparativeOperation(args[1]);
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                                    return "The binary boolean expression to evaluate. A value greater than 0 means true.";
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
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Stable Right (Historical AND)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the binary condition (greater, less, equal, etc]) is stable over a range of previous keys on the right side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] && condition.left[0] > condition.right[1] && ... && condition.left[0] > condition.right[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (!result) {
                                return 0D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 1D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Occurs (Historical OR)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the condition occurs true over a range of previous keys: condition[0] || condition[1] || ... || condition[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final IExpression condition = args[1];
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = condition.evaluateBoolean(curKey);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final IExpression condition = args[1];
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = condition.evaluateBoolean(curKey);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursLeftFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        BinaryOperation.validateComparativeOperation(args[1]);
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                                    return "The binary boolean expression to evaluate. A value greater than 0 means true.";
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
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Occurs Left (Historical OR)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the binary condition (greater, less, equal, etc]) occurs true over a range of previous keys on the left side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] || condition.left[1] > condition.right[0] || ... || condition.left[n-1] > condition.right[0]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursRightFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return false;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        BinaryOperation.validateComparativeOperation(args[1]);
                        return false;
                    }

                    @Override
                    public ExpressionReturnType getReturnType() {
                        return ExpressionReturnType.Boolean;
                    }

                    @Override
                    protected IFunctionParameterInfo getParameterInfo(final int index) {
                        switch (index) {
                        case 0:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "count";
                                }

                                @Override
                                public String getName() {
                                    return "count";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked?";
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
                                    return "The binary boolean expression to evaluate. A value greater than 0 means true.";
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
                    public int getNumberOfArguments() {
                        return 0;
                    }

                    @Override
                    public String getName() {
                        return "Occurs Right (Historical OR)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks that the binary condition (greater, less, equal, etc]) occurs true over a range of previous keys on the right side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] || condition.left[0] > condition.right[1] || ... || condition.left[0] > condition.right[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final int count = args[0].evaluateInteger(key);
                        final BinaryOperation condition = BinaryOperation.validateComparativeOperation(args[1]);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = condition.getOp().applyBoolean(leftResult, rightResult);
                            if (result) {
                                return 1D;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return 0D;
                    }
                };
            }

        };
    }

}
