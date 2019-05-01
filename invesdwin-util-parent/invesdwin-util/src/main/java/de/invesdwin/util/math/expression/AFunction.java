package de.invesdwin.util.math.expression;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public abstract class AFunction {

    public static final IFunctionParameterInfo[] NO_PARAMETER_INFOS = new IFunctionParameterInfo[0];
    private IFunctionParameterInfo[] parameterInfos;
    private int numberOfArgumentsRequired;

    public abstract String getExpressionName();

    /**
     * return a negative number for a variable number of arguments
     */
    public final int getNumberOfArgumentsRequired() {
        if (parameterInfos == null) {
            initParameterInfos();
        }
        return numberOfArgumentsRequired;
    }

    public final int getNumberOfArgumentsOptional() {
        return getNumberOfArguments() - getNumberOfArgumentsRequired();
    }

    public abstract int getNumberOfArguments();

    public final IFunctionParameterInfo[] getParameterInfos() {
        if (parameterInfos == null) {
            initParameterInfos();
        }
        return parameterInfos;
    }

    private void initParameterInfos() {
        final int numberOfArguments = getNumberOfArguments();
        if (numberOfArguments == 0) {
            parameterInfos = NO_PARAMETER_INFOS;
        }
        final IFunctionParameterInfo[] infos = new IFunctionParameterInfo[numberOfArguments];
        int infosRequired = 0;
        for (int i = 0; i < infos.length; i++) {
            final IFunctionParameterInfo info = getParameterInfo(i);
            infos[i] = info;
            if (!info.isOptional()) {
                infosRequired++;
            }
        }
        parameterInfos = infos;
        numberOfArgumentsRequired = infosRequired;
    }

    protected abstract IFunctionParameterInfo getParameterInfo(int index);

    public abstract double eval(FDate key, IExpression[] args);

    public abstract double eval(int key, IExpression[] args);

    public abstract double eval(IExpression[] args);

    /**
     * return true if this function returns the same value for every key on the same arguments, if this is the case and
     * als arguments are constants, then this function can be simplified into a constant expression too.
     */
    public abstract boolean isNaturalFunction();

    public abstract ExpressionReturnType getReturnType();

    public abstract String getName();

    public abstract String getDescription();

    public final String[] getDefaultValues() {
        final IFunctionParameterInfo[] parameters = getParameterInfos();
        final String[] defaultValues = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            defaultValues[i] = parameters[i].getDefaultValue();
        }
        return defaultValues;
    }

    public final String getExpressionString(final String[] args) {
        final StringBuilder sb = new StringBuilder(getExpressionName());
        if (args.length > 0) {
            sb.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(args[i]);
            }
            sb.append(")");
        }
        return sb.toString();
    }

}
