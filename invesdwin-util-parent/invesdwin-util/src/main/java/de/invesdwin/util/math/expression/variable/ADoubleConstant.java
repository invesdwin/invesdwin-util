package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public abstract class ADoubleConstant extends AConstant implements IDoubleVariable {

    private final double value;

    public ADoubleConstant(final double value) {
        this.value = value;
    }

    @Override
    public final double getValue() {
        return value;
    }

    @Override
    public final double getValue(final FDate key) {
        return value;
    }

    @Override
    public final double getValue(final int key) {
        return value;
    }

    @Override
    public String toString() {
        return getExpressionName() + ": " + getValue();
    }

}
