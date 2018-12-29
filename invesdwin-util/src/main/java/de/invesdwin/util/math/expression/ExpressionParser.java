package de.invesdwin.util.math.expression;

import java.io.StringReader;
import java.util.ArrayList;
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
            " true ", //
            " false " //
    };
    public static final String[] MODIFY_OUTPUT = new String[] { //
            " && ", //
            " || ", //
            " != ", " != ", //
            " 1 ", //
            " 0 " //
    };
    private static final FastThreadLocal<Tokenizer> TOKENIZER = new FastThreadLocal<Tokenizer>() {
        @Override
        protected Tokenizer initialValue() throws Exception {
            return new Tokenizer();
        }
    };

    private static final Map<String, IFunction> FUNCTION_TABLE;
    private static final Map<String, IParsedExpression> VARIABLE_TABLE;

    private final Tokenizer tokenizer;

    static {
        FUNCTION_TABLE = new TreeMap<>();

        registerFunction(Functions.SIN);
        registerFunction(Functions.COS);
        registerFunction(Functions.TAN);
        registerFunction(Functions.SINH);
        registerFunction(Functions.COSH);
        registerFunction(Functions.TANH);
        registerFunction(Functions.ASIN);
        registerFunction(Functions.ACOS);
        registerFunction(Functions.ATAN);
        registerFunction(Functions.ATAN2);
        registerFunction(Functions.DEG);
        registerFunction(Functions.RAD);
        registerFunction(Functions.ABS);
        registerFunction(Functions.ROUND);
        registerFunction(Functions.CEIL);
        registerFunction(Functions.FLOOR);
        registerFunction(Functions.EXP);
        registerFunction(Functions.LN);
        registerFunction(Functions.LOG);
        registerFunction(Functions.SQRT);
        registerFunction(Functions.POW);
        registerFunction(Functions.MIN);
        registerFunction(Functions.MAX);
        registerFunction(Functions.RND);
        registerFunction(Functions.SIGN);
        registerFunction(Functions.IF);
        registerFunction(Functions.ISNAN);
        registerFunction(Functions.NEGATE);

        VARIABLE_TABLE = new TreeMap<>();

        registerVariable(Variables.PI);
        registerVariable(Variables.EULER);
    }

    public ExpressionParser(final String expression) {
        tokenizer = TOKENIZER.get();
        //add space at the end so that replacements match properly
        tokenizer.init(new StringReader(modifyExpression(expression.toLowerCase() + " ")));
    }

    protected String modifyExpression(final String expressionLowerCase) {
        return Strings.replaceEach(expressionLowerCase, MODIFY_INPUT, MODIFY_OUTPUT);
    }

    public static void registerFunction(final IFunction function) {
        FUNCTION_TABLE.put(function.getName().toLowerCase(), function);
    }

    public static void registerVariable(final IVariable variable) {
        VARIABLE_TABLE.put(variable.getName().toLowerCase(), new VariableReference(variable));
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
        }
        if ("crosses".equals(current.getContents())) {
            final Token next = tokenizer.next();
            if ("above".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                return new CrossesAboveOperation(left, right, getPreviousKeyFunction(left),
                        getPreviousKeyFunction(right));
            } else if ("below".equals(next.getContents())) {
                tokenizer.consume(2);
                final IParsedExpression right = relationalExpression();
                return new CrossesBelowOperation(left, right, getPreviousKeyFunction(left),
                        getPreviousKeyFunction(right));
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
        }
        if (current.isNumber() && current.getContents().startsWith("-")) {
            final IParsedExpression right = term();
            return reOrder(left, right, BinaryOperation.Op.ADD);
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
            return new CrossesAboveOperation(left, right, getPreviousKeyFunction(left), getPreviousKeyFunction(right));
        case CROSSES_BELOW:
            return new CrossesBelowOperation(left, right, getPreviousKeyFunction(left), getPreviousKeyFunction(right));
        default:
            return new BinaryOperation(op, left, right);
        }
    }

    protected BinaryOperation replaceLeft(final BinaryOperation target, final IParsedExpression newLeft,
            final BinaryOperation.Op op) {
        if (target.getLeft() instanceof BinaryOperation) {
            final BinaryOperation leftOp = (BinaryOperation) target.getLeft();
            if (!leftOp.isSealed() && leftOp.getOp().getPriority() == op.getPriority()) {
                return replaceLeft(leftOp, newLeft, op);
            }
        }
        switch (op) {
        case AND:
            return target.setLeft(new AndOperation(newLeft, target.getLeft()));
        case OR:
            return target.setLeft(new OrOperation(newLeft, target.getLeft()));
        case NOT:
            return target.setLeft(new NotOperation(newLeft, target.getLeft()));
        case CROSSES_ABOVE:
            return target.setLeft(new CrossesAboveOperation(newLeft, target.getLeft(), getPreviousKeyFunction(newLeft),
                    getPreviousKeyFunction(target.getLeft())));
        case CROSSES_BELOW:
            return target.setLeft(new CrossesBelowOperation(newLeft, target.getLeft(), getPreviousKeyFunction(newLeft),
                    getPreviousKeyFunction(target.getLeft())));
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
                return new FunctionCall(Functions.ABS, expression());
            }
        }
        if (current.isIdentifier()) {
            final IParsedExpression functionOrVariable = functionOrVariable();
            final Token newCurrent = tokenizer.current();
            if (newCurrent.isSymbol() && newCurrent.matches("[")) {
                tokenizer.consume();
                final IParsedExpression indexExpression = expression();
                tokenizer.consumeExpectedSymbol("]");
                return new DynamicPreviousKeyExpression(functionOrVariable, indexExpression,
                        getPreviousKeyFunction(functionOrVariable));
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
        final Token variableName = tokenizer.consume();
        final IParsedExpression variable = findVariable(variableName.getContents());
        if (variable == null) {
            throw new ParseException(variableName, String.format("Unknown variable: '%s'", variableName.getContents()));
        }
        return variable;
    }

    protected IPreviousKeyFunction getPreviousKeyFunction(final IExpression expression) {
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
        final IFunction fun = findFunction(funToken.getContents());
        if (fun == null) {
            throw new ParseException(funToken, String.format("Unknown function: '%s'", funToken.getContents()));
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
                            funToken.getContents(), fun.getNumberOfArguments(), parameters.size()));
        }
        final IParsedExpression[] parametersArray = parameters.toArray(new IParsedExpression[parameters.size()]);
        return new FunctionCall(fun, parametersArray);
    }

    private IFunction findFunction(final String name) {
        final IFunction function = getFunction(name);
        if (function != null) {
            return function;
        }

        //redirect to variable if possible
        final IParsedExpression variable = getVariable(name);
        if (variable != null) {
            return new VariableFunction(name, variable);
        }

        return null;
    }

    protected IFunction getFunction(final String name) {
        return FUNCTION_TABLE.get(name);
    }

    private IParsedExpression findVariable(final String name) {
        final IParsedExpression variable = getVariable(name);
        if (variable != null) {
            return variable;
        }
        //redirect to function if possible
        final IFunction function = getFunction(name);
        if (function != null && function.getNumberOfArguments() == 0) {
            return new FunctionCall(function);
        }
        return null;
    }

    protected IParsedExpression getVariable(final String name) {
        final IParsedExpression variable = VARIABLE_TABLE.get(name);
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
