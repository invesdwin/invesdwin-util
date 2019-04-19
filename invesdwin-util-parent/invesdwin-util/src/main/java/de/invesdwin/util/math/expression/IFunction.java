package de.invesdwin.util.math.expression;

import de.invesdwin.util.time.fdate.FDate;

public interface IFunction {

    String getExpressionName();

    /**
     * return a negative number for a variable number of arguments
     */
    int getNumberOfArguments();

    /**
     * The "0 to abs(numberOfArguments)-1" iteration will be used to get the parameter infos. Thus all indexes are
     * positive. See getParameterInfos().
     */
    IFunctionParameterInfo getParameterInfo(int index);

    default IFunctionParameterInfo[] getParameterInfos() {
        final IFunctionParameterInfo[] infos = new IFunctionParameterInfo[Math.abs(getNumberOfArguments())];
        for (int i = 0; i < infos.length; i++) {
            infos[i] = getParameterInfo(i);
        }
        return infos;
    }

    double eval(FDate key, IExpression[] args);

    double eval(int key, IExpression[] args);

    double eval(IExpression[] args);

    /**
     * return true if this function returns the same value for every key on the same arguments, if this is the case and
     * als arguments are constants, then this function can be simplified into a constant expression too.
     */
    boolean isNaturalFunction();

    ExpressionReturnType getReturnType();

    String getName();

    String getDescription();

}
