package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.lambda.IBooleanNullableFromDoublesBinaryOp;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public final class HistoricalFunctions {

    private HistoricalFunctions() {
        //TODO: duplicate these functions as indicators to make them faster by using a trailing previousValuesCache
    }

    public static IFunctionFactory newOnceFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        return 1;
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
                    public IEvaluateBoolean newEvaluateBoolean(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateBooleanKey newEvaluateBooleanKey(final IExpression[] args) {
                        final IEvaluateBooleanKey conditionF = args[0].newEvaluateBooleanKey();
                        return key -> {
                            final boolean cur = conditionF.evaluateBoolean(key);
                            if (!cur) {
                                return false;
                            }
                            final int prevKey = previousKeyFunction.getPreviousKey(key, 1);
                            final boolean prev = conditionF.evaluateBoolean(prevKey);
                            if (prev) {
                                return false;
                            }
                            return true;
                        };
                    }

                    @Override
                    public IEvaluateBooleanFDate newEvaluateBooleanFDate(final IExpression[] args) {
                        final IEvaluateBooleanFDate condition = args[0].newEvaluateBooleanFDate();
                        return key -> {
                            final boolean cur = condition.evaluateBoolean(key);
                            if (!cur) {
                                return false;
                            }
                            final IFDateProvider prevKey = previousKeyFunction.getPreviousKey(key, 1);
                            final boolean prev = condition.evaluateBoolean(prevKey);
                            if (prev) {
                                return false;
                            }
                            return true;
                        };
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public IEvaluateBoolean newEvaluateBoolean(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateBooleanKey newEvaluateBooleanKey(final IExpression[] args) {
                        final IEvaluateBooleanNullableKey conditionF = args[0].newEvaluateBooleanNullableKey();
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            int curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final Boolean result = conditionF.evaluateBooleanNullable(curKey);
                                if (result != null && !result) {
                                    return false;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return true;
                        };
                    }

                    @Override
                    public IEvaluateBooleanFDate newEvaluateBooleanFDate(final IExpression[] args) {
                        final IEvaluateBooleanNullableFDate conditionF = args[0].newEvaluateBooleanNullableFDate();
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            IFDateProvider curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final Boolean result = conditionF.evaluateBooleanNullable(curKey);
                                if (result != null && !result) {
                                    return false;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return true;
                        };
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableCountFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Stable Count (Historical AND Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the condition has been stable now over a range of previous keys: condition[0] && condition[1] && ... && condition[n-1]";
                    }

                    @Override
                    public IEvaluateInteger newEvaluateInteger(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final IExpression[] args) {
                        final IEvaluateBooleanNullableKey conditionF = args[0].newEvaluateBooleanNullableKey();
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            int stableCount = 0;
                            int curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final Boolean result = conditionF.evaluateBooleanNullable(curKey);
                                if (result != null && !result) {
                                    return stableCount;
                                } else if (result != null && result) {
                                    stableCount++;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return stableCount;
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final IExpression[] args) {
                        final IEvaluateBooleanNullableFDate conditionF = args[0].newEvaluateBooleanNullableFDate();
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            int stableCount = 0;
                            IFDateProvider curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final Boolean result = conditionF.evaluateBooleanNullable(curKey);
                                if (result != null && !result) {
                                    return stableCount;
                                } else if (result != null && result) {
                                    stableCount++;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return stableCount;
                        };
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public IEvaluateBoolean newEvaluateBoolean(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateBooleanKey newEvaluateBooleanKey(final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final IEvaluateDoubleKey conditionRightF = condition.getRight().newEvaluateDoubleKey();
                        final IEvaluateDoubleKey conditionLeftF = condition.getLeft().newEvaluateDoubleKey();
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        final IBooleanNullableFromDoublesBinaryOp opF = condition.getOp()
                                .newBooleanNullableFromDoubles();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            final double rightResult = conditionRightF.evaluateDouble(key);
                            int curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final double leftResult = conditionLeftF.evaluateDouble(curKey);
                                final Boolean result = opF.applyBooleanNullableFromDoubles(leftResult, rightResult);
                                if (result != null && !result) {
                                    return false;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return true;
                        };
                    }

                    @Override
                    public IEvaluateBooleanFDate newEvaluateBooleanFDate(final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final IEvaluateDoubleFDate conditionRightF = condition.getRight().newEvaluateDoubleFDate();
                        final IEvaluateDoubleFDate conditionLeftF = condition.getLeft().newEvaluateDoubleFDate();
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        final IBooleanNullableFromDoublesBinaryOp opF = condition.getOp()
                                .newBooleanNullableFromDoubles();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            final double rightResult = conditionRightF.evaluateDouble(key);
                            IFDateProvider curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final double leftResult = conditionLeftF.evaluateDouble(curKey);
                                final Boolean result = opF.applyBooleanNullableFromDoubles(leftResult, rightResult);
                                if (result != null && !result) {
                                    return false;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return true;
                        };
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableCountLeftFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Stable Count Left (Historical AND Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the binary condition (greater, less, equal, etc]) has been stable now over a range of previous keys on the left side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] && condition.left[1] > condition.right[0] && ... && condition.left[n-1] > condition.right[0]";
                    }

                    @Override
                    public IEvaluateInteger newEvaluateInteger(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final IEvaluateDoubleKey conditionRightF = condition.getRight().newEvaluateDoubleKey();
                        final IEvaluateDoubleKey conditionLeftF = condition.getLeft().newEvaluateDoubleKey();
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        final IBooleanNullableFromDoublesBinaryOp opF = condition.getOp()
                                .newBooleanNullableFromDoubles();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            final double rightResult = conditionRightF.evaluateDouble(key);
                            int stableCount = 0;
                            int curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final double leftResult = conditionLeftF.evaluateDouble(curKey);
                                final Boolean result = opF.applyBooleanNullableFromDoubles(leftResult, rightResult);
                                if (result != null) {
                                    if (result) {
                                        stableCount++;
                                    } else {
                                        return stableCount;
                                    }
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return stableCount;
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final IEvaluateDoubleFDate conditionRightF = condition.getRight().newEvaluateDoubleFDate();
                        final IEvaluateDoubleFDate conditionLeftF = condition.getLeft().newEvaluateDoubleFDate();
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        final IBooleanNullableFromDoublesBinaryOp opF = condition.getOp()
                                .newBooleanNullableFromDoubles();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            final double rightResult = conditionRightF.evaluateDouble(key);
                            int stableCount = 0;
                            IFDateProvider curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final double leftResult = conditionLeftF.evaluateDouble(curKey);
                                final Boolean result = opF.applyBooleanNullableFromDoubles(leftResult, rightResult);
                                if (result != null) {
                                    if (result) {
                                        stableCount++;
                                    } else {
                                        return stableCount;
                                    }
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return stableCount;
                        };
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public boolean eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public boolean eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final Boolean result = condition.getOp()
                                    .applyBooleanNullableFromDoubles(leftResult, rightResult);
                            if (result != null && !result) {
                                return false;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return true;
                    }

                    @Override
                    public boolean eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final Boolean result = condition.getOp()
                                    .applyBooleanNullableFromDoubles(leftResult, rightResult);
                            if (result != null && !result) {
                                return false;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return true;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStableCountRightFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Stable Count Right (Historical AND Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the binary condition (greater, less, equal, etc]) has been stable now over a range of previous keys on the right side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] && condition.left[0] > condition.right[1] && ... && condition.left[0] > condition.right[n-1]";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int stableCount = 0;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final Boolean result = condition.getOp()
                                    .applyBooleanNullableFromDoubles(leftResult, rightResult);
                            if (result != null) {
                                if (result) {
                                    stableCount++;
                                } else {
                                    return stableCount;
                                }
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return stableCount;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int stableCount = 0;
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final Boolean result = condition.getOp()
                                    .applyBooleanNullableFromDoubles(leftResult, rightResult);
                            if (result != null) {
                                if (result) {
                                    stableCount++;
                                } else {
                                    return stableCount;
                                }
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return stableCount;
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public boolean eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public boolean eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean eval(final IFDateProvider key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursCountFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Occurs Count (Historical OR Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the condition occurs true over a range of previous keys: condition[0] || condition[1] || ... || condition[n-1]";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        int occursCount = 0;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        int occursCount = 0;
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public boolean eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public boolean eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursCountLeftFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Occurs Count Left (Historical OR Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the binary condition (greater, less, equal, etc]) occurs true over a range of previous keys on the left side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] || condition.left[1] > condition.right[0] || ... || condition.left[n-1] > condition.right[0]";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        int occursCount = 0;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double rightResult = condition.getRight().evaluateDouble(key);
                        int occursCount = 0;
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double leftResult = condition.getLeft().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
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
            public ABooleanFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new ABooleanFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
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
                    public boolean eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public boolean eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                return true;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return false;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newOccursCountRightFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        IBinaryOperation.validateComparativeOperation(args[0]);
                        return false;
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
                        case 1:
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
                                    return "Count";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Occurs Count Right (Historical OR Count)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks how many times the binary condition (greater, less, equal, etc]) occurs true over a range of previous keys on the right side. For example: "
                                + "[condition = left > right] => condition.left[0] > condition.right[0] || condition.left[0] > condition.right[1] || ... || condition.left[0] > condition.right[n-1]";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int occursCount = 0;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IBinaryOperation condition = IBinaryOperation.validateComparativeOperation(args[0]);
                        final int count = args[1].evaluateInteger(key);
                        final double leftResult = condition.getLeft().evaluateDouble(key);
                        int occursCount = 0;
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double rightResult = condition.getRight().evaluateDouble(curKey);
                            final boolean result = Booleans
                                    .isTrue(condition.getOp().applyBooleanNullableFromDoubles(leftResult, rightResult));
                            if (result) {
                                occursCount++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return occursCount;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newFirstIndexOfFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "lookback";
                                }

                                @Override
                                public String getName() {
                                    return "Lookback";
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
                                    return "100";
                                }
                            };

                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "First Index Of";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks at which most distant index the given condition occurs as true over a range of previous keys. "
                                + "Returns NaN when nothing was found which makes an index lookup invalid. "
                                + "This is the underlying formula: if(condition[n-1], n-1, if(condition[n-2], n-2, ... if(condition[0], 0, NaN)";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        for (int i = count - 1; i >= 0; i--) {
                            final int curKey;
                            if (count == 0) {
                                curKey = key;
                            } else {
                                curKey = previousKeyFunction.getPreviousKey(key, i);
                            }
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return i;
                            }
                        }
                        return -1;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        for (int i = count - 1; i >= 0; i--) {
                            final IFDateProvider curKey;
                            if (count == 0) {
                                curKey = key;
                            } else {
                                curKey = previousKeyFunction.getPreviousKey(key, i);
                            }
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return i;
                            }
                        }
                        return -1;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newLastIndexOfFunction(final String name) {
        return new IFunctionFactory() {
            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public AIntegerFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                if (previousKeyFunction == null) {
                    return null;
                }

                return new AIntegerFunction() {

                    @Override
                    public boolean shouldPersist() {
                        return false;
                    }

                    @Override
                    public boolean shouldDraw() {
                        return true;
                    }

                    @Override
                    public boolean isNaturalFunction(final IExpression[] args) {
                        return false;
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
                        case 1:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "lookback";
                                }

                                @Override
                                public String getName() {
                                    return "Lookback";
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
                                    return "100";
                                }
                            };
                        default:
                            throw new ArrayIndexOutOfBoundsException(index);
                        }
                    }

                    @Override
                    public int getNumberOfArguments() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return "Last Index Of";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks at which most recent index the given condition occurs as true over a range of previous keys. "
                                + "Returns NaN when nothing was found which makes an index lookup invalid. "
                                + "This is the underlying formula: if(condition[0], 0, if(condition[1], 1, ... if(condition[n-1], n-1, NaN)";
                    }

                    @Override
                    public int eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public int eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return i - 1;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return -1;
                    }

                    @Override
                    public int eval(final IFDateProvider key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        IFDateProvider curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final boolean result = Booleans.isTrue(condition.evaluateBooleanNullable(curKey));
                            if (result) {
                                return i - 1;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return -1;
                    }
                };
            }

        };
    }

}
