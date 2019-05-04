package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.tokenizer.ParseException;

@NotThreadSafe
public class ExpressionParserTest {

    @Test
    public void testExponent() {
        final IExpression parsed = new ExpressionParser("3-6^2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testContextFunctionSuffix() {
        final IExpression parsed = new ExpressionParser("isNAN(NAN)JFOREX:EURUSD:[ASDF=1,UHgj=2] and 1") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), "(JFOREX:EURUSD:[ASDF=1,UHgj=2]:isNaN(NaN) && 1)");
    }

    @Test
    public void testOfContext() {
        final IExpression parsed = new ExpressionParser("isNaN(NaN of JFOREX:EURUSD) of JFOREX:EURUSD") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), "JFOREX:EURUSD:isNaN(JFOREX:EURUSD:NaN)");
    }

    @Test
    public void testOfContextWithParams() {
        final IExpression parsed = new ExpressionParser(
                "isNaN(NaN of JFOREX:EURUSD:[asd=1,asdf=2]) of JFOREX:EURUSD:[uhg=1,uhgj=2]") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(),
                "JFOREX:EURUSD:[uhg=1,uhgj=2]:isNaN(JFOREX:EURUSD:[asd=1,asdf=2]:NaN)");
    }

    @Test
    public void testNotEqual() {
        final IExpression parsed = new ExpressionParser("1 <> 2 && 1 >< 2 && 1 != 2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testAnd() {
        final IExpression parsed = new ExpressionParser("1 AND 0").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testOr() {
        final IExpression parsed = new ExpressionParser("1 OR 0").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testNotOperator() {
        final IExpression parsed = new ExpressionParser("!!isNaN(1) || !isNaN(NaN)").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testConstantsAddition() {
        final IExpression parsed = new ExpressionParser("1+2+3+4+5+6+7+8+9+10").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(55D, evaluateDouble);
    }

    @Test
    public void testConstantsAdditionAndSubtraction() {
        final IExpression parsed = new ExpressionParser("1+2+3+4+5+6+7+8+9-10").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(35D, evaluateDouble);
    }

    @Test(expected = ParseException.class)
    public void testNull() {
        new ExpressionParser("").parse();
    }

    @Test(expected = ParseException.class)
    public void testEmpty() {
        new ExpressionParser("").parse();
    }

    @Test(expected = ParseException.class)
    public void testBlank() {
        new ExpressionParser(" ").parse();
    }
}
