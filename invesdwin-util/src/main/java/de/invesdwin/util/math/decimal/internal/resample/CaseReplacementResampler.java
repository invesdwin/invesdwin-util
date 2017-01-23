package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

@NotThreadSafe
public class CaseReplacementResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final List<? extends E> sample;
    private final E converter;
    private final RandomGenerator uniformRandom = newUniformRandomGenerator();

    public CaseReplacementResampler(final de.invesdwin.util.math.decimal.internal.DecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.converter = parent.getConverter();
    }

    @Override
    public IDecimalAggregate<E> resample() {
        final List<E> resample = new ArrayList<E>(sample.size());
        for (int resampleIdx = 0; resampleIdx < sample.size(); resampleIdx++) {
            final int sourceIdx = (int) (this.uniformRandom.nextLong() % sample.size());
            resample.add(sample.get(sourceIdx));
        }
        return new de.invesdwin.util.math.decimal.internal.DecimalAggregate<E>(resample, converter);
    }

    protected MersenneTwister newUniformRandomGenerator() {
        return new MersenneTwister();
    }

}
