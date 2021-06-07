package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.eval.function.ADoubleNullaryFunction;
import de.invesdwin.util.math.expression.function.AFunction;

@NotThreadSafe
public class ExpressionParserEagerTest {

    private ADoubleNullaryFunction newNanFunctionCalled(final MutableBoolean firstNanFunctionCalled,
            final String name) {
        return new ADoubleNullaryFunction() {

            @Override
            public Object getProperty(final String property) {
                return null;
            }

            @Override
            protected IFunctionParameterInfo getParameterInfo(final int index) {
                return null;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            protected double eval() {
                firstNanFunctionCalled.setTrue();
                return Double.NaN;
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return false;
            }
        };
    }

    private ADoubleNullaryFunction newConstantFunction(final double value, final String name) {
        return new ADoubleNullaryFunction() {

            @Override
            public Object getProperty(final String property) {
                return null;
            }

            @Override
            protected IFunctionParameterInfo getParameterInfo(final int index) {
                return null;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getExpressionName() {
                return name;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            protected double eval() {
                return value;
            }

            @Override
            public boolean isNaturalFunction(final IExpression[] args) {
                return false;
            }
        };
    }

    @Test
    public void testAndNanLazyFalseTrue() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(falseFunction() && firstNanFunction()) PAND (trueFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }
        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.FALSE, evaluateBooleanNullable);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }

    @Test
    public void testAndNanLazyTrueFalse() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(trueFunction() && firstNanFunction()) PAND (falseFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }
        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.FALSE, evaluateBooleanNullable);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }

    @Test
    public void testAndNanLazyTrueTrue() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(trueFunction() && firstNanFunction()) PAND (trueFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }

        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }

    @Test
    public void testOrNanLazyFalseTrue() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(falseFunction() && firstNanFunction()) POR (trueFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }
        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }

    @Test
    public void testOrNanLazyTrueFalse() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(trueFunction() && firstNanFunction()) POR (falseFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }
        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }

    @Test
    public void testOrNanLazyTrueTrue() {
        final MutableBoolean firstNanFunctionCalled = new MutableBoolean();
        final MutableBoolean secondNanFunctionCalled = new MutableBoolean();
        final ExpressionParser parser = new ExpressionParser(
                "(trueFunction() && firstNanFunction()) POR (trueFunction() && secondNanFunction())") {
            @Override
            public AFunction getFunction(final String context, final String name) {
                if ("firstNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(firstNanFunctionCalled, name);
                } else if ("secondNanFunction".equalsIgnoreCase(name)) {
                    return newNanFunctionCalled(secondNanFunctionCalled, name);
                } else if ("trueFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(1D, name);
                } else if ("falseFunction".equalsIgnoreCase(name)) {
                    return newConstantFunction(0D, name);
                } else {
                    return super.getFunction(context, name);
                }
            }

        };
        final IExpression parsed = parser.parse();
        Assertions.checkFalse(firstNanFunctionCalled.booleanValue());
        Assertions.checkFalse(secondNanFunctionCalled.booleanValue());
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
        Assertions.checkTrue(firstNanFunctionCalled.booleanValue());
        Assertions.checkTrue(secondNanFunctionCalled.booleanValue());
        firstNanFunctionCalled.setFalse();
        secondNanFunctionCalled.setFalse();
    }
}
