package de.invesdwin.util.math.expression.variable;

import de.invesdwin.util.math.expression.eval.variable.AVariableReference;
import de.invesdwin.util.math.expression.eval.variable.BooleanNullableVariableReference;
import de.invesdwin.util.time.fdate.IFDateProvider;

public interface IBooleanNullableVariable extends IVariable {

    Boolean getValue(IFDateProvider key);

    Boolean getValue(int key);

    Boolean getValue();

    @Override
    default AVariableReference<?> newReference(final String context) {
        return new BooleanNullableVariableReference(context, this);
    }

}
