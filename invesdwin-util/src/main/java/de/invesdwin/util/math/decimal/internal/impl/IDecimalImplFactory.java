package de.invesdwin.util.math.decimal.internal.impl;

public interface IDecimalImplFactory<E extends ADecimalImpl<E, ?>> {

    E valueOf(Number value);

    E valueOf(Double value);

    E valueOf(String value);

}
