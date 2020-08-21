package de.invesdwin.util.math.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.math.expression.tokenizer.ParseException;

@NotThreadSafe
public class ParsiiParserTest {
    private static final double EPSILON = 0.0000000001;

    @Test
    public void simple() {
        assertEquals(-109d, parse("1 - (10 - -100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.01d, parse("1 / 10 * 10 / 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-89d, parse("1 + 10 - 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(91d, parse("1 - 10 - -100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(91d, parse("1 - 10  + 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-109d, parse("1 - (10 + 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-89d, parse("1 + (10 - 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(100d, parse("1 / 1 * 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.01d, parse("1 / (1 * 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.01d, parse("1 * 1 / 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(7d, parse("3+4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(7d, parse("3      +    4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-1d, parse("3+ -4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-1d, parse("3+(-4)").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    private IExpression parse(final String string) {
        return new ExpressionParser(string).parse();
    }

    @Test
    public void number() {
        assertEquals(4003.333333d, parse("3.333_333+4_000").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.03, parse("3e-2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(300d, parse("3e2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(300d, parse("3e+2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(320d, parse("3.2e2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.032, parse("3.2e-2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.03, parse("3E-2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(300d, parse("3E2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(300d, parse("3E+2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(320d, parse("3.2E2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.032, parse("3.2E-2").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void precedence() {
        // term vs. product
        assertEquals(19d, parse("3+4*4").newEvaluateDouble().evaluateDouble(), EPSILON);
        // product vs. power
        assertEquals(20.25d, parse("3^4/4").newEvaluateDouble().evaluateDouble(), EPSILON);
        // relation vs. product
        assertEquals(1d, parse("3 < 4*4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0d, parse("3 > 4*4").newEvaluateDouble().evaluateDouble(), EPSILON);
        // brackets
        assertEquals(28d, parse("(3 + 4) * 4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(304d, parse("3e2 + 4").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1200d, parse("3e2 * 4").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void signed() {
        assertEquals(-2.02, parse("-2.02").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(2.02, parse("+2.02").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1.01, parse("+2.02 + -1.01").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(-4.03, parse("-2.02 - +2.01").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(3.03, parse("+2.02 + +1.01").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void blockComment() {
        assertEquals(29, parse("27+ /*xxx*/ 2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(29, parse("27+/*xxx*/ 2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(29, parse("27/*xxx*/+2").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void startingWithDecimalPoint() {
        assertEquals(.2, parse(".2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(.2, parse("+.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(.4, parse(".2+.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(.4, parse(".6+-.2").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void signedParentheses() {
        assertEquals(0.2, parse("-(-0.2)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1.2, parse("1-(-0.2)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0.8, parse("1+(-0.2)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(2.2, parse("+(2.2)").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void trailingDecimalPoint() {
        assertEquals(2., parse("2.").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void signedValueAfterOperand() {
        assertEquals(-1.2, parse("1+-2.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(3.2, parse("1++2.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(6 * -1.1, parse("6*-1.1").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(6 * 1.1, parse("6*+1.1").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void functions() {
        assertEquals(0d, parse("1 + sin(-pi) + cos(pi)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(4.72038341576d, parse("tan(sqrt(euler ^ (pi * 3)))").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        assertEquals(3d, parse("| 3 - 6 |").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(3d, parse("|3-6|").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(3d, parse("if(3 > 2 && 2 < 3, 2+1, 1+1)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(2d, parse("if(3 < 2 || 2 > 3, 2+1, 1+1)").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(2d, parse("min(3,2)").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void errors() {
        try {
            parse("test(1 2)+sin(1,2)*34-34.45.45+");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(8, e.getPosition().getIndex());
        }

        try {
            parse("1x");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("1(");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("3ee3");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("3e3.3");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(4, e.getPosition().getIndex());
        }

        try {
            parse("3e");
            assertTrue(false);
        } catch (final ParseException e) {
            assertEquals(2, e.getPosition().getIndex());
        }
    }

    @Test
    public void relationalOperators() {
        // Test for Issue with >= and <= operators (#4)
        assertEquals(1d, parse("5 <= 5").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1d, parse("5 >= 5").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0d, parse("5 < 5").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(0d, parse("5 > 5").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void quantifiers() {
        assertEquals(1000d, parse("1K").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1000d, parse("1M * 1m").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1d, parse("1n * 1G").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1d, parse("(1M / 1k) * 1m").newEvaluateDouble().evaluateDouble(), EPSILON);
        assertEquals(1d, parse("1u * 10 k * 1000  m * 0.1 k").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

}
