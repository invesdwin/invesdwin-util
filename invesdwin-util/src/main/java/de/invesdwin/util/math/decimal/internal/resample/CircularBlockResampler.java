package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.resample.blocklength.OptimalBlockLength;

/**
 * http://www.math.ucsd.edu/~politis/SOFT/PPW/ppw.R
 * 
 * http://www.numericalmethod.com/javadoc/suanshu/com/numericalmethod/suanshu/stats/random/sampler/resampler/bootstrap/block/PattonPolitisWhite2009.html
 * 
 * https://github.com/colintbowers/DependentBootstrap.jl
 */
@NotThreadSafe
public class CircularBlockResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final long blockLength;
    private final List<E> sample;
    private final E converter;
    private final RandomGenerator uniformRandom = newUniformRandomGenerator();

    public CircularBlockResampler(final de.invesdwin.util.math.decimal.internal.DecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.converter = parent.getConverter();
        this.blockLength = newBlockLength(parent);
        Assertions.assertThat(blockLength).isGreaterThan(1);
    }

    protected long newBlockLength(final IDecimalAggregate<E> parent) {
        return new OptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    public IDecimalAggregate<E> resample() {
        int curBlockLength;
        final int length = sample.size();
        final List<E> resample = new ArrayList<E>(length);
        for (int i = 0; i < length; i += curBlockLength) {
            final int randomI = (int) (uniformRandom.nextLong() % length);
            curBlockLength = this.newBlockLength();
            final int jMax = i + curBlockLength < length ? curBlockLength : length - i;
            for (int j = 0; j < jMax; ++j) {
                final int valuesIndex = (randomI + j) % length;
                resample.add(sample.get(valuesIndex));
            }
        }
        return new de.invesdwin.util.math.decimal.internal.DecimalAggregate<E>(resample, converter);
    }

    protected int newBlockLength() {
        return (int) blockLength;
    }

    protected RandomGenerator newUniformRandomGenerator() {
        return new MersenneTwister();
    }

}