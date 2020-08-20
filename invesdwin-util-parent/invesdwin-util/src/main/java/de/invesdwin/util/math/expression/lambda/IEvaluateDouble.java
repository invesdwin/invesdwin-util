package de.invesdwin.util.math.expression.lambda;

@FunctionalInterface
public interface IEvaluateDouble {

    /**
     * evaluates the expression using the current available time/int key
     */
    double evaluateDouble();

}
