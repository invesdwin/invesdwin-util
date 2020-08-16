package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.DoubleVariableReference;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IDoubleVariable extends IVariable {

    double getValue(IFDateProvider key);

    double getValue(int key);

    double getValue();

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new DoubleVariableReference(context, this);
    }

}
