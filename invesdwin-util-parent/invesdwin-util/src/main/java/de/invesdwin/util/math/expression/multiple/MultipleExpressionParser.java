package de.invesdwin.util.math.expression.multiple;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.expression.ExpressionParser;
import de.invesdwin.util.math.expression.ExpressionType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IExpressionParser;
import de.invesdwin.util.math.expression.delegate.ADelegateParsedExpression;
import de.invesdwin.util.math.expression.eval.IParsedExpression;
import de.invesdwin.util.math.expression.eval.operation.Op;
import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.function.AFunction;
import de.invesdwin.util.math.expression.function.IPreviousKeyFunction;
import de.invesdwin.util.math.expression.lambda.IEvaluateBoolean;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullable;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateBooleanNullableKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateDouble;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateDoubleKey;
import de.invesdwin.util.math.expression.lambda.IEvaluateInteger;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerFDate;
import de.invesdwin.util.math.expression.lambda.IEvaluateIntegerKey;
import de.invesdwin.util.math.expression.multiple.pool.NestedExpressionParserObjectPool;
import de.invesdwin.util.math.expression.tokenizer.IPosition;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.math.expression.tokenizer.Token;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;
import de.invesdwin.util.math.expression.tokenizer.pool.TokenizerObjectPool;
import de.invesdwin.util.math.expression.variable.IBooleanNullableVariable;
import de.invesdwin.util.math.expression.variable.IBooleanVariable;
import de.invesdwin.util.math.expression.variable.IDoubleVariable;
import de.invesdwin.util.math.expression.variable.IIntegerVariable;
import de.invesdwin.util.math.expression.variable.IVariable;

@NotThreadSafe
public class MultipleExpressionParser implements IExpressionParser {

    private static final NestedExpressionParserObjectPool FAKE_PARSER_POOL = new NestedExpressionParserObjectPool("1");
    private final Map<String, IVariable> variables = ILockCollectionFactory.getInstance(false).newLinkedMap();
    private Tokenizer tokenizer;
    private NestedExpressionParser fakeParser;
    private final String originalExpression;

    public MultipleExpressionParser(final String expression) {
        if (expression == null) {
            throw new NullPointerException("expression should not be null");
        }
        this.originalExpression = modifyExpression(expression);
    }

    @Override
    public IExpression parse() throws ParseException {
        try {
            fakeParser = FAKE_PARSER_POOL.borrowObject();
            fakeParser.setParent(this);
            tokenizer = TokenizerObjectPool.INSTANCE.borrowObject();
            tokenizer.init(originalExpression, isSemicolonAllowed());
            final IParsedExpression result = expression();
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
            TokenizerObjectPool.INSTANCE.returnObject(tokenizer);
            tokenizer = null;
            fakeParser.setParent(null);
            FAKE_PARSER_POOL.returnObject(fakeParser);
            fakeParser = null;
        }
    }

    private boolean isSemicolonAllowed() {
        return true;
    }

    private IParsedExpression expression() {
        if (tokenizer.current().getContents().equalsIgnoreCase("var")) {
            tokenizer.consume();
            variableDefinition();
            return expression();
        } else {
            return finalExpression();
        }
    }

    private IParsedExpression finalExpression() {
        final Token positionBefore = tokenizer.current();
        if (positionBefore.isEnd()) {
            throw new ParseException(positionBefore,
                    TextDescription.format("Unexpected token: '%s'", positionBefore.getSource()));
        }
        final String expression = collectExpression();
        try {
            final IParsedExpression parsed = (IParsedExpression) newNestedParser(expression).parse();
            return newParsedExpression(variables, parsed);
        } catch (final ParseException e) {
            throw new ParseException(newNestedPosition(positionBefore, e), e.getMessage(), e);
        }
    }

    private IPosition newNestedPosition(final IPosition positionBefore, final ParseException e) {
        return new IPosition() {
            @Override
            public int getLineOffset() {
                return positionBefore.getLineOffset() + e.getPosition().getLineOffset();
            }

            @Override
            public int getLength() {
                return e.getPosition().getLength();
            }

            @Override
            public int getIndexOffset() {
                return positionBefore.getIndexOffset() + e.getPosition().getIndexOffset();
            }

            @Override
            public int getColumnOffset() {
                return positionBefore.getColumnOffset() + e.getPosition().getColumnOffset();
            }
        };
    }

    private static IParsedExpression newParsedExpression(final Map<String, IVariable> variables,
            final IParsedExpression parsed) {
        return new ADelegateParsedExpression() {
            @Override
            protected IParsedExpression getDelegate() {
                return parsed;
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                for (final IVariable variable : variables.values()) {
                    sb.append(variable);
                    sb.append(" ");
                }
                sb.append(parsed);
                return sb.toString();
            }
        };
    }

    private void variableDefinition() {
        final Token variableNameToken = tokenizer.consume();
        final String variableName = variableNameToken.getContents();
        final String variableNameLower = variableName.toLowerCase();
        assertVariableName(variableName, variableNameLower, variableNameToken);
        tokenizer.consumeExpectedSymbol("=");
        final IPosition positionBefore = tokenizer.current();
        final String definition = collectExpression();
        try {
            final IParsedExpression parsedDefinition = (IParsedExpression) newNestedParser(definition).parse();
            final IVariable variable = newVariable(variableName, parsedDefinition);
            variables.put(variableNameLower, variable);
        } catch (final ParseException e) {
            throw new ParseException(newNestedPosition(positionBefore, e), e.getMessage(), e);
        }
    }

    private String collectExpression() {
        final StringBuilder sb = new StringBuilder();
        int consumed = 0;
        for (int i = tokenizer.current().getIndexOffset(); i < originalExpression.length(); i++) {
            consumed++;
            if (originalExpression.charAt(i) == ';') {
                break;
            }
            sb.append(originalExpression.charAt(i));
        }
        tokenizer.setPostition(tokenizer.current());
        tokenizer.skipCharacters(consumed);
        return sb.toString();
    }

    private void assertVariableName(final String variableName, final String variableNameLower,
            final Token variableNameToken) {
        if (!Strings.isAlphanumeric(variableNameLower) || !Characters.isAsciiAlpha(variableNameLower.charAt(0))) {
            throw new ParseException(variableNameToken,
                    "variableName should be alphanumeric with the first char being alpha: " + variableName);
        }
        final AVariableReference<?> variable = fakeParser.getVariable(null, variableNameLower);
        if (variable != null) {
            throw new ParseException(variableNameToken,
                    "var name [" + variableName + "] already used for variable: " + variable.toString());
        }
        final AFunction function = fakeParser.getFunction(null, variableNameLower);
        if (function != null) {
            throw new ParseException(variableNameToken, "var name [" + variableName + "] already used for function: "
                    + function.getExpressionString(function.getDefaultValues()));
        }
    }

    private static IVariable newVariable(final String name, final IParsedExpression definition) {
        final ExpressionType type = definition.getType();
        switch (type) {
        case Double:
            return new IDoubleVariable() {

                @Override
                public boolean isConstant() {
                    return definition.isConstant();
                }

                @Override
                public Object getProperty(final String property) {
                    return definition.getProperty(property);
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getExpressionName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public IEvaluateDoubleKey newEvaluateDoubleKey(final String context) {
                    return definition.newEvaluateDoubleKey();
                }

                @Override
                public IEvaluateDoubleFDate newEvaluateDoubleFDate(final String context) {
                    return definition.newEvaluateDoubleFDate();
                }

                @Override
                public IEvaluateDouble newEvaluateDouble(final String context) {
                    return definition.newEvaluateDouble();
                }

                @Override
                public String toString() {
                    return newVarStr(getExpressionName(), definition);
                }
            };
        case Integer:
            return new IIntegerVariable() {

                @Override
                public boolean isConstant() {
                    return definition.isConstant();
                }

                @Override
                public Object getProperty(final String property) {
                    return definition.getProperty(property);
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getExpressionName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public IEvaluateIntegerKey newEvaluateIntegerKey(final String context) {
                    return definition.newEvaluateIntegerKey();
                }

                @Override
                public IEvaluateIntegerFDate newEvaluateIntegerFDate(final String context) {
                    return definition.newEvaluateIntegerFDate();
                }

                @Override
                public IEvaluateInteger newEvaluateInteger(final String context) {
                    return definition.newEvaluateInteger();
                }

                @Override
                public String toString() {
                    return newVarStr(getExpressionName(), definition);
                }
            };

        case BooleanNullable:
            return new IBooleanNullableVariable() {

                @Override
                public boolean isConstant() {
                    return definition.isConstant();
                }

                @Override
                public Object getProperty(final String property) {
                    return definition.getProperty(property);
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getExpressionName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public IEvaluateBooleanNullableKey newEvaluateBooleanNullableKey(final String context) {
                    return definition.newEvaluateBooleanNullableKey();
                }

                @Override
                public IEvaluateBooleanNullableFDate newEvaluateBooleanNullableFDate(final String context) {
                    return definition.newEvaluateBooleanNullableFDate();
                }

                @Override
                public IEvaluateBooleanNullable newEvaluateBooleanNullable(final String context) {
                    return definition.newEvaluateBooleanNullable();
                }

                @Override
                public String toString() {
                    return newVarStr(getExpressionName(), definition);
                }
            };
        case Boolean:
            return new IBooleanVariable() {

                @Override
                public boolean isConstant() {
                    return definition.isConstant();
                }

                @Override
                public Object getProperty(final String property) {
                    return definition.getProperty(property);
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getExpressionName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public IEvaluateBooleanKey newEvaluateBooleanKey(final String context) {
                    return definition.newEvaluateBooleanKey();
                }

                @Override
                public IEvaluateBooleanFDate newEvaluateBooleanFDate(final String context) {
                    return definition.newEvaluateBooleanFDate();
                }

                @Override
                public IEvaluateBoolean newEvaluateBoolean(final String context) {
                    return definition.newEvaluateBoolean();
                }

                @Override
                public String toString() {
                    return newVarStr(getExpressionName(), definition);
                }

            };
        default:
            throw UnknownArgumentException.newInstance(ExpressionType.class, type);
        }
    }

    private static String newVarStr(final String expressionName, final IParsedExpression definition) {
        return "var " + expressionName + " = " + definition.toString() + ";";
    }

    private NestedExpressionParser newNestedParser(final String expression) {
        final NestedExpressionParser parser = new NestedExpressionParser(expression);
        parser.setParent(this);
        return parser;
    }

    protected String modifyExpression(final String expression) {
        return expression;
    }

    protected String modifyContext(final String context) {
        return context;
    }

    protected Op getCommaOp() {
        return ExpressionParser.DEFAULT_COMMA_OP;
    }

    protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
        return null;
    }

    protected AVariableReference<?> getVariable(final String context, final String name) {
        final IVariable variable = variables.get(name);
        if (variable != null) {
            return variable.newReference(context);
        }
        return null;
    }

    protected AFunction getFunction(final String context, final String name) {
        return null;
    }

    protected IParsedExpression simplify(final IParsedExpression expression) {
        return expression.simplify();
    }

}
