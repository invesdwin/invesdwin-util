package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.math.statistics.RunningMedian;
import de.invesdwin.util.math.stream.doubl.DoubleStreamStandardDeviation;
import de.invesdwin.util.math.stream.doubl.DoubleStreamVariance;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class StatisticalFunctions {

    private StatisticalFunctions() {
    }

    public static IFunctionFactory newCountFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Count not NaN/Null (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Counts previous values that are not NaN/Null (the count should normally equal to the lookback value in series without missing values): isNaN(value[0]) + isNaN(value[1]) + ... + isNaN(value[n-1])";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double countNotNan = 0D;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                countNotNan++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return countNotNan;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double countNotNan = 0D;
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                countNotNan++;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return countNotNan;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newSumFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Sum (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Adds together the previous values: value[0] + value[1] + ... + value[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double sum = 0D;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                sum += result;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return sum;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double sum = 0D;
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                sum += result;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return sum;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newProductFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Product (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Multiplies together the previous values: value[0] * value[1] * ... * value[n-1]";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double sum = 0D;
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                sum *= result;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return sum;
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        double sum = 0D;
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                sum *= result;
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return sum;
                    }
                };
            }

        };
    }

    public static IFunctionFactory newVarianceFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Variance (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the variance of the previous values."
                                + "Normally the sample standard deviaition is to be preferred, since in real life there are normally no complete populations to be measured.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamVariance variance = new DoubleStreamVariance();
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                variance.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return variance.getVariance();
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamVariance variance = new DoubleStreamVariance();
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                variance.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return variance.getVariance();
                    }
                };
            }

        };
    }

    public static IFunctionFactory newSampleVarianceFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Sample Variance (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the sample variance of the previous values.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamVariance variance = new DoubleStreamVariance();
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                variance.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return variance.getSampleVariance();
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamVariance variance = new DoubleStreamVariance();
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                variance.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return variance.getSampleVariance();
                    }
                };
            }

        };
    }

    public static IFunctionFactory newStandardDeviationFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Standard Deviation (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the standard deviation of the previous values. "
                                + "Normally the sample standard deviaition is to be preferred, since in real life there are normally no complete populations to be measured.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamStandardDeviation standardDeviation = new DoubleStreamStandardDeviation();
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                standardDeviation.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return standardDeviation.getStandardDeviation();
                    }

                    @SuppressWarnings("deprecation")
                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamStandardDeviation standardDeviation = new DoubleStreamStandardDeviation();
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                standardDeviation.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return standardDeviation.getStandardDeviation();
                    }
                };
            }

        };
    }

    public static IFunctionFactory newSampleStandardDeviationFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Sample Standard Deviation (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the sample standard deviation of the previous values.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamStandardDeviation standardDeviation = new DoubleStreamStandardDeviation();
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                standardDeviation.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return standardDeviation.getSampleStandardDeviation();
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final DoubleStreamStandardDeviation standardDeviation = new DoubleStreamStandardDeviation();
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                standardDeviation.process(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return standardDeviation.getSampleStandardDeviation();
                    }
                };
            }

        };
    }

    public static IFunctionFactory newMedianFunction(final String name) {
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
                        return true;
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return "Median (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the median of the previous values.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final RunningMedian median = new RunningMedian(count);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                median.add(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return median.getMedian();
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final IExpression condition = args[0];
                        final int count = args[1].evaluateInteger(key);
                        final RunningMedian median = new RunningMedian(count);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                median.add(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return median.getMedian();
                    }
                };
            }

        };
    }

    //CHECKSTYLE:OFF
    public static IFunctionFactory newPercentileFunction(final String name) {
        //CHECKSTYLE:ON
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
                        return true;
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
                                    return "percentile";
                                }

                                @Override
                                public String getName() {
                                    return "Percentile";
                                }

                                @Override
                                public String getDescription() {
                                    return "0.25 for the lower quartile, 0.5 for the median, 0.75 for the upper quartile";
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
                                    return "value";
                                }

                                @Override
                                public String getName() {
                                    return "Value";
                                }

                                @Override
                                public String getDescription() {
                                    return "The numeric expression to evaluate.";
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
                        return 3;
                    }

                    @Override
                    public String getName() {
                        return "Percentile (Historical)";
                    }

                    @Override
                    public String getExpressionName() {
                        return name;
                    }

                    @Override
                    public String getDescription() {
                        return "Calculates the percentile of the previous values.";
                    }

                    @Override
                    public double eval(final IExpression[] args) {
                        throw new UnsupportedOperationException("use time or int key instead");
                    }

                    @Override
                    public double eval(final int key, final IExpression[] args) {
                        final double percentile = args[0].evaluateDouble(key);
                        final IExpression condition = args[1];
                        final int count = args[2].evaluateInteger(key);
                        final RunningMedian median = new RunningMedian(count);
                        int curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                median.add(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return median.getPercentile(percentile);
                    }

                    @Override
                    public double eval(final FDate key, final IExpression[] args) {
                        final double percentile = args[0].evaluateDouble(key);
                        final IExpression condition = args[1];
                        final int count = args[2].evaluateInteger(key);
                        final RunningMedian median = new RunningMedian(count);
                        FDate curKey = key;
                        for (int i = 1; i <= count; i++) {
                            final double result = condition.evaluateDouble(curKey);
                            if (!Doubles.isNaN(result)) {
                                median.add(result);
                            }
                            if (i != count) {
                                curKey = previousKeyFunction.getPreviousKey(curKey, 1);
                            }
                        }
                        return median.getPercentile(percentile);
                    }
                };
            }

        };
    }

}
