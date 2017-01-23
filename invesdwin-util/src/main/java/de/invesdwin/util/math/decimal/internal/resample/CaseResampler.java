package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;

@Immutable
public class CaseResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final List<E> sample;
    private final E converter;
    private final RandomGenerator uniformRandom = newUniformRandomGenerator();

    public CaseResampler(final DecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.converter = parent.getConverter();
    }

    @Override
    public IDecimalAggregate<E> resample() {
        final List<E> resample = new ArrayList<E>(sample.size());
        final List<E> sampleCopy = new ArrayList<E>(sample);
        while (!sampleCopy.isEmpty()) {
            final int sourceIdx = (int) (this.uniformRandom.nextLong() % sampleCopy.size());
            resample.add(sampleCopy.remove(sourceIdx));
        }
        return new DecimalAggregate<E>(resample, converter);
    }

    protected MersenneTwister newUniformRandomGenerator() {
        return new MersenneTwister();
    }

}
