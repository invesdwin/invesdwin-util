package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.tokenizer.ParseException;

@NotThreadSafe
public class ExpressionParserTest {

    @Test
    public void testExponent() {
        final IExpression parsed = new ExpressionParser("3-6^2").parse();
        final double evaluateDouble = parsed.evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
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
