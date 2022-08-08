package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.random.IRandomGenerator;

@ThreadSafe
public class BootstrapRandomizer<E extends ADecimal<E>> implements IDecimalRandomizer<E> {

    private final List<? extends E> sample;

    public BootstrapRandomizer(final IDecimalAggregate<E> parent) {
        this.sample = parent.values();
    }

    @Override
    public Iterator<E> randomize(final IRandomGenerator random) {
        return new Iterator<E>() {

            private final int size = sample.size();
            private int resampleIdx = 0;

            @Override
            public boolean hasNext() {
                return resampleIdx < size;
            }

            @Override
            public E next() {
                final int sourceIdx = random.nextInt(size);
                resampleIdx++;
                return sample.get(sourceIdx);
            }

        };
    }

}
