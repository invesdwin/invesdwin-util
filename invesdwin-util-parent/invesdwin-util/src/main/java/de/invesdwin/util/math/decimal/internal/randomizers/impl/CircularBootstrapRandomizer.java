package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.blocklength.CircularOptimalBlockLength;
import de.invesdwin.util.math.random.IRandomGenerator;

/**
 * http://www.math.ucsd.edu/~politis/SOFT/PPW/ppw.R
 * 
 * http://www.numericalmethod.com/javadoc/suanshu/com/numericalmethod/suanshu/stats/random/sampler/resampler/bootstrap/block/PattonPolitisWhite2009.html
 * 
 * https://github.com/colintbowers/DependentBootstrap.jl
 */
@ThreadSafe
public class CircularBootstrapRandomizer<E extends ADecimal<E>> implements IDecimalRandomizer<E> {

    private final int blockLength;
    private final List<E> sample;
    private final IDecimalRandomizer<E> delegate;

    public CircularBootstrapRandomizer(final IDecimalAggregate<E> parent) {
        this.sample = parent.values();
        this.blockLength = newOptimalBlockLength(parent);
        Assertions.assertThat(blockLength).isGreaterThanOrEqualTo(1);
        if (blockLength == 1) {
            //blockwise resample makes no sense with block maxResampleIdx 1
            delegate = new BootstrapRandomizer<E>(parent);
        } else {
            delegate = new IDecimalRandomizer<E>() {
                @Override
                public Iterator<E> randomize(final IRandomGenerator random) {
                    return internalResample(random);
                }
            };
        }
    }

    protected int newOptimalBlockLength(final IDecimalAggregate<E> parent) {
        return new CircularOptimalBlockLength<E>(parent).getBlockLength();
    }

    @Override
    public final Iterator<E> randomize(final IRandomGenerator random) {
        return delegate.randomize(random);
    }

    protected int nextBlockLength(final IRandomGenerator random) {
        return blockLength;
    }

    private Iterator<E> internalResample(final IRandomGenerator random) {
        return new Iterator<E>() {
            private final int maxResampleIdx = sample.size();
            private int curResampleIdx = 0;
            private int curBlockIdx = 0;
            private int maxBlockIdx = 0;
            private int curStartIdx = 0;

            @Override
            public boolean hasNext() {
                return curResampleIdx < maxResampleIdx;
            }

            @Override
            public E next() {
                if (curBlockIdx == maxBlockIdx) {
                    initNextBlock(random);
                }
                final int valuesIndex = (curStartIdx + curBlockIdx) % maxResampleIdx;
                final E value = sample.get(valuesIndex);
                curBlockIdx++;
                curResampleIdx++;
                return value;
            }

            private void initNextBlock(final IRandomGenerator random) {
                curStartIdx = random.nextInt(maxResampleIdx);
                final int curBlockLength = nextBlockLength(random);
                if (curResampleIdx + curBlockLength < maxResampleIdx) {
                    maxBlockIdx = curBlockLength;
                } else {
                    maxBlockIdx = maxResampleIdx - curResampleIdx;
                }
                curBlockIdx = 0;
            }

        };
    }

}