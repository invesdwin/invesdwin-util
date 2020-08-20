package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateGenericKey<E> {

    /**
     * evaluates the expression using the current int key
     */
    E evaluateGeneric(int key);

}
