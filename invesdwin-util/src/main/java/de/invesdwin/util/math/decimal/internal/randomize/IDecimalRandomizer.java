package de.invesdwin.util.math.decimal.internal.randomize;

import java.util.Iterator;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;

public interface IDecimalRandomizer<E extends ADecimal<E>> {

    Iterator<E> randomize(RandomGenerator random);

}
