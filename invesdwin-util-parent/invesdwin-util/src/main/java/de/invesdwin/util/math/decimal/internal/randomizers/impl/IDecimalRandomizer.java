package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import java.util.Iterator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.random.IRandomGenerator;

public interface IDecimalRandomizer<E extends ADecimal<E>> {

    Iterator<E> randomize(IRandomGenerator random);

}
