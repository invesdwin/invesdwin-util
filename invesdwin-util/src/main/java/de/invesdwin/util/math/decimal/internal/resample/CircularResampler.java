package de.invesdwin.util.math.decimal.internal.resample;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.DecimalAggregate;
import de.invesdwin.util.math.decimal.internal.resample.blocklength.CircularOptimalBlockLength;

/**
 * http://www.math.ucsd.edu/~politis/SOFT/PPW/ppw.R
 * 
 * http://www.numericalmethod.com/javadoc/suanshu/com/numericalmethod/suanshu/stats/random/sampler/resampler/bootstrap/block/PattonPolitisWhite2009.html
 * 
 * https://github.com/colintbowers/DependentBootstrap.jl
 */
@ThreadSafe
public class CircularResampler<E extends ADecimal<E>> implements IDecimalResampler<E> {

    private final int blockLength;
    private final List<E> sample;
    private final E converter;
    private final IDecimalResampler<E> delegate;

    public CircularResampler(final DecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.converter = parent.getConverter();
        this.blockLength = newOptimalBlockLength(parent);
        Assertions.assertThat(blockLength).isGreaterThanOrEqualTo(1);
        if (blockLength == 1) {
            //blockwise resample makes no sense with block length 1
            delegate = new CaseReplacementResampler<E>(parent);
        } else {
            delegate = new IDecimalResampler<E>() {
                @Override
                public IDecimalAggregate<E> resample(final RandomGenerator random) {
                    return internalResample(random);
                }
            };
        }
    }

    protected int newOptimalBlockLength(final IDecimalAggregate<E> parent) {
        return new CircularOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    public final IDecimalAggregate<E> resample(final RandomGenerator random) {
        return delegate.resample(random);
    }

    protected int nextBlockLength(final RandomGenerator random) {
        return blockLength;
    }

    private IDecimalAggregate<E> internalResample(final RandomGenerator random) {
        int curBlockLength;
        final int length = sample.size();
        final List<E> resample = new ArrayList<E>(length);
        for (int resampleIdx = 0; resampleIdx < length; resampleIdx += curBlockLength) {
            final int startIdx = random.nextInt(length);
            curBlockLength = nextBlockLength(random);
            final int maxBlockIdx;
            if (resampleIdx + curBlockLength < length) {
                maxBlockIdx = curBlockLength;
            } else {
                maxBlockIdx = length - resampleIdx;
            }
            for (int blockIdx = 0; blockIdx < maxBlockIdx; blockIdx++) {
                final int valuesIndex = (startIdx + blockIdx) % length;
                resample.add(sample.get(valuesIndex));
            }
        }
        return new DecimalAggregate<E>(resample, converter);
    }

}