package de.invesdwin.util.math.expression;

import de.invesdwin.util.assertions.Assertions;

public class MultipleExpressionParserTest {

    public void testVars() {
        final String expression = "var one = true; var two = false; one && two";
        final IExpression parsed = new MultipleExpressionParser(expression).parse();
        Assertions.checkEquals(expression, parsed.toString());
        parsed.newEvaluateBoolean();
    }

}
