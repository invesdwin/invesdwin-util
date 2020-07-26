package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.BooleanVariableReference;
import de.invesdwin.util.time.fdate.FDate;

public interface IBooleanVariable extends IVariable {

    boolean getValue(FDate key);

    boolean getValue(int key);

    boolean getValue();

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new BooleanVariableReference(context, this);
    }

}
