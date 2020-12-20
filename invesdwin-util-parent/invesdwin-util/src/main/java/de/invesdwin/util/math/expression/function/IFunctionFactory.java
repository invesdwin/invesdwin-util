package de.invesdwin.util.math.expression.function;

public interface IFunctionFactory {

    String getExpressionName();

    AFunction newFunction(IPreviousKeyFunction previousKeyFunction);

    static IFunctionFactory valueOf(final AFunction function) {
        return new IFunctionFactory() {

            @Override
            public String getExpressionName() {
                return function.getExpressionName();
            }

            @Override
            public AFunction newFunction(final IPreviousKeyFunction previousKeyFunction) {
                return function;
            }
        };
    }

}
