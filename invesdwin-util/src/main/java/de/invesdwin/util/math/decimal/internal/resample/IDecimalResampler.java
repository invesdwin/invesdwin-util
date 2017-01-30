package de.invesdwin.util.math.decimal.internal.resample;

import java.util.Iterator;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;

public interface IDecimalResampler<E extends ADecimal<E>> {

    Iterator<E> resample(RandomGenerator random);

}
