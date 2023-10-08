package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ADecimalNumber<E extends ADecimalNumber<E>> extends Number implements Comparable<Object> {

    public abstract E fromDefaultValue(double value);

    public abstract double getDefaultValue();

}
