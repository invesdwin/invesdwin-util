package de.invesdwin.util.math.expression;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.Characters;
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
import de.invesdwin.util.math.expression.tokenizer.IPosition;
import de.invesdwin.util.math.expression.tokenizer.ParseException;
import de.invesdwin.util.math.expression.variable.IBooleanNullableVariable;
import de.invesdwin.util.math.expression.variable.IBooleanVariable;
import de.invesdwin.util.math.expression.variable.IDoubleVariable;
import de.invesdwin.util.math.expression.variable.IIntegerVariable;
import de.invesdwin.util.math.expression.variable.IVariable;

@NotThreadSafe
public class MultipleExpressionParser implements IExpressionParser {

    private final Map<String, IVariable> variables = ILockCollectionFactory.getInstance(false).newLinkedMap();
    private final String originalExpression;
    private final ExpressionParser fakeExpressionParser;

    public MultipleExpressionParser(final String expression) {
        this.originalExpression = expression;
        this.fakeExpressionParser = newExpressionParser("1");
    }

    @Override
    public IExpression parse() throws ParseException {
        final String[] expressions = Strings.splitPreserveAllTokens(originalExpression, ";");
        for (int i = 0; i < expressions.length - 1; i++) {
            final String expression = expressions[i];
            variableDefinition(expression);
        }
        final IParsedExpression parsed = (IParsedExpression) newExpressionParser(expressions[expressions.length - 1])
                .parse();
        return newParsedExpression(variables, parsed);
    }

    private IParsedExpression newParsedExpression(final Map<String, IVariable> variables,
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

    private void variableDefinition(final String expression) {
        if (expression.isBlank()) {
            return;
        }
        final String variableName = Strings.substringBetween(expression, "var ", "=").trim();
        if (Strings.isBlank(variableName)) {
            throw new ParseException(IPosition.UNKNOWN,
                    "Expected format [var name = expression;] but got: " + expression + ";");
        }
        if (!Strings.isAlphanumeric(variableName) || !Characters.isAsciiAlpha(variableName.charAt(0))) {
            throw new ParseException(IPosition.UNKNOWN,
                    "variableName should be alphanumeric with the first char being alpha: " + variableName);
        }
        final String variableNameLower = variableName.toLowerCase();
        assertVariableNameUnused(variableNameLower);
        final String definition = Strings.substringAfter(expression, "=");
        final IParsedExpression parsedDefinition = (IParsedExpression) newExpressionParser(definition).parse();
        final IVariable variable = newVariable(variableName, parsedDefinition);
        variables.put(variableNameLower, variable);
    }

    private void assertVariableNameUnused(final String variableName) {
        final AVariableReference<?> variable = fakeExpressionParser.getVariable(null, variableName);
        if (variable != null) {
            throw new ParseException(IPosition.UNKNOWN,
                    "var name [" + variableName + "] already used for variable: " + variable.toString());
        }
        final AFunction function = fakeExpressionParser.getFunction(null, variableName);
        if (function != null) {
            throw new ParseException(IPosition.UNKNOWN, "var name [" + variableName + "] already used for function: "
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

    protected ExpressionParser newExpressionParser(final String expression) {
        return new ExpressionParser(expression) {
            @Override
            public AVariableReference<?> getVariable(final String context, final String name) {
                final AVariableReference<?> variable = MultipleExpressionParser.this.getVariable(context, name);
                if (variable != null) {
                    return variable;
                }
                return super.getVariable(context, name);
            }

            @Override
            public AFunction getFunction(final String context, final String name) {
                final AFunction function = MultipleExpressionParser.this.getFunction(context, name);
                if (function != null) {
                    return function;
                }
                return super.getFunction(context, name);
            }

            @Override
            protected Op getCommaOp() {
                return MultipleExpressionParser.this.getCommaOp();
            }

            @Override
            protected IPreviousKeyFunction getPreviousKeyFunction(final String context) {
                return MultipleExpressionParser.this.getPreviousKeyFunction(context);
            }

            @Override
            protected String modifyContext(final String context) {
                return MultipleExpressionParser.this.modifyContext(context);
            }

            @Override
            protected String modifyExpression(final String expression) {
                return MultipleExpressionParser.this.modifyExpression(expression);
            }

            @Override
            protected IParsedExpression simplify(final IParsedExpression expression) {
                return MultipleExpressionParser.this.simplify(expression);
            }
        };
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
