package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.eval.function.ADoubleNullaryFunction;
import de.invesdwin.util.math.expression.function.AFunction;

@NotThreadSafe
public class MultipleExpressionParserTest {

    @Test
    public void testVarsConstant() {
        final String expression = "var one = true; var two = one; one && two";
        final IExpression parsed = new MultipleExpressionParser(expression).parse();
        Assertions.checkEquals("var one = 1; var two = 1; 1", parsed.toString());
        final boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkTrue(evaluateBoolean);
    }

    @Test
    public void testVarsFunction() {
        final String expression = "var one = trueFunction; var two = one; one && two";
        final IExpression parsed = new MultipleExpressionParser(expression) {
            @Override
            protected AFunction getFunction(final String context, final String name) {
                if ("trueFunction".equalsIgnoreCase(name)) {
                    return new ADoubleNullaryFunction() {

                        @Override
                        public boolean isNaturalFunction(final IExpression[] args) {
                            return false;
                        }

                        @Override
                        public String getName() {
                            return getExpressionName();
                        }

                        @Override
                        public String getExpressionName() {
                            return "trueFunction";
                        }

                        @Override
                        protected double eval() {
                            return 1D;
                        }

                        @Override
                        protected IFunctionParameterInfo getParameterInfo(final int index) {
                            return null;
                        }

                        @Override
                        public String getDescription() {
                            return null;
                        }

                        @Override
                        public Object getProperty(final String property) {
                            return null;
                        }

                    };
                }
                return super.getFunction(context, name);
            }
        }.parse();
        Assertions.checkEquals("var one = trueFunction; var two = one; (one && two)", parsed.toString());
        final boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkTrue(evaluateBoolean);
    }

}
