package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AConstant implements IVariable {

    @Override
    public boolean isConstant() {
        return true;
    }

}
