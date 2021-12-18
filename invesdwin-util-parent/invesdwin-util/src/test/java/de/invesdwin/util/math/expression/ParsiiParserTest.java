package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.math.expression.tokenizer.ParseException;

@NotThreadSafe
public class ParsiiParserTest {
    private static final double EPSILON = 0.0000000001;

    @Test
    public void simple() {
        org.junit.jupiter.api.Assertions.assertEquals(-109d,
                parse("1 - (10 - -100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.01d,
                parse("1 / 10 * 10 / 100").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-89d, parse("1 + 10 - 100").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(91d, parse("1 - 10 - -100").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(91d, parse("1 - 10  + 100").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-109d,
                parse("1 - (10 + 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-89d,
                parse("1 + (10 - 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(100d, parse("1 / 1 * 100").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.01d,
                parse("1 / (1 * 100)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.01d, parse("1 * 1 / 100").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(7d, parse("3+4").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(7d, parse("3      +    4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-1d, parse("3+ -4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-1d, parse("3+(-4)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    private IExpression parse(final String string) {
        return new ExpressionParser(string).parse();
    }

    @Test
    public void number() {
        org.junit.jupiter.api.Assertions.assertEquals(4003.333333d,
                parse("3.333_333+4_000").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.03, parse("3e-2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(300d, parse("3e2").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(300d, parse("3e+2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(320d, parse("3.2e2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.032, parse("3.2e-2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.03, parse("3E-2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(300d, parse("3E2").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(300d, parse("3E+2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(320d, parse("3.2E2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.032, parse("3.2E-2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void precedence() {
        // term vs. product
        org.junit.jupiter.api.Assertions.assertEquals(19d, parse("3+4*4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        // product vs. power
        org.junit.jupiter.api.Assertions.assertEquals(20.25d, parse("3^4/4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        // relation vs. product
        org.junit.jupiter.api.Assertions.assertEquals(1d, parse("3 < 4*4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0d, parse("3 > 4*4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        // brackets
        org.junit.jupiter.api.Assertions.assertEquals(28d, parse("(3 + 4) * 4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(304d, parse("3e2 + 4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1200d, parse("3e2 * 4").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void signed() {
        org.junit.jupiter.api.Assertions.assertEquals(-2.02, parse("-2.02").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(2.02, parse("+2.02").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1.01, parse("+2.02 + -1.01").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(-4.03,
                parse("-2.02 - +2.01").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(3.03, parse("+2.02 + +1.01").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void blockComment() {
        org.junit.jupiter.api.Assertions.assertEquals(29, parse("27+ /*xxx*/ 2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(29, parse("27+/*xxx*/ 2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(29, parse("27/*xxx*/+2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void startingWithDecimalPoint() {
        org.junit.jupiter.api.Assertions.assertEquals(.2, parse(".2").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(.2, parse("+.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(.4, parse(".2+.2").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(.4, parse(".6+-.2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void signedParentheses() {
        org.junit.jupiter.api.Assertions.assertEquals(0.2, parse("-(-0.2)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1.2, parse("1-(-0.2)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0.8, parse("1+(-0.2)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(2.2, parse("+(2.2)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void trailingDecimalPoint() {
        org.junit.jupiter.api.Assertions.assertEquals(2., parse("2.").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void signedValueAfterOperand() {
        org.junit.jupiter.api.Assertions.assertEquals(-1.2, parse("1+-2.2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(3.2, parse("1++2.2").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(6 * -1.1, parse("6*-1.1").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(6 * 1.1, parse("6*+1.1").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void functions() {
        org.junit.jupiter.api.Assertions.assertEquals(0d,
                parse("1 + sin(-pi) + cos(pi)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(4.72038341576d,
                parse("tan(sqrt(euler ^ (pi * 3)))").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(3d, parse("| 3 - 6 |").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(3d, parse("|3-6|").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(3d,
                parse("if(3 > 2 && 2 < 3, 2+1, 1+1)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(2d,
                parse("if(3 < 2 || 2 > 3, 2+1, 1+1)").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(2d, parse("min(3,2)").newEvaluateDouble().evaluateDouble(),
                EPSILON);
    }

    @Test
    public void errors() {
        try {
            parse("test(1 2)+sin(1,2)*34-34.45.45+");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(8, e.getPosition().getIndex());
        }

        try {
            parse("1x");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("1(");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("3ee3");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(2, e.getPosition().getIndex());
        }

        try {
            parse("3e3.3");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(4, e.getPosition().getIndex());
        }

        try {
            parse("3e");
            org.junit.jupiter.api.Assertions.assertTrue(false);
        } catch (final ParseException e) {
            org.junit.jupiter.api.Assertions.assertEquals(2, e.getPosition().getIndex());
        }
    }

    @Test
    public void relationalOperators() {
        // Test for Issue with >= and <= operators (#4)
        org.junit.jupiter.api.Assertions.assertEquals(1d, parse("5 <= 5").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1d, parse("5 >= 5").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0d, parse("5 < 5").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(0d, parse("5 > 5").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

    @Test
    public void quantifiers() {
        org.junit.jupiter.api.Assertions.assertEquals(1000d, parse("1K").newEvaluateDouble().evaluateDouble(), EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1000d, parse("1M * 1m").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1d, parse("1n * 1G").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1d, parse("(1M / 1k) * 1m").newEvaluateDouble().evaluateDouble(),
                EPSILON);
        org.junit.jupiter.api.Assertions.assertEquals(1d,
                parse("1u * 10 k * 1000  m * 0.1 k").newEvaluateDouble().evaluateDouble(), EPSILON);
    }

}
