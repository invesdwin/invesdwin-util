package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public class Constant implements IVariable {

    private final String name;
    private final double value;

    public Constant(final String name, final double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public double getValue(final FDate key) {
        return value;
    }

    @Override
    public double getValue(final int key) {
        return value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }

}
