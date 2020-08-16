package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public abstract class ABooleanConstant extends AConstant implements IBooleanVariable {

    private final boolean value;

    public ABooleanConstant(final boolean value) {
        this.value = value;
    }

    @Override
    public final boolean getValue() {
        return value;
    }

    @Override
    public final boolean getValue(final IFDateProvider key) {
        return value;
    }

    @Override
    public final boolean getValue(final int key) {
        return value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + getValue();
    }

}
