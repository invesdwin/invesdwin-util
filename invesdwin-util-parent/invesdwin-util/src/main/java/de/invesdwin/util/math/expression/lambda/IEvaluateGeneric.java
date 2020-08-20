package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateGeneric<E> {

    /**
     * evaluates the expression using the current available time/int key
     */
    E evaluateGeneric();

}
