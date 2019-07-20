package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class ExpressionParserTest {

    @Test
    public void testExponent() {
        final IExpression parsed = new ExpressionParser("3-6^2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testTwoComments() {
        final String str = "  //one comment\n" //
                + "1=1\n" //
                + "    //only trade on monday and tuesday\n" //
                + "       && 2=2\n";
        final IExpression parsed = new ExpressionParser(str).parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testPreviousKey() {
        final IExpression parsed = new ExpressionParser("isNaN(NaN[2])[5]") {
            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return new IPreviousKeyFunction() {

                    @Override
                    public int getPreviousKey(final int key, final int index) {
                        return key - index;
                    }

                    @Override
                    public FDate getPreviousKey(final FDate key, final int index) {
                        return key.addDays(-index);
                    }
                };
            }
        }.parse();
        final double evaluateDouble = parsed.evaluateDouble(0);
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testPreviousKeyWithComment() {
        final IExpression parsed = new ExpressionParser("//asdf\nNaN").parse();
        final double evaluateDouble = parsed.evaluateDouble(0);
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testLeadingComment() {
        final IExpression parsed = new ExpressionParser("//bla\n3-6^2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testLeadingCommentMultiline() {
        final IExpression parsed = new ExpressionParser("/*bla\n*\n\n*/3-6^2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
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
                "isNaN(NaN of JFOREX:EURUSD:[asd=1,asdf=2]@TimeBarConfig[15 MINUTES, bla]) of JFOREX:EURUSD:[uhg=1,uhgj=2]@TimeBarConfig[15 MINUTES, bla]") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(),
                "JFOREX:EURUSD:[uhg=1,uhgj=2]@TimeBarConfig[15 MINUTES, bla]:isNaN(JFOREX:EURUSD:[asd=1,asdf=2]@TimeBarConfig[15 MINUTES, bla]:NaN)");
    }

    @Test
    public void testContextWithParams() {
        final IExpression parsed = new ExpressionParser(
                "JFOREX:EURUSD:[uhg=1,uhgj=2]@TimeBarConfig[15 MINUTES, bla]:isNaN(JFOREX:EURUSD:[asd=1,asdf=2]@TimeBarConfig[15 MINUTES, bla]:NaN)") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(),
                "JFOREX:EURUSD:[uhg=1,uhgj=2]@TimeBarConfig[15 MINUTES, bla]:isNaN(JFOREX:EURUSD:[asd=1,asdf=2]@TimeBarConfig[15 MINUTES, bla]:NaN)");
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
    public void testAndNan() {
        final IExpression parsed = new ExpressionParser("1 AND NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testOrNan() {
        final IExpression parsed = new ExpressionParser("0 OR NaN OR 1").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testAddNan() {
        final IExpression parsed = new ExpressionParser("1 + NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testSubtractNan() {
        final IExpression parsed = new ExpressionParser("1 - NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testMultiplyNan() {
        final IExpression parsed = new ExpressionParser("1 * NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testDivideNan() {
        final IExpression parsed = new ExpressionParser("1 / NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testModuloNan() {
        final IExpression parsed = new ExpressionParser("1 % NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testExpNan() {
        final IExpression parsed = new ExpressionParser("1 ^ NaN").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
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
