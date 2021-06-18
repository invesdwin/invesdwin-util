package de.invesdwin.util.math.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.math.expression.eval.BooleanConstantExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.DynamicPreviousKeyExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.function.DoubleFunctionCall;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableAndOperation;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableExclusiveOrOperation;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableNotOperation;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableOrOperation;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableParallelAndOperation;
import de.invesdwin.util.math.expression.eval.operation.BooleanNullableParallelOrOperation;
import de.invesdwin.util.math.expression.eval.operation.DoubleBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.DoubleCrossesAboveOperation;
import de.invesdwin.util.math.expression.eval.operation.DoubleCrossesBelowOperation;
import de.invesdwin.util.math.expression.eval.operation.IBinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.function.HistoricalFunctions;
import de.invesdwin.util.math.expression.function.IFunctionFactory;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.function.IndexOfFunctions;
import de.invesdwin.util.math.expression.function.LogicalFunctions;
import de.invesdwin.util.math.expression.function.MathFunctions;
import de.invesdwin.util.math.expression.function.StatisticalFunctions;
import de.invesdwin.util.math.expression.tokenizer.ExpressionContextUtil;
import de.invesdwin.util.math.expression.tokenizer.IPosition;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.math.expression.tokenizer.Token;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;
import de.invesdwin.util.math.expression.variable.IVariable;
import de.invesdwin.util.math.expression.variable.Variables;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public class ExpressionParser implements IExpressionParser {

    public static final Op DEFAULT_COMMA_OP = Op.AND;

    private static final FastThreadLocal<Tokenizer> TOKENIZER = new FastThreadLocal<Tokenizer>() {
        @Override
        protected Tokenizer initialValue() throws Exception {
            return new Tokenizer();
        }
    };

    private static final Map<String, IFunctionFactory> DEFAULT_FUNCTIONS;
    private static final Map<String, IVariable> DEFAULT_VARIABLES;

    private final String originalExpression;
    private Tokenizer tokenizer;

    //CHECKSTYLE:OFF
    static {
        //CHECKSTYLE:ON
        DEFAULT_FUNCTIONS = new LinkedHashMap<>();

        putDefaultFunction(MathFunctions.SIN);
        putDefaultFunction(MathFunctions.COS);
        putDefaultFunction(MathFunctions.TAN);
        putDefaultFunction(MathFunctions.SINH);
        putDefaultFunction(MathFunctions.COSH);
        putDefaultFunction(MathFunctions.TANH);
        putDefaultFunction(MathFunctions.ASIN);
        putDefaultFunction(MathFunctions.ACOS);
        putDefaultFunction(MathFunctions.ATAN);
        putDefaultFunction(MathFunctions.ATAN2);
        putDefaultFunction(MathFunctions.DEG);
        putDefaultFunction(MathFunctions.RAD);
        putDefaultFunction(MathFunctions.ABS);
        putDefaultFunction(MathFunctions.ROUND);
        putDefaultFunction(MathFunctions.CEIL);
        putDefaultFunction(MathFunctions.FLOOR);
        putDefaultFunction(MathFunctions.EXP);
        putDefaultFunction(MathFunctions.LN);
        putDefaultFunction(MathFunctions.LOG);
        putDefaultFunction(MathFunctions.SQRT);
        putDefaultFunction(MathFunctions.SQUARE);
        putDefaultFunction(MathFunctions.POW);
        for (final String name : new String[] { "min", "minimum" }) {
            putDefaultFunction(MathFunctions.newMinimumFunction(name));
        }
        for (final String name : new String[] { "max", "maximum" }) {
            putDefaultFunction(MathFunctions.newMaximumFunction(name));
        }
        for (final String name : new String[] { "between", "clamp" }) {
            putDefaultFunction(MathFunctions.newBetweenFunction(name));
        }
        for (final String name : new String[] { "random", "rnd", "rng" }) {
            putDefaultFunction(MathFunctions.newRandomFunction(name));
        }
        putDefaultFunction(MathFunctions.NORMALIZE_VALUE);
        putDefaultFunction(MathFunctions.SIGN);
        putDefaultFunction(MathFunctions.NEGATE);

        putDefaultFunction(LogicalFunctions.IF);

        for (final String name : new String[] { "map", "select", "array", "decide" }) {
            putDefaultFunction(LogicalFunctions.newMapFunction(name));
        }
        for (final String name : new String[] { "vote", "ensemble", "threshold", "majority", "fuzzy" }) {
            putDefaultFunction(LogicalFunctions.newVoteFunction(name));
        }

        putDefaultFunction(LogicalFunctions.ISNAN);
        putDefaultFunction(LogicalFunctions.ISTRUE);
        putDefaultFunction(LogicalFunctions.ISFALSE);
        putDefaultFunction(LogicalFunctions.NOT);

        for (final String name : new String[] { "once", "onceOnly", "onChange", "onChangeOnly", "onChangeOnlyOnce",
                "changeOnly", "changed", "change", "single", "singleOnly" }) {
            putDefaultFunction(HistoricalFunctions.newOnceFunction(name));
        }
        for (final String name : new String[] { "stable", "repeat", "repeatAnd", "loop", "loopAnd", "hist", "histAnd",
                "historical", "historicalAnd" }) {
            putDefaultFunction(HistoricalFunctions.newStableFunction(name));
            putDefaultFunction(HistoricalFunctions.newStableFunction(name + "Both"));
            putDefaultFunction(HistoricalFunctions.newStableLeftFunction(name + "Left"));
            putDefaultFunction(HistoricalFunctions.newStableRightFunction(name + "Right"));

            putDefaultFunction(HistoricalFunctions.newStableCountFunction(name + "Count"));
            putDefaultFunction(HistoricalFunctions.newStableCountFunction(name + "Count" + "Both"));
            putDefaultFunction(HistoricalFunctions.newStableCountLeftFunction(name + "Count" + "Left"));
            putDefaultFunction(HistoricalFunctions.newStableCountRightFunction(name + "Count" + "Right"));
        }
        for (final String name : new String[] { "occurs", "repeatOr", "loopOr", "histOr", "historicalOr" }) {
            putDefaultFunction(HistoricalFunctions.newOccursFunction(name));
            putDefaultFunction(HistoricalFunctions.newOccursFunction(name + "Both"));
            putDefaultFunction(HistoricalFunctions.newOccursLeftFunction(name + "Left"));
            putDefaultFunction(HistoricalFunctions.newOccursRightFunction(name + "Right"));

            putDefaultFunction(HistoricalFunctions.newOccursCountFunction(name + "Count"));
            putDefaultFunction(HistoricalFunctions.newOccursCountFunction(name + "Count" + "Both"));
            putDefaultFunction(HistoricalFunctions.newOccursCountLeftFunction(name + "Count" + "Left"));
            putDefaultFunction(HistoricalFunctions.newOccursCountRightFunction(name + "Count" + "Right"));
        }
        putDefaultFunction(IndexOfFunctions.newFirstIndexOfFunction("firstIndexOf"));
        for (final String name : new String[] { "indexOf", "lastIndexOf" }) {
            putDefaultFunction(IndexOfFunctions.newLastIndexOfFunction(name));
        }
        for (final String name : new String[] { "previousIndexOf", "prevIndexOf" }) {
            putDefaultFunction(IndexOfFunctions.newPreviousIndexOfFunction(name));
        }
        for (final String name : new String[] { "latestIndexOf" }) {
            putDefaultFunction(IndexOfFunctions.newLatestIndexOfFunction(name));
        }

        for (final String name : new String[] { "count", "countNotNaN", "countNotNull", "countExists" }) {
            putDefaultFunction(StatisticalFunctions.newCountFunction(name));
        }
        for (final String name : new String[] { "median", "runningMedian" }) {
            putDefaultFunction(StatisticalFunctions.newMedianFunction(name));
        }
        for (final String name : new String[] { "percentile", "quantile", "quartile" }) {
            putDefaultFunction(StatisticalFunctions.newPercentileFunction(name));
        }
        for (final String name : new String[] { "product", "runningProduct" }) {
            putDefaultFunction(StatisticalFunctions.newProductFunction(name));
        }
        for (final String name : new String[] { "sum", "runningSum" }) {
            putDefaultFunction(StatisticalFunctions.newSumFunction(name));
        }
        for (final String name : new String[] { "variance", "var" }) {
            putDefaultFunction(StatisticalFunctions.newVarianceFunction(name));
        }
        for (final String name : new String[] { "sampleVariance", "sampleVar" }) {
            putDefaultFunction(StatisticalFunctions.newSampleVarianceFunction(name));
        }
        for (final String name : new String[] { "standardDeviation", "stddev" }) {
            putDefaultFunction(StatisticalFunctions.newStandardDeviationFunction(name));
        }
        for (final String name : new String[] { "sampleStandardDeviation", "sampleStddev" }) {
            putDefaultFunction(StatisticalFunctions.newSampleStandardDeviationFunction(name));
        }

        DEFAULT_VARIABLES = new LinkedHashMap<>();

        putDefaultVariable(Variables.PI);
        putDefaultVariable(Variables.EULER);
        putDefaultVariable(Variables.NAN);
        putDefaultVariable(Variables.NULL);
        putDefaultVariable(Variables.TRUE);
        putDefaultVariable(Variables.FALSE);
    }

    public ExpressionParser(final String expression) {
        if (expression == null) {
            throw new NullPointerException("expression should not be null");
        }
        originalExpression = modifyExpression(expression);
    }

    protected String modifyExpression(final String expression) {
        return expression;
    }

    public static IFunctionFactory putDefaultFunction(final IFunctionFactory function) {
        return DEFAULT_FUNCTIONS.put(function.getExpressionName().toLowerCase(), function);
    }

    public static IFunctionFactory putDefaultFunction(final AFunction function) {
        return DEFAULT_FUNCTIONS.put(function.getExpressionName().toLowerCase(), IFunctionFactory.valueOf(function));
    }

    public static Collection<IFunctionFactory> getDefaultFunctions() {
        return DEFAULT_FUNCTIONS.values();
    }

    public static IVariable putDefaultVariable(final IVariable variable) {
        return DEFAULT_VARIABLES.put(variable.getExpressionName().toLowerCase(), variable);
    }

    public static Collection<IVariable> getDefaultVariables() {
        return DEFAULT_VARIABLES.values();
    }

    @Override
    public IExpression parse() {
        try {
            tokenizer = TOKENIZER.get();
            tokenizer.init(originalExpression, isSemicolonAllowed());
            final IParsedExpression result = simplify(expression(true));
            if (tokenizer.current().isNotEnd()) {
                final Token token = tokenizer.consume();
                throw new ParseException(token,
                        TextDescription.format("Unexpected token: '%s'. Expected an expression", token.getSource()));
            }
            return result;
        } catch (final ParseException e) {
            if (Throwables.isDebugStackTraceEnabled()) {
                throw new ParseException(e.getPosition(),
                        TextDescription.format("%s (%s)", e.getMessage(), originalExpression));
            } else {
                throw e;
            }
        } catch (final Throwable t) {
            if (Throwables.isDebugStackTraceEnabled()) {
                throw new RuntimeException("At: " + originalExpression, t);
            } else {
                throw t;
            }
        } finally {
            tokenizer = null;
        }
    }

    protected boolean isSemicolonAllowed() {
        return false;
    }

    protected IParsedExpression simplify(final IParsedExpression expression) {
        return expression.simplify();
    }

    protected IParsedExpression expression(final boolean commaAllowed) {
        final IParsedExpression left = relationalExpression();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (commaAllowed && current.matches(",")) {
                tokenizer.consume();
                final IParsedExpression right = expression(commaAllowed);
                return reOrder(left, right, getCommaOp());
            }
            if (current.matches("&&")) {
                tokenizer.consume();
                final IParsedExpression right = expression(commaAllowed);
                return reOrder(left, right, DEFAULT_COMMA_OP);
            }
            if (current.matches("||")) {
                tokenizer.consume();
                final IParsedExpression right = expression(commaAllowed);
                return reOrder(left, right, Op.OR);
            }
        } else if ("and".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, DEFAULT_COMMA_OP);
        } else if ("or".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, Op.OR);
        } else if ("xor".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, Op.XOR);
        } else if ("pand".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, Op.PAND);
        } else if ("por".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, Op.POR);
        }
        return left;
    }

    /**
     * Someone might want to switch to OR here
     */
    protected Op getCommaOp() {
        return DEFAULT_COMMA_OP;
    }

    //CHECKSTYLE:OFF
    protected IParsedExpression relationalExpression() {
        //CHECKSTYLE:ON
        final IParsedExpression left = term();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("<")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.LT);
            }
            if (current.matches("<=")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.LT_EQ);
            }
            if (current.matches("=") || current.matches("==")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.EQ);
            }
            if (current.matches(">=")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.GT_EQ);
            }
            if (current.matches(">")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.GT);
            }
            if (current.matches("!=") || current.matches("<>") || current.matches("><")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, Op.NEQ);
            }
        } else if ("crosses".equalsIgnoreCase(current.getContents())) {
            final Token next = tokenizer.next();
            if ("above".equalsIgnoreCase(next.getContents()) || "over".equalsIgnoreCase(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                final DoubleCrossesAboveOperation result = new DoubleCrossesAboveOperation(left, right,
                        getPreviousKeyFunctionOrThrow(left.getContext()),
                        getPreviousKeyFunctionOrThrow(right.getContext()));
                result.seal();
                return result;
            } else if ("below".equalsIgnoreCase(next.getContents()) || "under".equalsIgnoreCase(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                final DoubleCrossesBelowOperation result = new DoubleCrossesBelowOperation(left, right,
                        getPreviousKeyFunctionOrThrow(left.getContext()),
                        getPreviousKeyFunctionOrThrow(right.getContext()));
                result.seal();
                return result;
            }
        }
        return left;
    }

    protected IParsedExpression term() {
        final IParsedExpression left = product();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("+")) {
                tokenizer.consume();
                final IParsedExpression right = term();
                return reOrder(left, right, Op.ADD);
            }
            if (current.matches("-")) {
                tokenizer.consume();
                final IParsedExpression right = term();
                return reOrder(left, right, Op.SUBTRACT);
            }
        } else if (current.isNumber() && current.getContents().startsWith("-")) {
            current.setContent(current.getContents().substring(1));
            final IParsedExpression right = term();
            return reOrder(left, right, Op.SUBTRACT);
        }

        return left;
    }

    protected IParsedExpression product() {
        final IParsedExpression left = power();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("*")) {
                tokenizer.consume();
                final IParsedExpression right = product();
                return reOrder(left, right, Op.MULTIPLY);
            }
            if (current.matches("/")) {
                tokenizer.consume();
                final IParsedExpression right = product();
                return reOrder(left, right, Op.DIVIDE);
            }
            if (current.matches("%")) {
                tokenizer.consume();
                final IParsedExpression right = product();
                return reOrder(left, right, Op.MODULO);
            }
        }
        return left;
    }

    protected IParsedExpression reOrder(final IParsedExpression left, final IParsedExpression right, final Op op) {
        if (right instanceof IBinaryOperation) {
            final IBinaryOperation rightOp = (IBinaryOperation) right;
            if (!rightOp.isSealed() && rightOp.getOp().getPriority() == op.getPriority()) {
                return replaceLeft(rightOp, left, op);
            }
        }
        switch (op) {
        case AND:
            return new BooleanNullableAndOperation(left, right);
        case PAND:
            return new BooleanNullableParallelAndOperation(left, right);
        case OR:
            return new BooleanNullableOrOperation(left, right);
        case POR:
            return new BooleanNullableParallelOrOperation(left, right);
        case XOR:
            return new BooleanNullableExclusiveOrOperation(left, right);
        case NOT:
            return new BooleanNullableNotOperation(left, right);
        case CROSSES_ABOVE:
            return new DoubleCrossesAboveOperation(left, right, getPreviousKeyFunctionOrThrow(left.getContext()),
                    getPreviousKeyFunctionOrThrow(right.getContext()));
        case CROSSES_BELOW:
            return new DoubleCrossesBelowOperation(left, right, getPreviousKeyFunctionOrThrow(left.getContext()),
                    getPreviousKeyFunctionOrThrow(right.getContext()));
        default:
            return new DoubleBinaryOperation(op, left, right);
        }
    }

    protected IBinaryOperation replaceLeft(final IBinaryOperation target, final IParsedExpression newLeft,
            final Op op) {
        if (target.getLeft() instanceof IBinaryOperation) {
            final IBinaryOperation leftOp = (IBinaryOperation) target.getLeft();
            if (!leftOp.isSealed() && leftOp.getOp().getPriority() == op.getPriority()) {
                final IBinaryOperation replacedLeft = replaceLeft(leftOp, newLeft, op);
                final IBinaryOperation replacedTarget = target.setLeft(replacedLeft);
                return replacedTarget;
            }
        }
        return replaceLeftDirect(target, newLeft, op);
    }

    private IBinaryOperation replaceLeftDirect(final IBinaryOperation target, final IParsedExpression newLeft,
            final Op op) {
        switch (op) {
        case AND:
            return target.setLeft(new BooleanNullableAndOperation(newLeft, target.getLeft()));
        case PAND:
            return target.setLeft(new BooleanNullableParallelAndOperation(newLeft, target.getLeft()));
        case OR:
            return target.setLeft(new BooleanNullableOrOperation(newLeft, target.getLeft()));
        case POR:
            return target.setLeft(new BooleanNullableParallelOrOperation(newLeft, target.getLeft()));
        case XOR:
            return target.setLeft(new BooleanNullableExclusiveOrOperation(newLeft, target.getLeft()));
        case NOT:
            return target.setLeft(new BooleanNullableNotOperation(newLeft, target.getLeft()));
        case CROSSES_ABOVE:
            return target.setLeft(new DoubleCrossesAboveOperation(newLeft, target.getLeft(),
                    getPreviousKeyFunctionOrThrow(newLeft.getContext()),
                    getPreviousKeyFunctionOrThrow(target.getLeft().getContext())));
        case CROSSES_BELOW:
            return target.setLeft(new DoubleCrossesBelowOperation(newLeft, target.getLeft(),
                    getPreviousKeyFunctionOrThrow(newLeft.getContext()),
                    getPreviousKeyFunctionOrThrow(target.getLeft().getContext())));
        default:
            return target.setLeft(new DoubleBinaryOperation(op, newLeft, target.getLeft()));
        }
    }

    protected IParsedExpression power() {
        final IParsedExpression left = atom();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("^") || current.matches("**")) {
                tokenizer.consume();
                final IParsedExpression right = power();
                return reOrder(left, right, Op.POWER);
            }
        }
        return left;
    }

    //CHECKSTYLE:OFF
    protected IParsedExpression atom() {
        //CHECKSTYLE:ON
        Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("-")) {
                tokenizer.consume();
                final DoubleBinaryOperation result = new DoubleBinaryOperation(Op.SUBTRACT,
                        BooleanConstantExpression.FALSE, atom());
                result.seal();
                return result;
            }
            if (current.matches("!")) {
                tokenizer.consume();
                final DoubleBinaryOperation result = new BooleanNullableNotOperation(BooleanConstantExpression.FALSE,
                        atom());
                result.seal();
                return result;
            }
            if (current.matches("+") && tokenizer.next().matches("(")) {
                // Support for brackets with a leading + like "+(2.2)" in this case we simply ignore the
                // + sign
                tokenizer.consume();
                current = tokenizer.current();
            }
            if (current.matches("(")) {
                tokenizer.consume();
                final IParsedExpression result = expression(false);
                if (result instanceof IBinaryOperation) {
                    ((IBinaryOperation) result).seal();
                }
                expect(Token.TokenType.SYMBOL, ")");
                return result;
            }
            if (current.matches("|")) {
                tokenizer.consume();
                final IParsedExpression param = expression(false);
                expect(Token.TokenType.SYMBOL, "|");
                return new DoubleFunctionCall(null, MathFunctions.ABS, param);
            }
        } else if (current.isIdentifier()) {
            final IParsedExpression functionOrVariable = functionOrVariable();
            final Token newCurrent = tokenizer.current();
            if (newCurrent.isSymbol() && newCurrent.matches("[")) {
                tokenizer.consume();
                final IParsedExpression indexExpression = expression(false);
                tokenizer.consumeExpectedSymbol("]");
                return new DynamicPreviousKeyExpression(functionOrVariable, indexExpression,
                        getPreviousKeyFunctionOrThrow(functionOrVariable.getContext()));
            } else {
                return functionOrVariable;
            }
        }
        return literalAtom();
    }

    private IParsedExpression functionOrVariable() {
        Token token = tokenizer.current();
        final String str = ExpressionContextUtil.collectContext(tokenizer, originalExpression, false);
        token = Token.create(token, str);
        final int nextCharIdx = token.getIndexOffset() + token.getLength();
        if (nextCharIdx < originalExpression.length()) {
            final char nextChar = originalExpression.charAt(nextCharIdx);
            if (nextChar == '(') {
                expect(Token.TokenType.SYMBOL, "(");
                return functionCall(token, str);
            }
        }
        return variableReference(token, str);
    }

    protected IParsedExpression variableReference(final Token variableToken, final String variableStr) {
        String variableContext;
        final String variableName;
        final int lastIndexOfContextSeparator = variableStr.lastIndexOf(':');
        if (lastIndexOfContextSeparator > 0 && lastIndexOfContextSeparator < variableStr.length()) {
            final int start = variableToken.getIndexOffset();
            //keep original casing because contexts might be case sensitive (e.g. instrument IDs with parameters)
            variableContext = modifyContext(originalExpression.substring(start, start + lastIndexOfContextSeparator));
            variableName = variableStr.substring(lastIndexOfContextSeparator + 1).toLowerCase();
        } else {
            variableContext = null;
            variableName = variableStr.toLowerCase();
        }

        if ("of".equalsIgnoreCase(tokenizer.current().getContents())) {
            tokenizer.consume();
            variableContext = ofContext(variableContext);
        }

        final IParsedExpression variable = findVariable(variableToken, variableContext, variableName);
        return variable;
    }

    protected IParsedExpression functionCall(final Token functionToken, final String functionStr) {
        String functionContext;
        final String functionName;
        final int lastIndexOfContextSeparator = functionStr.lastIndexOf(':');
        if (lastIndexOfContextSeparator > 0 && lastIndexOfContextSeparator < functionStr.length()) {
            final int start = functionToken.getIndexOffset();
            //keep original casing because contexts might be case sensitive (e.g. instrument IDs with parameters)
            functionContext = modifyContext(originalExpression.substring(start, start + lastIndexOfContextSeparator));
            functionName = functionStr.substring(lastIndexOfContextSeparator + 1).toLowerCase();
        } else {
            functionContext = null;
            functionName = functionStr.toLowerCase();
        }

        final List<IParsedExpression> parameters = new ArrayList<>();
        while (!tokenizer.current().isSymbol(")") && tokenizer.current().isNotEnd()) {
            if (!parameters.isEmpty()) {
                expect(Token.TokenType.SYMBOL, ",");
            }
            parameters.add(expression(false));
        }
        expect(Token.TokenType.SYMBOL, ")");

        if ("of".equals(tokenizer.current().getContents())) {
            tokenizer.consume();
            functionContext = ofContext(functionContext);
        }

        final AFunction fun = findFunction(functionToken, functionContext, functionName);
        final int numberOfArgumentsMax = fun.getNumberOfArgumentsMax();
        final int numberOfArgumentsMin = fun.getNumberOfArgumentsMin();
        final int arguments = parameters.size();
        if (fun.isVarArgs()) {
            if (arguments < numberOfArgumentsMin) {
                throw new ParseException(functionToken, TextDescription.format(
                        "Wrong number of arguments for function '%s'. Expected at least min=%s with max=variable but found: %s",
                        functionStr, numberOfArgumentsMin, arguments));
            }
        } else {
            if (arguments < numberOfArgumentsMin || arguments > numberOfArgumentsMax) {
                throw new ParseException(functionToken, TextDescription.format(
                        "Wrong number of arguments for function '%s'. Expected between min=%s and max=%s but found: %s",
                        functionStr, numberOfArgumentsMin, numberOfArgumentsMax, arguments));
            }
        }
        final IParsedExpression[] parametersArray = parameters.toArray(new IParsedExpression[arguments]);
        return fun.newCall(functionContext, parametersArray);
    }

    protected String modifyContext(final String context) {
        return context;
    }

    protected String ofContext(final String existingContext) {
        final String originalContext = ExpressionContextUtil.collectContext(tokenizer, originalExpression, true);
        final String context = modifyContext(originalContext);

        if (existingContext != null && !existingContext.equals(context)) {
            throw new ParseException(tokenizer.current(), "Ambiguous context defitions [" + existingContext + "] and ["
                    + context + "] introduced by OF operator.");
        }

        return context;
    }

    protected IPreviousKeyFunction getPreviousKeyFunctionOrThrow(final String context) {
        final IPreviousKeyFunction previousKeyFunction = getPreviousKeyFunction(context);
        if (previousKeyFunction == null) {
            throw new UnsupportedOperationException(
                    "getPreviousKeyFunction() needs to be implemented for indexed expressions");
        } else {
            return previousKeyFunction;
        }
    }

    protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
        return null;
    }

    private IParsedExpression literalAtom() {
        Token current = tokenizer.current();
        if (current.isSymbol("+") && tokenizer.next().isNumber()) {
            // Parse numbers with a leading + sign like +2.02 by simply ignoring the +
            tokenizer.consume();
            current = tokenizer.current();
        }
        if (current.isNumber()) {
            final String valueStr = tokenizer.consume().getContents();
            double value = Double.parseDouble(valueStr);
            if (tokenizer.current().isIdentifier()) {
                //CHECKSTYLE:OFF
                final String quantifierStr = tokenizer.current().getContents();
                if (quantifierStr.length() == 1) {
                    final char quantifier = quantifierStr.charAt(0);
                    if ('n' == quantifier) {
                        value /= 1000000000d;
                        tokenizer.consume();
                    } else if ('u' == quantifier) {
                        value /= 1000000d;
                        tokenizer.consume();
                    } else if ('m' == quantifier) {
                        value /= 1000d;
                        tokenizer.consume();
                    } else if ('K' == quantifier || 'k' == quantifier) {
                        value *= 1000d;
                        tokenizer.consume();
                    } else if ('M' == quantifier) {
                        value *= 1000000d;
                        tokenizer.consume();
                    } else if ('G' == quantifier) {
                        value *= 1000000000d;
                        tokenizer.consume();
                    } else {
                        final Token token = tokenizer.consume();
                        throw new ParseException(token, String
                                .format("Unexpected token: '%s'. Expected a valid quantifier.", token.getSource()));
                    }
                }
                //CHECKSTYLE:ON
            }
            return new ConstantExpression(value);
        }
        final Token token = tokenizer.consume();
        throw new ParseException(token,
                TextDescription.format("Unexpected token: '%s'. Expected an expression.", token.getSource()));
    }

    private AFunction findFunction(final IPosition position, final String context, final String name) {
        final AFunction function = getFunction(context, name);
        if (function != null) {
            return function;
        }

        //redirect to variable if possible
        final AVariableReference<?> variable = getVariable(context, name);
        if (variable != null) {
            return variable.asFunction();
        }

        throw new ParseException(position, TextDescription.format("Unknown function: '%s'", name));
    }

    public AFunction getFunction(final String context, final String name) {
        final IFunctionFactory functionFactory = DEFAULT_FUNCTIONS.get(name);
        if (functionFactory == null) {
            return null;
        }
        return functionFactory.newFunction(getPreviousKeyFunction(context));
    }

    private IParsedExpression findVariable(final IPosition position, final String context, final String name) {
        final IParsedExpression variable = getVariable(context, name);
        if (variable != null) {
            return variable;
        }
        //redirect to function if possible
        final AFunction function = getFunction(context, name);
        if (function != null) {
            if (function.getNumberOfArgumentsMin() == 0) {
                return function.newCall(context, IParsedExpression.EMPTY_EXPRESSIONS);
            } else {
                throw new ParseException(position,
                        TextDescription.format(
                                "Wrong number of arguments for function '%s'. Exprected at least %s but found 0", name,
                                function.getNumberOfArgumentsMin()));
            }
        }

        throw new ParseException(position, TextDescription.format("Unknown variable: '%s'", name));
    }

    public AVariableReference<?> getVariable(final String context, final String name) {
        final IVariable variable = DEFAULT_VARIABLES.get(name);
        if (variable != null) {
            return variable.newReference(context);
        }
        return null;
    }

    protected Token expect(final Token.TokenType type, final String trigger) {
        final Token current = tokenizer.current();
        if (current.is(type) && current.matches(trigger)) {
            tokenizer.consume();
            return current;
        } else {
            throw new ParseException(current,
                    TextDescription.format("Unexpected token '%s'. Expected: '%s'", current.getSource(), trigger));
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(originalExpression).toString();
    }
}
