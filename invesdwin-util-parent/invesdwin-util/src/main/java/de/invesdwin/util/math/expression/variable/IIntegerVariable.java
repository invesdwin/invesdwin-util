package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.IntegerVariableReference;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IIntegerVariable extends IVariable {

    int getValue(IFDateProvider key);

    int getValue(int key);

    int getValue();

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new IntegerVariableReference(context, this);
    }

}
