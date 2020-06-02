package de.invesdwin.util.math.expression;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.DynamicPreviousKeyExpression;
import de.invesdwin.util.math.expression.eval.FunctionCall;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.VariableFunction;
import de.invesdwin.util.math.expression.eval.VariableReference;
import de.invesdwin.util.math.expression.eval.operation.AndOperation;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation.Op;
import de.invesdwin.util.math.expression.eval.operation.CrossesAboveOperation;
import de.invesdwin.util.math.expression.eval.operation.CrossesBelowOperation;
import de.invesdwin.util.math.expression.eval.operation.NotOperation;
import de.invesdwin.util.math.expression.eval.operation.OrOperation;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.function.HistoricalFunctions;
import de.invesdwin.util.math.expression.function.IFunctionFactory;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.function.LogicalFunctions;
import de.invesdwin.util.math.expression.function.MathFunctions;
import de.invesdwin.util.math.expression.tokenizer.IPosition;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.math.expression.tokenizer.Token;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;
import de.invesdwin.util.math.expression.variable.IVariable;
import de.invesdwin.util.math.expression.variable.Variables;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public class ExpressionParser {

    private static final FastThreadLocal<Tokenizer> TOKENIZER = new FastThreadLocal<Tokenizer>() {
        @Override
        protected Tokenizer initialValue() throws Exception {
            return new Tokenizer();
        }
    };

    private static final Map<String, IFunctionFactory> DEFAULT_FUNCTIONS;
    private static final Map<String, IVariable> DEFAULT_VARIABLES;

    private final Tokenizer tokenizer;
    private final String originalExpression;

    //CHECKSTYLE:OFF
    static {
        //CHECKSTYLE:ON
        DEFAULT_FUNCTIONS = new LinkedHashMap<>();

        registerDefaultFunction(MathFunctions.SIN);
        registerDefaultFunction(MathFunctions.COS);
        registerDefaultFunction(MathFunctions.TAN);
        registerDefaultFunction(MathFunctions.SINH);
        registerDefaultFunction(MathFunctions.COSH);
        registerDefaultFunction(MathFunctions.TANH);
        registerDefaultFunction(MathFunctions.ASIN);
        registerDefaultFunction(MathFunctions.ACOS);
        registerDefaultFunction(MathFunctions.ATAN);
        registerDefaultFunction(MathFunctions.ATAN2);
        registerDefaultFunction(MathFunctions.DEG);
        registerDefaultFunction(MathFunctions.RAD);
        registerDefaultFunction(MathFunctions.ABS);
        registerDefaultFunction(MathFunctions.ROUND);
        registerDefaultFunction(MathFunctions.CEIL);
        registerDefaultFunction(MathFunctions.FLOOR);
        registerDefaultFunction(MathFunctions.EXP);
        registerDefaultFunction(MathFunctions.LN);
        registerDefaultFunction(MathFunctions.LOG);
        registerDefaultFunction(MathFunctions.SQRT);
        registerDefaultFunction(MathFunctions.POW);
        for (final String name : new String[] { "min", "minimum" }) {
            registerDefaultFunction(MathFunctions.newMinimumFunction(name));
        }
        for (final String name : new String[] { "max", "maximum" }) {
            registerDefaultFunction(MathFunctions.newMaximumFunction(name));
        }
        for (final String name : new String[] { "between", "clamp" }) {
            registerDefaultFunction(MathFunctions.newBetweenFunction(name));
        }
        for (final String name : new String[] { "random", "rnd", "rng" }) {
            registerDefaultFunction(MathFunctions.newRandomFunction(name));
        }
        registerDefaultFunction(MathFunctions.NORMALIZE_VALUE);
        registerDefaultFunction(MathFunctions.SIGN);
        registerDefaultFunction(MathFunctions.NEGATE);

        registerDefaultFunction(LogicalFunctions.IF);

        for (final String name : new String[] { "map", "select", "array", "decide" }) {
            registerDefaultFunction(LogicalFunctions.newMapFunction(name));
        }
        for (final String name : new String[] { "vote", "ensemble", "threshold", "majority", "fuzzy" }) {
            registerDefaultFunction(LogicalFunctions.newVoteFunction(name));
        }

        registerDefaultFunction(LogicalFunctions.ISNAN);
        registerDefaultFunction(LogicalFunctions.ISTRUE);
        registerDefaultFunction(LogicalFunctions.ISFALSE);
        registerDefaultFunction(LogicalFunctions.NOT);

        for (final String name : new String[] { "once", "onceOnly", "onChange", "onChangeOnly", "onChangeOnlyOnce",
                "changeOnly", "changed", "change", "single", "singleOnly" }) {
            registerDefaultFunction(HistoricalFunctions.newOnceFunction(name));
        }
        for (final String name : new String[] { "stable", "repeat", "repeatAnd", "loop", "loopAnd", "hist", "histAnd",
                "historical", "historicalAnd" }) {
            registerDefaultFunction(HistoricalFunctions.newStableFunction(name));
            registerDefaultFunction(HistoricalFunctions.newStableFunction(name + "Both"));
            registerDefaultFunction(HistoricalFunctions.newStableLeftFunction(name + "Left"));
            registerDefaultFunction(HistoricalFunctions.newStableRightFunction(name + "Right"));

            registerDefaultFunction(HistoricalFunctions.newStableCountFunction(name));
            registerDefaultFunction(HistoricalFunctions.newStableCountFunction(name + "Count" + "Both"));
            registerDefaultFunction(HistoricalFunctions.newStableCountLeftFunction(name + "Count" + "Left"));
            registerDefaultFunction(HistoricalFunctions.newStableCountRightFunction(name + "Count" + "Right"));
        }
        for (final String name : new String[] { "occurs", "repeatOr", "loopOr", "histOr", "historicalOr" }) {
            registerDefaultFunction(HistoricalFunctions.newOccursFunction(name));
            registerDefaultFunction(HistoricalFunctions.newOccursFunction(name + "Both"));
            registerDefaultFunction(HistoricalFunctions.newOccursLeftFunction(name + "Left"));
            registerDefaultFunction(HistoricalFunctions.newOccursRightFunction(name + "Right"));

            registerDefaultFunction(HistoricalFunctions.newOccursCountFunction(name + "Count"));
            registerDefaultFunction(HistoricalFunctions.newOccursCountFunction(name + "Count" + "Both"));
            registerDefaultFunction(HistoricalFunctions.newOccursCountLeftFunction(name + "Count" + "Left"));
            registerDefaultFunction(HistoricalFunctions.newOccursCountRightFunction(name + "Count" + "Right"));
        }
        registerDefaultFunction(HistoricalFunctions.newFirstIndexOfFunction("firstIndexOf"));
        for (final String name : new String[] { "indexOf", "lastIndexOf" }) {
            registerDefaultFunction(HistoricalFunctions.newLastIndexOfFunction(name));
        }

        DEFAULT_VARIABLES = new LinkedHashMap<>();

        registerDefaultVariable(Variables.PI);
        registerDefaultVariable(Variables.EULER);
        registerDefaultVariable(Variables.NAN);
        registerDefaultVariable(Variables.NULL);
        registerDefaultVariable(Variables.TRUE);
        registerDefaultVariable(Variables.FALSE);
    }

    public ExpressionParser(final String expression) {
        tokenizer = TOKENIZER.get();
        originalExpression = modifyExpression(expression);
        tokenizer.init(new StringReader(originalExpression));
    }

    protected String modifyExpression(final String expression) {
        return expression;
    }

    public static void registerDefaultFunction(final IFunctionFactory function) {
        DEFAULT_FUNCTIONS.put(function.getExpressionName().toLowerCase(), function);
    }

    public static void registerDefaultFunction(final AFunction function) {
        DEFAULT_FUNCTIONS.put(function.getExpressionName().toLowerCase(), new IFunctionFactory() {

            @Override
            public String getExpressionName() {
                return function.getExpressionName();
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                return function;
            }
        });
    }

    public static Collection<IFunctionFactory> getDefaultFunctions() {
        return DEFAULT_FUNCTIONS.values();
    }

    public static void registerDefaultVariable(final IVariable variable) {
        DEFAULT_VARIABLES.put(variable.getExpressionName().toLowerCase(), variable);
    }

    public static Collection<IVariable> getDefaultVariables() {
        return DEFAULT_VARIABLES.values();
    }

    public IExpression parse() {
        final IParsedExpression result = simplify(expression(true));
        if (tokenizer.current().isNotEnd()) {
            final Token token = tokenizer.consume();
            throw new ParseException(token,
                    TextDescription.format("Unexpected token: '%s'. Expected an expression.", token.getSource()));
        }
        return result;
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
                return reOrder(left, right, BinaryOperation.Op.AND);
            }
            if (current.matches("||")) {
                tokenizer.consume();
                final IParsedExpression right = expression(commaAllowed);
                return reOrder(left, right, BinaryOperation.Op.OR);
            }
        } else if ("and".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, BinaryOperation.Op.AND);
        } else if ("or".equalsIgnoreCase(current.getContents())) {
            tokenizer.consume();
            final IParsedExpression right = expression(commaAllowed);
            return reOrder(left, right, BinaryOperation.Op.OR);
        }
        return left;
    }

    /**
     * Someone might want to switch to OR here
     */
    protected Op getCommaOp() {
        return BinaryOperation.Op.AND;
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
                return reOrder(left, right, BinaryOperation.Op.LT);
            }
            if (current.matches("<=")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.LT_EQ);
            }
            if (current.matches("=") || current.matches("==")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.EQ);
            }
            if (current.matches(">=")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.GT_EQ);
            }
            if (current.matches(">")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.GT);
            }
            if (current.matches("!=") || current.matches("<>") || current.matches("><")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.NEQ);
            }
        } else if ("crosses".equals(current.getContents())) {
            final Token next = tokenizer.next();
            if ("above".equals(next.getContents()) || "over".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                final CrossesAboveOperation result = new CrossesAboveOperation(left, right,
                        getPreviousKeyFunctionOrThrow(left.getContext()),
                        getPreviousKeyFunctionOrThrow(right.getContext()));
                result.seal();
                return result;
            } else if ("below".equals(next.getContents()) || "under".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                final CrossesBelowOperation result = new CrossesBelowOperation(left, right,
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
                return reOrder(left, right, BinaryOperation.Op.ADD);
            }
            if (current.matches("-")) {
                tokenizer.consume();
                final IParsedExpression right = term();
                return reOrder(left, right, BinaryOperation.Op.SUBTRACT);
            }
        } else if (current.isNumber() && current.getContents().startsWith("-")) {
            current.setContent(current.getContents().substring(1));
            final IParsedExpression right = term();
            return reOrder(left, right, BinaryOperation.Op.SUBTRACT);
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
                return reOrder(left, right, BinaryOperation.Op.MULTIPLY);
            }
            if (current.matches("/")) {
                tokenizer.consume();
                final IParsedExpression right = product();
                return reOrder(left, right, BinaryOperation.Op.DIVIDE);
            }
            if (current.matches("%")) {
                tokenizer.consume();
                final IParsedExpression right = product();
                return reOrder(left, right, BinaryOperation.Op.MODULO);
            }
        }
        return left;
    }

    protected IParsedExpression reOrder(final IParsedExpression left, final IParsedExpression right,
            final BinaryOperation.Op op) {
        if (right instanceof BinaryOperation) {
            final BinaryOperation rightOp = (BinaryOperation) right;
            if (!rightOp.isSealed() && rightOp.getOp().getPriority() == op.getPriority()) {
                return replaceLeft(rightOp, left, op);
            }
        }
        switch (op) {
        case AND:
            return new AndOperation(left, right);
        case OR:
            return new OrOperation(left, right);
        case NOT:
            return new NotOperation(left, right);
        case CROSSES_ABOVE:
            return new CrossesAboveOperation(left, right, getPreviousKeyFunctionOrThrow(left.getContext()),
                    getPreviousKeyFunctionOrThrow(right.getContext()));
        case CROSSES_BELOW:
            return new CrossesBelowOperation(left, right, getPreviousKeyFunctionOrThrow(left.getContext()),
                    getPreviousKeyFunctionOrThrow(right.getContext()));
        default:
            return new BinaryOperation(op, left, right);
        }
    }

    protected BinaryOperation replaceLeft(final BinaryOperation target, final IParsedExpression newLeft,
            final BinaryOperation.Op op) {
        if (target.getLeft() instanceof BinaryOperation) {
            final BinaryOperation leftOp = (BinaryOperation) target.getLeft();
            if (!leftOp.isSealed() && leftOp.getOp().getPriority() == op.getPriority()) {
                final BinaryOperation replacedLeft = replaceLeft(leftOp, newLeft, op);
                final BinaryOperation replacedTarget = target.setLeft(replacedLeft);
                return replacedTarget;
            }
        }
        return replaceLeftDirect(target, newLeft, op);
    }

    private BinaryOperation replaceLeftDirect(final BinaryOperation target, final IParsedExpression newLeft,
            final BinaryOperation.Op op) {
        switch (op) {
        case AND:
            return target.setLeft(new AndOperation(newLeft, target.getLeft()));
        case OR:
            return target.setLeft(new OrOperation(newLeft, target.getLeft()));
        case NOT:
            return target.setLeft(new NotOperation(newLeft, target.getLeft()));
        case CROSSES_ABOVE:
            return target.setLeft(new CrossesAboveOperation(newLeft, target.getLeft(),
                    getPreviousKeyFunctionOrThrow(newLeft.getContext()),
                    getPreviousKeyFunctionOrThrow(target.getLeft().getContext())));
        case CROSSES_BELOW:
            return target.setLeft(new CrossesBelowOperation(newLeft, target.getLeft(),
                    getPreviousKeyFunctionOrThrow(newLeft.getContext()),
                    getPreviousKeyFunctionOrThrow(target.getLeft().getContext())));
        default:
            return target.setLeft(new BinaryOperation(op, newLeft, target.getLeft()));
        }
    }

    protected IParsedExpression power() {
        final IParsedExpression left = atom();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("^") || current.matches("**")) {
                tokenizer.consume();
                final IParsedExpression right = power();
                return reOrder(left, right, BinaryOperation.Op.POWER);
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
                final BinaryOperation result = new BinaryOperation(BinaryOperation.Op.SUBTRACT,
                        new ConstantExpression(0d), atom());
                result.seal();
                return result;
            }
            if (current.matches("!")) {
                tokenizer.consume();
                final BinaryOperation result = new NotOperation(new ConstantExpression(0d), atom());
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
                if (result instanceof BinaryOperation) {
                    ((BinaryOperation) result).seal();
                }
                expect(Token.TokenType.SYMBOL, ")");
                return result;
            }
            if (current.matches("|")) {
                tokenizer.consume();
                final IParsedExpression param = expression(false);
                expect(Token.TokenType.SYMBOL, "|");
                return new FunctionCall(null, MathFunctions.ABS, param);
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
        final String str = collectContext(true);
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
        return new FunctionCall(functionContext, fun, parametersArray);
    }

    protected String modifyContext(final String context) {
        return context;
    }

    protected String ofContext(final String existingContext) {
        final String originalContext = collectContext(false);
        final String context = modifyContext(originalContext);

        if (existingContext != null && !existingContext.equals(context)) {
            throw new ParseException(tokenizer.current(), "Ambiguous context defitions [" + existingContext + "] and ["
                    + context + "] introduced by OF operator.");
        }

        return context;
    }

    private String collectContext(final boolean ignoreBracketsAtEnd) {
        final StringBuilder context = new StringBuilder();
        boolean consumeMore = true;
        while (consumeMore) {
            consumeMore = false;
            final Token contextToken = tokenizer.current();
            tokenizer.consume();
            final int start = contextToken.getIndexOffset();
            int end = start + contextToken.getLength();
            int skipCharacters = -1;
            int skipBracketClose = -1;
            while (true) {
                if (originalExpression.length() <= end) {
                    break;
                }
                final char endCharacter = originalExpression.charAt(end);
                if (endCharacter == '[') {
                    skipBracketClose++;
                } else if (endCharacter == ']') {
                    skipBracketClose--;
                    skipCharacters++;
                    end++;
                }
                if (skipBracketClose >= 0) {
                    skipCharacters++;
                    end++;
                } else {
                    break;
                }
            }

            final int next = end;
            if (originalExpression.length() > next) {
                final char nextCharacter = originalExpression.charAt(next);
                if (nextCharacter == '@' || nextCharacter == ':') {
                    consumeMore = true;
                    skipCharacters++;
                    end++;
                }
            }
            maybeSkipContextCharacters(ignoreBracketsAtEnd, context, consumeMore, start, end, skipCharacters);
        }
        return context.toString();
    }

    private void maybeSkipContextCharacters(final boolean ignoreBracketsAtEnd, final StringBuilder context,
            final boolean consumeMore, final int start, final int end, final int skipCharacters) {
        context.append(originalExpression.substring(start, end));
        int usedSkipCharacters = skipCharacters;
        if (ignoreBracketsAtEnd && !consumeMore && Strings.endsWith(context, "]")) {
            final int lastIndexOf = context.lastIndexOf("[");
            if (lastIndexOf >= 0) {
                usedSkipCharacters -= context.length() - lastIndexOf + 1;
                context.setLength(lastIndexOf);
            }
        }
        if (usedSkipCharacters > 0) {
            tokenizer.skipCharacters(skipCharacters);
        }
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
            double value = Double.parseDouble(tokenizer.consume().getContents());
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
        final VariableReference variable = getVariable(context, name);
        if (variable != null) {
            return new VariableFunction(context, name, variable);
        }

        throw new ParseException(position, TextDescription.format("Unknown function: '%s'", name));
    }

    protected AFunction getFunction(final String context, final String name) {
        return DEFAULT_FUNCTIONS.get(name).newFunction(getPreviousKeyFunction(context));
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
                return new FunctionCall(context, function);
            } else {
                throw new ParseException(position,
                        TextDescription.format(
                                "Wrong number of arguments for function '%s'. Exprected at least %s but found 0", name,
                                function.getNumberOfArgumentsMin()));
            }
        }

        throw new ParseException(position, TextDescription.format("Unknown variable: '%s'", name));
    }

    protected VariableReference getVariable(final String context, final String name) {
        final IVariable variable = DEFAULT_VARIABLES.get(name);
        if (variable != null) {
            return new VariableReference(context, variable);
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
