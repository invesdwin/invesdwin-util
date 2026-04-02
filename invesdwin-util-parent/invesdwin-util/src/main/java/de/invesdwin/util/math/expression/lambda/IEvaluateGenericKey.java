package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateGenericKey<E> {

    /**
     * evaluates the expression using the given long key
     */
    E evaluateGeneric(long key);

}
