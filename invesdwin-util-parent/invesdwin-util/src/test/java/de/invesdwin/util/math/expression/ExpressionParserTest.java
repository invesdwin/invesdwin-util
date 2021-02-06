package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.ComparisonFailure;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.time.fdate.IFDateProvider;

@NotThreadSafe
public class ExpressionParserTest {

    private static final String[] ESCAPE_STRS = new String[] { "*", ".", ",", "+", "-", "^", "\\", ":", ";", "!", "§",
            "$", "%", "&", "{", "}", "?", "#", "~", "¸", "´", "|", "<", ">", "=", "€", "ß", "@", "/" };

    @Test
    public void testExponent() {
        final IExpression parsed = new ExpressionParser("3-6^2").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testXor() {
        final IExpression parsed = new ExpressionParser("true xor false").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testXorLeftNaN() {
        final IExpression parsed = new ExpressionParser("NaN xOr true").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testXorRightNaN() {
        final IExpression parsed = new ExpressionParser("true XOR NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testXorFalseFromTrue() {
        final IExpression parsed = new ExpressionParser("true xor true").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testXorFalseFromFalse() {
        final IExpression parsed = new ExpressionParser("false xor false").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testXorNanFromNan() {
        final IExpression parsed = new ExpressionParser("NaN xor NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testTwoComments() {
        final String str = "  //one comment\n" //
                + "1=1\n" //
                + "    //only trade on monday and tuesday\n" //
                + "       && 2=2\n";
        final IExpression parsed = new ExpressionParser(str).parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
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
                    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
                        return key.asFDate().addDays(-index);
                    }
                };
            }
        }.parse();
        final double evaluateDouble = parsed.newEvaluateDoubleKey().evaluateDouble(0);
        Assertions.checkEquals(1D, evaluateDouble);
    }

    @Test
    public void testPreviousKeyWithComment() {
        final IExpression parsed = new ExpressionParser("//asdf\nNaN").parse();
        final double evaluateDouble = parsed.newEvaluateDoubleKey().evaluateDouble(0);
        Assertions.checkEquals(Double.NaN, evaluateDouble);
    }

    @Test
    public void testLeadingComment() {
        final IExpression parsed = new ExpressionParser("//bla\n3-6^2").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testLeadingCommentMultiline() {
        final IExpression parsed = new ExpressionParser("/*bla\n*\n\n*/3-6^2").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(-33D, evaluateDouble);
    }

    @Test
    public void testIfParametersNumber() {
        final IExpression parsed = new ExpressionParser("if(random,1,0)") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals("if(random, 1, 0)", parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testIfParametersOperator() {
        for (final Op op : Op.values()) {
            if (op == Op.NOT || op == Op.CROSSES_ABOVE || op == Op.CROSSES_BELOW) {
                continue;
            }
            try {
                String opStr = op.toString();
                if (Characters.isAsciiAlpha(opStr.charAt(0))) {
                    opStr = " " + opStr + " ";
                }
                final IExpression parsed = new ExpressionParser("if(random" + opStr + "random,1,0)") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }
                }.parse();
                Assertions.checkEquals("if((random " + op + " random), 1, 0)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + op, t);
                }
            }
        }
    }

    @Test
    public void testIfParametersLiteral() {
        final IExpression parsed = new ExpressionParser("if(random,true,false)") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals("if(random, true, false)", parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testOfContext() {
        final IExpression parsed = new ExpressionParser("isNaN(NaN of JFOREX:EURUSD) of JFOREX:EURUSD") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals("JFOREX:EURUSD:isNaN(JFOREX:EURUSD:NaN)", parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testOfContextEscapeStart() {
        for (final String escapeStr : ESCAPE_STRS) {
            try {
                final IExpression parsed = new ExpressionParser(
                        "isNaN(NaN of STOOQ:" + escapeStr + "dax) of STOOQ:" + escapeStr + "dax") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + escapeStr + "dax:isNaN(STOOQ:" + escapeStr + "dax:NaN)",
                        parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeOnly() {
        for (final String escapeStr : ESCAPE_STRS) {
            try {
                final IExpression parsed = new ExpressionParser(
                        "isNaN(NaN of STOOQ:" + escapeStr + ") of STOOQ:" + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + escapeStr + ":isNaN(STOOQ:" + escapeStr + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeTwoOnly() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser(
                        "isNaN(NaN of STOOQ:" + escapeStr + escapeStr + ") of STOOQ:" + escapeStr + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(
                        "STOOQ:" + escapeStr + escapeStr + ":isNaN(STOOQ:" + escapeStr + escapeStr + ":NaN)",
                        parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeComment() {
        final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:da//asdf\nx) of STOOQ:dax//asdf") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected boolean isSemicolonAllowed() {
                return true;
            }
        }.parse();
        Assertions.checkEquals("STOOQ:dax:isNaN(STOOQ:dax:NaN)", parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected boolean isSemicolonAllowed() {
                return true;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testOfContextEscapeCommentInline() {
        final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:d/*asdf*/ax) of STOOQ:dax/*asdf*/") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected boolean isSemicolonAllowed() {
                return true;
            }
        }.parse();
        Assertions.checkEquals("STOOQ:dax:isNaN(STOOQ:dax:NaN)", parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected boolean isSemicolonAllowed() {
                return true;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testOfContextEscapeTwoEnd() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + "dax" + escapeStr + escapeStr
                        + ") of STOOQ:" + "dax" + escapeStr + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + "dax" + escapeStr + escapeStr + ":isNaN(STOOQ:" + "dax" + escapeStr
                        + escapeStr + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeTwoStart() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + escapeStr + escapeStr + "dax"
                        + ") of STOOQ:" + escapeStr + escapeStr + "dax") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + escapeStr + escapeStr + "dax" + ":isNaN(STOOQ:" + escapeStr
                        + escapeStr + "dax" + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeTwoMiddle() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + "dax" + escapeStr + escapeStr
                        + "dax" + ") of STOOQ:" + "dax" + escapeStr + escapeStr + "dax") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + "dax" + escapeStr + escapeStr + "dax" + ":isNaN(STOOQ:" + "dax"
                        + escapeStr + escapeStr + "dax" + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeThreeOnly() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + escapeStr + escapeStr
                        + escapeStr + ") of STOOQ:" + escapeStr + escapeStr + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + escapeStr + escapeStr + escapeStr + ":isNaN(STOOQ:" + escapeStr
                        + escapeStr + escapeStr + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeThreeEnd() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + "dax" + escapeStr + escapeStr
                        + escapeStr + ") of STOOQ:" + "dax" + escapeStr + escapeStr + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + "dax" + escapeStr + escapeStr + escapeStr + ":isNaN(STOOQ:" + "dax"
                        + escapeStr + escapeStr + escapeStr + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeThreeStart() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + escapeStr + escapeStr
                        + escapeStr + "dax" + ") of STOOQ:" + escapeStr + escapeStr + escapeStr + "dax") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + escapeStr + escapeStr + escapeStr + "dax" + ":isNaN(STOOQ:"
                        + escapeStr + escapeStr + escapeStr + "dax" + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeThreeMiddle() {
        for (final String escapeStr : ESCAPE_STRS) {
            if ("/".equals(escapeStr)) {
                //don't test comment
                continue;
            }
            try {
                final IExpression parsed = new ExpressionParser("isNaN(NaN of STOOQ:" + "dax" + escapeStr + escapeStr
                        + escapeStr + "dax" + ") of STOOQ:" + "dax" + escapeStr + escapeStr + escapeStr + "dax") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:" + "dax" + escapeStr + escapeStr + escapeStr + "dax" + ":isNaN(STOOQ:"
                        + "dax" + escapeStr + escapeStr + escapeStr + "dax" + ":NaN)", parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeMiddle() {
        for (final String escapeStr : ESCAPE_STRS) {
            try {
                final IExpression parsed = new ExpressionParser(
                        "isNaN(NaN of STOOQ:vi" + escapeStr + "c) of STOOQ:vi" + escapeStr + "c") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:vi" + escapeStr + "c:isNaN(STOOQ:vi" + escapeStr + "c:NaN)",
                        parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextEscapeEnd() {
        for (final String escapeStr : ESCAPE_STRS) {
            try {
                final IExpression parsed = new ExpressionParser(
                        "isNaN(NaN of STOOQ:vic" + escapeStr + ") of STOOQ:vic" + escapeStr) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:vic" + escapeStr + ":isNaN(STOOQ:vic" + escapeStr + ":NaN)",
                        parsed.toString());

                final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testContextEscapeEnd() {
        for (final String escapeStr : ESCAPE_STRS) {
            try {
                final IExpression parsed = new ExpressionParser(
                        "STOOQ:vic" + escapeStr + ":isNaN(STOOQ:vic" + escapeStr + ":NaN)") {
                    @Override
                    protected IParsedExpression simplify(final IParsedExpression expression) {
                        return expression;
                    }

                    @Override
                    protected boolean isSemicolonAllowed() {
                        return true;
                    }
                }.parse();
                Assertions.checkEquals("STOOQ:vic" + escapeStr + ":isNaN(STOOQ:vic" + escapeStr + ":NaN)",
                        parsed.toString());
            } catch (final Throwable t) {
                if (t instanceof ComparisonFailure) {
                    throw t;
                } else {
                    throw new RuntimeException("At: " + escapeStr, t);
                }
            }
        }
    }

    @Test
    public void testOfContextWithParams() {
        final IExpression parsed = new ExpressionParser(
                "isNaN(NaN of JFOREX:EURUSD:[asd=1|asdf=2]@TimeBarConfig[15 MINUTES| bla]) of JFOREX:EURUSD:[uhg=1|uhgj=2]@TimeBarConfig[15 MINUTES| bla]") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(
                "JFOREX:EURUSD:[uhg=1|uhgj=2]@TimeBarConfig[15 MINUTES| bla]:isNaN(JFOREX:EURUSD:[asd=1|asdf=2]@TimeBarConfig[15 MINUTES| bla]:NaN)",
                parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testContextWithParams() {
        final IExpression parsed = new ExpressionParser(
                "JFOREX:EURUSD:[uhg=1 | uhgj=2]@TimeBarConfig[15 MINUTES| bla]:isNaN(JFOREX:EURUSD:[asd=1|asdf=2]@TimeBarConfig[15 MINUTES| bla]:NaN)") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(
                "JFOREX:EURUSD:[uhg=1 | uhgj=2]@TimeBarConfig[15 MINUTES| bla]:isNaN(JFOREX:EURUSD:[asd=1|asdf=2]@TimeBarConfig[15 MINUTES| bla]:NaN)",
                parsed.toString());

        final IExpression parsedAgain = new ExpressionParser(parsed.toString()) {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(), parsedAgain.toString());
    }

    @Test
    public void testNotEqual() {
        final IExpression parsed = new ExpressionParser("1 <> 2 && 1 >< 2 && 1 != 2").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
    }

    @Test
    public void testAnd() {
        final IExpression parsed = new ExpressionParser("1 AND 0").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.FALSE, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testOr() {
        final IExpression parsed = new ExpressionParser("1 OR 0").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
    }

    @Test
    public void testAndNan() {
        final IExpression parsed = new ExpressionParser("1 AND NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
    }

    @Test
    public void testOrNan() {
        final IExpression parsed = new ExpressionParser("0 OR NaN OR 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(1D, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(Boolean.TRUE, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, evaluateBoolean);
    }

    @Test
    public void testAddNan() {
        final IExpression parsed = new ExpressionParser("1 + NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testSubtractNan() {
        final IExpression parsed = new ExpressionParser("1 - NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testMultiplyNan() {
        final IExpression parsed = new ExpressionParser("1 * NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testDivideNan() {
        final IExpression parsed = new ExpressionParser("1 / NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testModuloNan() {
        final IExpression parsed = new ExpressionParser("1 % NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testExpNan() {
        final IExpression parsed = new ExpressionParser("1 ^ NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testGreaterThanNan() {
        final IExpression parsed = new ExpressionParser("1 > NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testGreaterThanNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN > 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testGreaterThanOrEqualToNan() {
        final IExpression parsed = new ExpressionParser("1 >= NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testGreaterThanOrEqualToNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN >= 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testLessThanNan() {
        final IExpression parsed = new ExpressionParser("1 < NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testLessThanNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN < 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testLessThanOrEqualToNan() {
        final IExpression parsed = new ExpressionParser("1 <= NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testLessThanOrEqualToNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN <= 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testEqualsNan() {
        final IExpression parsed = new ExpressionParser("1 == NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testEqualsNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN == 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testNotEqualsNan() {
        final IExpression parsed = new ExpressionParser("1 != NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testNanNotEqualsNan() {
        final IExpression parsed = new ExpressionParser("NaN != NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testNanEqualsNan() {
        final IExpression parsed = new ExpressionParser("NaN = NaN").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testNotEqualsNanReverse() {
        final IExpression parsed = new ExpressionParser("NaN != 1").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(Double.NaN, evaluateDouble);

        final Boolean evaluateBooleanNullable = parsed.newEvaluateBooleanNullable().evaluateBooleanNullable();
        Assertions.checkEquals(null, evaluateBooleanNullable);

        final Boolean evaluateBoolean = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateBoolean);
    }

    @Test
    public void testNotOperator() {
        final IExpression parsed = new ExpressionParser("!!isNaN(1) || !isNaN(NaN)").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(0D, evaluateDouble);
    }

    @Test
    public void testConstantsAddition() {
        final IExpression parsed = new ExpressionParser("1+2+3+4+5+6+7+8+9+10").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(55D, evaluateDouble);
    }

    @Test
    public void testConstantsAdditionAndSubtraction() {
        final IExpression parsed = new ExpressionParser("1+2+3+4+5+6+7+8+9-10").parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(35D, evaluateDouble);
    }

    @Test
    public void testNegativeBooleanAnd() {
        final ExpressionParser expressionParser = new ExpressionParser("!(2>1 and 1<2)");
        final IExpression parsed = expressionParser.parse();
        final boolean evaluateDouble = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateDouble);
    }

    @Test
    public void testNegativeBoolean() {
        final ExpressionParser expressionParser = new ExpressionParser("!(true)");
        final IExpression parsed = expressionParser.parse();
        final boolean evaluateDouble = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(false, evaluateDouble);
    }

    @Test
    public void testNegativeVariable() {
        final ExpressionParser expressionParser = new ExpressionParser("-PI");
        final IExpression parsed = expressionParser.parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(-Math.PI, evaluateDouble);
    }

    @Test
    public void testNegativeTrue() {
        final ExpressionParser expressionParser = new ExpressionParser("-True");
        final IExpression parsed = expressionParser.parse();
        final double evaluateDouble = parsed.newEvaluateDouble().evaluateDouble();
        Assertions.checkEquals(-1D, evaluateDouble);
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

    @Test
    public void testCrossesBelow() {
        //high[1] > open[2] and high[2] > high[9] and open[6] > close[7] and CompositeRSI(2,24)[0] crosses below 50
        final IExpression parsed = new ExpressionParser(
                "nan[1] > nan[2] and nan[2] > nan[9] and nan[6] > nan[7] and isNaN(nan)[0] crosses below 50") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return new IPreviousKeyFunction() {

                    @Override
                    public int getPreviousKey(final int key, final int index) {
                        return key - index;
                    }

                    @Override
                    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
                        return key.asFDate().addDays(-index);
                    }
                };
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(),
                "((((NaN[1] > NaN[2]) && (NaN[2] > NaN[9])) && (NaN[6] > NaN[7])) && (isNaN(NaN)[0] crosses below 50))");

    }

    @Test
    public void testCrossesAbove() {
        //high[1] > open[2] and high[2] > high[9] and open[6] > close[7] and CompositeRSI(2,24)[0] crosses below 50
        final IExpression parsed = new ExpressionParser(
                "nan[1] > nan[2] and nan[2] > nan[9] and nan[6] > nan[7] and isNaN(nan)[0] crosses above 50") {
            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return expression;
            }

            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return new IPreviousKeyFunction() {

                    @Override
                    public int getPreviousKey(final int key, final int index) {
                        return key - index;
                    }

                    @Override
                    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
                        return key.asFDate().addDays(-index);
                    }
                };
            }
        }.parse();
        Assertions.checkEquals(parsed.toString(),
                "((((NaN[1] > NaN[2]) && (NaN[2] > NaN[9])) && (NaN[6] > NaN[7])) && (isNaN(NaN)[0] crosses above 50))");

    }

    @Test
    public void testIntegerNan() {
        final int result = new ExpressionParser("NaN").parse().newEvaluateInteger().evaluateInteger();
        Assertions.checkEquals(0, result);
    }

    @Test
    public void testPreviousKeyMinus1() {
        final IExpression parsed = new ExpressionParser("true[-1]") {
            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return new IPreviousKeyFunction() {

                    @Override
                    public int getPreviousKey(final int key, final int index) {
                        return key - index;
                    }

                    @Override
                    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
                        return key.asFDate().addDays(-index);
                    }
                };
            }
        }.parse();
        final boolean result = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, result);
    }

    @Test
    public void testPreviousKeyNaN() {
        final IExpression parsed = new ExpressionParser("true[NaN]") {
            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return new IPreviousKeyFunction() {

                    @Override
                    public int getPreviousKey(final int key, final int index) {
                        return key - index;
                    }

                    @Override
                    public IFDateProvider getPreviousKey(final IFDateProvider key, final int index) {
                        return key.asFDate().addDays(-index);
                    }
                };
            }
        }.parse();
        final boolean result = parsed.newEvaluateBoolean().evaluateBoolean();
        Assertions.checkEquals(true, result);
    }
}
