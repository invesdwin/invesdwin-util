package de.invesdwin.util.math.expression.variable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class Variable implements IVariable {

    private double value = 0D;
    private final String name;

    public Variable(final String name) {
        this.name = name;
    }

    public void setValue(final double value) {
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
    public String toString() {
        return name + ": " + value;
    }

    @Override
    public String getName() {
        return name;
    }

}
