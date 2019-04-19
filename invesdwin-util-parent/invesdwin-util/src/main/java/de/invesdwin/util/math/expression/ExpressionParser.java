package de.invesdwin.util.math.expression;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.math.expression.eval.DynamicPreviousKeyExpression;
import de.invesdwin.util.math.expression.eval.FunctionCall;
import de.invesdwin.util.math.expression.eval.Functions;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.VariableFunction;
import de.invesdwin.util.math.expression.eval.VariableReference;
import de.invesdwin.util.math.expression.eval.Variables;
import de.invesdwin.util.math.expression.eval.operation.AndOperation;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation;
import de.invesdwin.util.math.expression.eval.operation.BinaryOperation.Op;
import de.invesdwin.util.math.expression.eval.operation.CrossesAboveOperation;
import de.invesdwin.util.math.expression.eval.operation.CrossesBelowOperation;
import de.invesdwin.util.math.expression.eval.operation.NotOperation;
import de.invesdwin.util.math.expression.eval.operation.OrOperation;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.math.expression.tokenizer.Token;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;
import de.invesdwin.util.math.expression.variable.IVariable;
import io.netty.util.concurrent.FastThreadLocal;

@NotThreadSafe
public class ExpressionParser {

    public static final String[] MODIFY_INPUT = new String[] { //
            " and ", //
            " or ", //
            " <> ", " >< ", //
    };
    public static final String[] MODIFY_OUTPUT = new String[] { //
            " && ", //
            " || ", //
            " != ", " != ", //
    };
    private static final FastThreadLocal<Tokenizer> TOKENIZER = new FastThreadLocal<Tokenizer>() {
        @Override
        protected Tokenizer initialValue() throws Exception {
            return new Tokenizer();
        }
    };

    private static final Map<String, IFunction> DEFAULT_FUNCTIONS;
    private static final Map<String, VariableReference> DEFAULT_VARIABLES;

    private final Tokenizer tokenizer;

    static {
        DEFAULT_FUNCTIONS = new TreeMap<>();

        registerDefaultFunction(Functions.SIN);
        registerDefaultFunction(Functions.COS);
        registerDefaultFunction(Functions.TAN);
        registerDefaultFunction(Functions.SINH);
        registerDefaultFunction(Functions.COSH);
        registerDefaultFunction(Functions.TANH);
        registerDefaultFunction(Functions.ASIN);
        registerDefaultFunction(Functions.ACOS);
        registerDefaultFunction(Functions.ATAN);
        registerDefaultFunction(Functions.ATAN2);
        registerDefaultFunction(Functions.DEG);
        registerDefaultFunction(Functions.RAD);
        registerDefaultFunction(Functions.ABS);
        registerDefaultFunction(Functions.ROUND);
        registerDefaultFunction(Functions.CEIL);
        registerDefaultFunction(Functions.FLOOR);
        registerDefaultFunction(Functions.EXP);
        registerDefaultFunction(Functions.LN);
        registerDefaultFunction(Functions.LOG);
        registerDefaultFunction(Functions.SQRT);
        registerDefaultFunction(Functions.POW);
        registerDefaultFunction(Functions.MIN);
        registerDefaultFunction(Functions.MAX);
        registerDefaultFunction(Functions.RND);
        registerDefaultFunction(Functions.SIGN);
        registerDefaultFunction(Functions.IF);
        registerDefaultFunction(Functions.ISNAN);
        registerDefaultFunction(Functions.ISTRUE);
        registerDefaultFunction(Functions.ISFALSE);
        registerDefaultFunction(Functions.NEGATE);
        registerDefaultFunction(Functions.NOT);

        DEFAULT_VARIABLES = new TreeMap<>();

        registerDefaultVariable(Variables.PI);
        registerDefaultVariable(Variables.EULER);
        registerDefaultVariable(Variables.NAN);
        registerDefaultVariable(Variables.TRUE);
        registerDefaultVariable(Variables.FALSE);
    }

    public ExpressionParser(final String expression) {
        tokenizer = TOKENIZER.get();
        //add space at the end so that replacements match properly
        tokenizer.init(new StringReader(modifyExpression(expression.toLowerCase() + " ")));
    }

    protected String modifyExpression(final String expressionLowerCase) {
        return Strings.replaceEach(expressionLowerCase, MODIFY_INPUT, MODIFY_OUTPUT);
    }

    public static void registerDefaultFunction(final IFunction function) {
        DEFAULT_FUNCTIONS.put(function.getExpressionName().toLowerCase(), function);
    }

    public static Collection<IFunction> getDefaultFunctions() {
        return DEFAULT_FUNCTIONS.values();
    }

    public static void registerDefaultVariable(final IVariable variable) {
        DEFAULT_VARIABLES.put(variable.getExpressionName().toLowerCase(), new VariableReference(null, variable));
    }

    public static Collection<VariableReference> getDefaultVariables() {
        return DEFAULT_VARIABLES.values();
    }

    public IExpression parse() {
        final IParsedExpression result = expressionComma().simplify();
        if (tokenizer.current().isNotEnd()) {
            final Token token = tokenizer.consume();
            throw new ParseException(token,
                    String.format("Unexpected token: '%s'. Expected an expression.", token.getSource()));
        }
        return result;
    }

    protected IParsedExpression expressionComma() {
        final IParsedExpression left = relationalExpression();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches(",")) {
                tokenizer.consume();
                final IParsedExpression right = expressionComma();
                return reOrder(left, right, getCommaOp());
            }
            if (current.matches("&&")) {
                tokenizer.consume();
                final IParsedExpression right = expressionComma();
                return reOrder(left, right, BinaryOperation.Op.AND);
            }
            if (current.matches("||")) {
                tokenizer.consume();
                final IParsedExpression right = expressionComma();
                return reOrder(left, right, BinaryOperation.Op.OR);
            }
        }
        return left;
    }

    /**
     * Someone might want to switch to OR here
     */
    protected Op getCommaOp() {
        return BinaryOperation.Op.AND;
    }

    protected IParsedExpression expression() {
        final IParsedExpression left = relationalExpression();
        final Token current = tokenizer.current();
        if (current.isSymbol()) {
            if (current.matches("&&")) {
                tokenizer.consume();
                final IParsedExpression right = expression();
                return reOrder(left, right, BinaryOperation.Op.AND);
            }
            if (current.matches("||")) {
                tokenizer.consume();
                final IParsedExpression right = expression();
                return reOrder(left, right, BinaryOperation.Op.OR);
            }
        }
        return left;
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
            if (current.matches("!=")) {
                tokenizer.consume();
                final IParsedExpression right = relationalExpression();
                return reOrder(left, right, BinaryOperation.Op.NEQ);
            }
        } else if ("crosses".equals(current.getContents())) {
            final Token next = tokenizer.next();
            if ("above".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                return new CrossesAboveOperation(left, right, getPreviousKeyFunction(left.getContext()),
                        getPreviousKeyFunction(right.getContext()));
            } else if ("below".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                return new CrossesBelowOperation(left, right, getPreviousKeyFunction(left.getContext()),
                        getPreviousKeyFunction(right.getContext()));
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
            return new CrossesAboveOperation(left, right, getPreviousKeyFunction(left.getContext()),
                    getPreviousKeyFunction(right.getContext()));
        case CROSSES_BELOW:
            return new CrossesBelowOperation(left, right, getPreviousKeyFunction(left.getContext()),
                    getPreviousKeyFunction(right.getContext()));
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
            return target.setLeft(
                    new CrossesAboveOperation(newLeft, target.getLeft(), getPreviousKeyFunction(newLeft.getContext()),
                            getPreviousKeyFunction(target.getLeft().getContext())));
        case CROSSES_BELOW:
            return target.setLeft(
                    new CrossesBelowOperation(newLeft, target.getLeft(), getPreviousKeyFunction(newLeft.getContext()),
                            getPreviousKeyFunction(target.getLeft().getContext())));
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
                final IParsedExpression result = expression();
                if (result instanceof BinaryOperation) {
                    ((BinaryOperation) result).seal();
                }
                expect(Token.TokenType.SYMBOL, ")");
                return result;
            }
            if (current.matches("|")) {
                tokenizer.consume();
                expect(Token.TokenType.SYMBOL, "|");
                return new FunctionCall(null, Functions.ABS, expression());
            }
        } else if (current.isIdentifier()) {
            final IParsedExpression functionOrVariable = functionOrVariable();
            final Token newCurrent = tokenizer.current();
            if (newCurrent.isSymbol() && newCurrent.matches("[")) {
                tokenizer.consume();
                final IParsedExpression indexExpression = expression();
                tokenizer.consumeExpectedSymbol("]");
                return new DynamicPreviousKeyExpression(functionOrVariable, indexExpression,
                        getPreviousKeyFunction(functionOrVariable.getContext()));
            } else {
                return functionOrVariable;
            }
        }
        return literalAtom();
    }

    private IParsedExpression functionOrVariable() {
        if (tokenizer.next().isSymbol("(")) {
            return functionCall();
        }
        final Token variableToken = tokenizer.consume();
        final String variableStr = variableToken.getContents();
        final String variableContext;
        final String variableName;
        if (variableStr.contains(":")) {
            final String[] split = Strings.split(variableStr, ':');
            variableContext = Strings.join(split, 0, split.length - 2);
            variableName = split[split.length - 1];
        } else {
            variableContext = null;
            variableName = variableStr;
        }

        final IParsedExpression variable = findVariable(variableContext, variableName);
        if (variable == null) {
            throw new ParseException(variableToken,
                    String.format("Unknown variable: '%s'", variableToken.getContents()));
        }
        return variable;
    }

    protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
        throw new UnsupportedOperationException("dynamic indexed expression needs to be implemented from the outside");
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
                String.format("Unexpected token: '%s'. Expected an expression.", token.getSource()));
    }

    protected IParsedExpression functionCall() {
        final Token funToken = tokenizer.consume();
        final String functionStr = funToken.getContents();
        final String functionContext;
        final String functionName;
        if (functionStr.contains(":")) {
            final String[] split = Strings.split(functionStr, ':');
            functionContext = Strings.join(split, 0, split.length - 2);
            functionName = split[split.length - 1];
        } else {
            functionContext = null;
            functionName = functionStr;
        }
        final IFunction fun = findFunction(functionContext, functionName);
        if (fun == null) {
            throw new ParseException(funToken, String.format("Unknown function: '%s'", functionStr));
        }
        tokenizer.consume();
        final List<IParsedExpression> parameters = new ArrayList<>();
        while (!tokenizer.current().isSymbol(")") && tokenizer.current().isNotEnd()) {
            if (!parameters.isEmpty()) {
                expect(Token.TokenType.SYMBOL, ",");
            }
            parameters.add(expression());
        }
        expect(Token.TokenType.SYMBOL, ")");
        if (parameters.size() != fun.getNumberOfArguments() && fun.getNumberOfArguments() >= 0) {
            throw new ParseException(funToken,
                    String.format("Number of arguments for function '%s' do not match. Expected: %d, Found: %d",
                            functionStr, fun.getNumberOfArguments(), parameters.size()));
        }
        final IParsedExpression[] parametersArray = parameters.toArray(new IParsedExpression[parameters.size()]);
        return new FunctionCall(functionContext, fun, parametersArray);
    }

    private IFunction findFunction(final String context, final String name) {
        final IFunction function = getFunction(context, name);
        if (function != null) {
            return function;
        }

        //redirect to variable if possible
        final VariableReference variable = getVariable(context, name);
        if (variable != null) {
            return new VariableFunction(context, name, variable);
        }

        return null;
    }

    protected IFunction getFunction(final String context, final String name) {
        return DEFAULT_FUNCTIONS.get(name);
    }

    private IParsedExpression findVariable(final String context, final String name) {
        final IParsedExpression variable = getVariable(context, name);
        if (variable != null) {
            return variable;
        }
        //redirect to function if possible
        final IFunction function = getFunction(context, name);
        if (function != null && function.getNumberOfArguments() == 0) {
            return new FunctionCall(context, function);
        }
        return null;
    }

    protected VariableReference getVariable(final String context, final String name) {
        final VariableReference variable = DEFAULT_VARIABLES.get(name);
        if (variable != null) {
            return variable;
        }
        return null;
    }

    protected void expect(final Token.TokenType type, final String trigger) {
        final Token current = tokenizer.current();
        if (current.is(type) && current.matches(trigger)) {
            tokenizer.consume();
        } else {
            throw new ParseException(current,
                    String.format("Unexpected token '%s'. Expected: '%s'", current.getSource(), trigger));
        }
    }
}
