package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public final class IndexOfFunctions {

    private IndexOfFunctions() {
        //TODO: duplicate these functions as indicators to make them faster by using a trailing previousValuesCache
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
                    public IEvaluateInteger newEvaluateInteger(final String context, final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final String context, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        final IEvaluateBooleanKey conditionF = condition.newEvaluateBooleanKey();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            for (int i = count - 1; i >= 0; i--) {
                                final int curKey;
                                if (count == 0) {
                                    curKey = key;
                                } else {
                                    curKey = previousKeyFunction.getPreviousKey(key, i);
                                }
                                final boolean result = conditionF.evaluateBoolean(curKey);
                                if (result) {
                                    return i;
                                }
                            }
                            return -1;
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context,
                            final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        final IEvaluateBooleanFDate conditionF = condition.newEvaluateBooleanFDate();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            for (int i = count - 1; i >= 0; i--) {
                                final IFDateProvider curKey;
                                if (count == 0) {
                                    curKey = key;
                                } else {
                                    curKey = previousKeyFunction.getPreviousKey(key, i);
                                }
                                final boolean result = conditionF.evaluateBoolean(curKey);
                                if (result) {
                                    return i;
                                }
                            }
                            return -1;
                        };
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
                                + "Returns -1 when nothing was found which makes an index lookup invalid and returns NaN overall. "
                                + "This is the underlying formula: if(condition[0], 0, if(condition[1], 1, ... if(condition[n-1], n-1, -1). "
                                + "If the lookback has been exeeded, -1 is returned as the index.";
                    }

                    @Override
                    public IEvaluateInteger newEvaluateInteger(final String context, final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final String context, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerKey countF = args[1].newEvaluateIntegerKey();
                        final IEvaluateBooleanKey conditionF = condition.newEvaluateBooleanKey();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            int curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final boolean result = conditionF.evaluateBoolean(curKey);
                                if (result) {
                                    return i - 1;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return -1;
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context,
                            final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerFDate countF = args[1].newEvaluateIntegerFDate();
                        final IEvaluateBooleanFDate conditionF = condition.newEvaluateBooleanFDate();
                        return key -> {
                            final int count = countF.evaluateInteger(key);
                            IFDateProvider curKey = key;
                            for (int i = 1; i <= count; i++) {
                                final boolean result = conditionF.evaluateBoolean(curKey);
                                if (result) {
                                    return i - 1;
                                }
                                if (i != count) {
                                    curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                }
                            }
                            return -1;
                        };
                    }
                };
            }

        };
    }

    public static IFunctionFactory newPreviousIndexOfFunction(final String name) {
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
                                    return "steps";
                                }

                                @Override
                                public String getName() {
                                    return "Steps";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many steps should be checked?";
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
                                    return "0";
                                }
                            };
                        case 2:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "lookbackPerStep";
                                }

                                @Override
                                public String getName() {
                                    return "Lookback Per Step";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked per step?";
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
                        return 3;
                    }

                    @Override
                    public String getName() {
                        return "Previous Index Of";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks at which most recent index the given condition occurs as true over a range of previous keys and steps. "
                                + "Returns -1 when nothing was found in a given step which makes an index lookup invalid and returns NaN overall. "
                                + "This is the underlying formula which is repeated per step: if(condition[0], 0, if(condition[1], 1, ... if(condition[n-1], n-1, -1). "
                                + "If the lookback is exceeded for a step, -1 is returned as the index.";
                    }

                    @Override
                    public IEvaluateInteger newEvaluateInteger(final String context, final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final String context, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerKey stepF = args[1].newEvaluateIntegerKey();
                        final IEvaluateIntegerKey countF = args[2].newEvaluateIntegerKey();
                        final IEvaluateBooleanKey conditionF = condition.newEvaluateBooleanKey();

                        final IEvaluateIntegerKey lastIndexOf = newLastIndexOfFunction("lastIndexOf")
                                .newFunction(previousKeyFunction)
                                .newCall(context,
                                        new IParsedExpression[] { (IParsedExpression) args[0],
                                                (IParsedExpression) args[2] })
                                .newEvaluateIntegerKey();

                        return key -> {
                            final int steps = stepF.evaluateInteger(key);
                            if (steps < 0) {
                                return -1;
                            } else if (steps == 0) {
                                return lastIndexOf.evaluateInteger(key);
                            } else {
                                final int count = countF.evaluateInteger(key);
                                int curKey = key;
                                int lastStepIndexOf = 0;
                                STEP: for (int step = 0; step < steps; step++) {
                                    for (int i = 1; i <= count; i++) {
                                        final boolean result = conditionF.evaluateBoolean(curKey);
                                        if (i != count) {
                                            curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                        }
                                        if (result) {
                                            //continue with next step
                                            lastStepIndexOf += i - 1;
                                            continue STEP;
                                        }
                                    }
                                    //nothing found in this step
                                    return -1;
                                }
                                return lastStepIndexOf;
                            }
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context,
                            final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerFDate stepF = args[1].newEvaluateIntegerFDate();
                        final IEvaluateIntegerFDate countF = args[2].newEvaluateIntegerFDate();
                        final IEvaluateBooleanFDate conditionF = condition.newEvaluateBooleanFDate();

                        final IEvaluateIntegerFDate lastIndexOf = newLastIndexOfFunction("lastIndexOf")
                                .newFunction(previousKeyFunction)
                                .newCall(context,
                                        new IParsedExpression[] { (IParsedExpression) args[0],
                                                (IParsedExpression) args[2] })
                                .newEvaluateIntegerFDate();
                        return key -> {
                            final int steps = stepF.evaluateInteger(key);
                            if (steps < 0) {
                                return -1;
                            } else if (steps == 0) {
                                return lastIndexOf.evaluateInteger(key);
                            } else {
                                final int count = countF.evaluateInteger(key);
                                IFDateProvider curKey = key;
                                int lastStepIndexOf = 0;
                                STEP: for (int step = 0; step < steps; step++) {
                                    for (int i = 1; i <= count; i++) {
                                        final boolean result = conditionF.evaluateBoolean(curKey);
                                        if (i != count) {
                                            curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                        }
                                        if (result) {
                                            //continue with next step
                                            lastStepIndexOf += i - 1;
                                            continue STEP;
                                        }
                                    }
                                    //nothing found in this step
                                    return -1;
                                }
                                return lastStepIndexOf;
                            }
                        };
                    }
                };
            }

        };
    }

    public static IFunctionFactory newLatestIndexOfFunction(final String name) {
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
                                    return "steps";
                                }

                                @Override
                                public String getName() {
                                    return "Steps";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many steps should be checked?";
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
                                    return "0";
                                }
                            };
                        case 2:
                            return new IFunctionParameterInfo() {

                                @Override
                                public String getType() {
                                    return ExpressionReturnType.Integer.toString();
                                }

                                @Override
                                public String getExpressionName() {
                                    return "lookbackPerStep";
                                }

                                @Override
                                public String getName() {
                                    return "Lookback Per Step";
                                }

                                @Override
                                public String getDescription() {
                                    return "How many previous keys/periods/bars should be checked per step?";
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
                        return 3;
                    }

                    @Override
                    public String getName() {
                        return "Latest Index Of";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Checks at which most recent index the given condition occurs as true over a range of previous keys and steps. "
                                + "Returns -1 when nothing was found in all steps which makes an index lookup invalid and returns NaN overall. "
                                + "This is the underlying formula which is repeated per step: if(condition[0], 0, if(condition[1], 1, ... if(condition[n-1], n-1, -1). "
                                + "If the lookback is exceeded for a step, the index of the latest step where the condition was true is returned.";
                    }

                    @Override
                    public IEvaluateInteger newEvaluateInteger(final String context, final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public IEvaluateIntegerKey newEvaluateIntegerKey(final String context, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerKey stepF = args[1].newEvaluateIntegerKey();
                        final IEvaluateIntegerKey countF = args[2].newEvaluateIntegerKey();
                        final IEvaluateBooleanKey conditionF = condition.newEvaluateBooleanKey();

                        final IEvaluateIntegerKey lastIndexOf = newLastIndexOfFunction("lastIndexOf")
                                .newFunction(previousKeyFunction)
                                .newCall(context,
                                        new IParsedExpression[] { (IParsedExpression) args[0],
                                                (IParsedExpression) args[2] })
                                .newEvaluateIntegerKey();

                        return key -> {
                            final int steps = stepF.evaluateInteger(key);
                            if (steps < 0) {
                                return -1;
                            } else if (steps == 0) {
                                return lastIndexOf.evaluateInteger(key);
                            } else {
                                final int count = countF.evaluateInteger(key);
                                int curKey = key;
                                int lastStepIndexOf = 0;
                                boolean found = false;
                                STEP: for (int step = 0; step < steps; step++) {
                                    for (int i = 1; i <= count; i++) {
                                        final boolean result = conditionF.evaluateBoolean(curKey);
                                        if (i != count) {
                                            curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                        }
                                        if (result) {
                                            //continue with next step
                                            lastStepIndexOf += i - 1;
                                            found = true;
                                            continue STEP;
                                        }
                                    }
                                    //nothing found in this step
                                    if (found) {
                                        return lastStepIndexOf;
                                    } else {
                                        return -1;
                                    }
                                }
                                if (found) {
                                    return lastStepIndexOf;
                                } else {
                                    return -1;
                                }
                            }
                        };
                    }

                    @Override
                    public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context,
                            final IExpression[] args) {
                        final IExpression condition = args[0];
                        final IEvaluateIntegerFDate stepF = args[1].newEvaluateIntegerFDate();
                        final IEvaluateIntegerFDate countF = args[2].newEvaluateIntegerFDate();
                        final IEvaluateBooleanFDate conditionF = condition.newEvaluateBooleanFDate();

                        final IEvaluateIntegerFDate lastIndexOf = newLastIndexOfFunction("lastIndexOf")
                                .newFunction(previousKeyFunction)
                                .newCall(context,
                                        new IParsedExpression[] { (IParsedExpression) args[0],
                                                (IParsedExpression) args[2] })
                                .newEvaluateIntegerFDate();
                        return key -> {
                            final int steps = stepF.evaluateInteger(key);
                            if (steps < 0) {
                                return -1;
                            } else if (steps == 0) {
                                return lastIndexOf.evaluateInteger(key);
                            } else {
                                final int count = countF.evaluateInteger(key);
                                IFDateProvider curKey = key;
                                int lastStepIndexOf = 0;
                                boolean found = false;
                                STEP: for (int step = 0; step < steps; step++) {
                                    for (int i = 1; i <= count; i++) {
                                        final boolean result = conditionF.evaluateBoolean(curKey);
                                        if (i != count) {
                                            curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                                        }
                                        if (result) {
                                            //continue with next step
                                            lastStepIndexOf += i - 1;
                                            found = true;
                                            continue STEP;
                                        }
                                    }
                                    //nothing found in this step
                                    if (found) {
                                        return lastStepIndexOf;
                                    } else {
                                        return -1;
                                    }
                                }
                                if (found) {
                                    return lastStepIndexOf;
                                } else {
                                    return -1;
                                }
                            }
                        };
                    }
                };
            }

        };
    }

}
