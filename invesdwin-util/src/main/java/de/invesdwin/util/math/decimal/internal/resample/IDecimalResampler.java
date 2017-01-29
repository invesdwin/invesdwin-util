package de.invesdwin.util.math.decimal.internal.resample;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

public interface IDecimalResampler<E extends ADecimal<E>> {

    IDecimalAggregate<E> resample(RandomGenerator random);

}
