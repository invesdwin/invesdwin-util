package de.invesdwin.util.math.expression.function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.expression.ExpressionReturnType;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.IFunctionParameterInfo;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public abstract class AFunction {

    public static final IFunctionParameterInfo[] NO_PARAMETER_INFOS = new IFunctionParameterInfo[0];
    private IFunctionParameterInfo[] parameterInfos;
    private int numberOfArgumentsRequired;
    private int numberOfArgumentsOptional;

    public abstract String getExpressionName();

    /**
     * return a negative number for a variable number of arguments
     */
    public final int getNumberOfArgumentsMin() {
        if (parameterInfos == null) {
            initParameterInfos();
        }
        return numberOfArgumentsRequired;
    }

    public final int getNumberOfArgumentsMax() {
        if (parameterInfos == null) {
            initParameterInfos();
        }
        if (isVarArgs()) {
            return -1;
        } else {
            return numberOfArgumentsRequired + numberOfArgumentsOptional;
        }
    }

    public final boolean isVarArgs() {
        return numberOfArgumentsOptional < 0;
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
        int infosOptional = 0;
        for (int i = 0; i < infos.length; i++) {
            final IFunctionParameterInfo info = getParameterInfo(i);
            infos[i] = info;
            if (!info.isOptional()) {
                infosRequired++;
            } else if (infosOptional >= 0) {
                infosOptional++;
            }
            if (info.isVarArgs()) {
                infosOptional = -1;
            }
        }
        parameterInfos = infos;
        numberOfArgumentsRequired = infosRequired;
        numberOfArgumentsOptional = infosOptional;
    }

    protected abstract IFunctionParameterInfo getParameterInfo(int index);

    public abstract double eval(FDate key, IExpression[] args);

    public abstract double eval(int key, IExpression[] args);

    public abstract double eval(IExpression[] args);

    /**
     * return true if this function returns the same value for every key on the same arguments, if this is the case and
     * all arguments are constants, then this function can be simplified into a constant expression too. You don't have
     * to check the args yourself for being constant since this is being done on the outside, maybe only the length or
     * type of the arguments influence this function (when having optional arguments that make it natural).
     */
    public abstract boolean isNaturalFunction(IExpression[] args);

    public abstract ExpressionReturnType getReturnType();

    public abstract String getName();

    public abstract String getDescription();

    public final String[] getDefaultValues() {
        final IFunctionParameterInfo[] parameters = getParameterInfos();
        final String[] defaultValues = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final IFunctionParameterInfo parameter = parameters[i];
            final String defaultValue = parameter.getDefaultValue();
            if (Strings.isNotBlank(defaultValue)) {
                defaultValues[i] = defaultValue;
            } else {
                defaultValues[i] = parameter.getExpressionName();
            }
        }
        return defaultValues;
    }

    public final String getExpressionString(final String[] args) {
        final StringBuilder sb = new StringBuilder(getExpressionName());
        if (args.length > 0) {
            sb.append("(");
            for (int i = 0; i < args.length; i++) {
                final String arg = args[i];
                if (arg != null) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(arg);
                }
            }
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * Return true if values are only availble point in time without history. E.g. dependant on active orders and thus
     * should be persisted for charts.
     */
    public abstract boolean shouldPersist();

    /**
     * Return true if this expression can be drawn. This might be false for command expressions that always return NaN.
     * In that case the children might be drawn.
     */
    public abstract boolean shouldDraw();

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(getExpressionName()).toString();
    }

}
