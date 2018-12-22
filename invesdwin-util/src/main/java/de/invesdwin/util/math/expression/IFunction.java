package de.invesdwin.util.math.expression;

import de.invesdwin.util.time.fdate.FDate;

public interface IFunction {

    String getName();

    /**
     * return a negative number for a variable number of arguments
     */
    int getNumberOfArguments();

    double eval(FDate key, IExpression[] args);

    double eval(int key, IExpression[] args);

    double eval(IExpression[] args);

    /**
     * return true if this function returns the same value for every key on the same arguments, if this is the case and
     * als arguments are constants, then this function can be simplified into a constant expression too.
     */
    boolean isNaturalFunction();

}
