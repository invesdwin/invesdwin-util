package de.invesdwin.util.math.expression;

public interface IFunctionParameterInfo {

    String getExpressionName();

    String getName();

    String getDescription();

    String getType();

    String getDefaultValue();

    boolean isOptional();

}
