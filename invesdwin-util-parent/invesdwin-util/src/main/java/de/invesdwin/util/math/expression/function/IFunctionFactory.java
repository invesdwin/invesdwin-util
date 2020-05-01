package de.invesdwin.util.math.expression.function;

public interface IFunctionFactory {

    String getExpressionName();

    AFunction newFunction(IPreviousKeyFunction previousKeyFunction);

}
