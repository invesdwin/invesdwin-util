package de.invesdwin.util.math.decimal.internal.resample;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;

@ThreadSafe
public class CaseReplacementResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final List<? extends E> sample;

    public CaseReplacementResampler(final DecimalAggregate<E> parent) {
        this.sample = parent.values();
    }

    @Override
    public Iterator<E> resample(final RandomGenerator random) {
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
