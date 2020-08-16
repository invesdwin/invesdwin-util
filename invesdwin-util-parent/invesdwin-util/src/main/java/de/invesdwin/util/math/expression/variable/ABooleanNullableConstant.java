package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.IFDateProvider;

@Immutable
public abstract class ABooleanNullableConstant extends AConstant implements IBooleanNullableVariable {

    private final Boolean value;

    public ABooleanNullableConstant(final Boolean value) {
        this.value = value;
    }

    @Override
    public final Boolean getValue() {
        return value;
    }

    @Override
    public final Boolean getValue(final IFDateProvider key) {
        return value;
    }

    @Override
    public final Boolean getValue(final int key) {
        return value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + getValue();
    }

}
