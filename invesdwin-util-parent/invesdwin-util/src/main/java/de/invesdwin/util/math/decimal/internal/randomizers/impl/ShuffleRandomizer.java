package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.RandomAdapter;

@ThreadSafe
public class ShuffleRandomizer<E extends ADecimal<E>> implements IDecimalRandomizer<E> {

    private final List<E> sample;

    public ShuffleRandomizer(final IDecimalAggregate<E> parent) {
        this.sample = parent.values();
    }

    @Override
    public Iterator<E> randomize(final IRandomGenerator random) {
        final List<E> sampleCopy = new ArrayList<E>(sample);
        Collections.shuffle(sampleCopy, new RandomAdapter(random));
        return sampleCopy.iterator();
    }

}
