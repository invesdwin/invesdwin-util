package de.invesdwin.util.math.decimal.internal.randomizers.impl;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.bean.tuple.Pair;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

@NotThreadSafe
public class WeightedChunksAscendingRandomizer<E extends ADecimal<E>> implements IDecimalRandomizer<E> {

    private final int sampleSize;
    private final Pair<Double, ? extends List<E>>[] threshold_chunk;

    @SuppressWarnings("unchecked")
    public WeightedChunksAscendingRandomizer(final IDecimalAggregate<E> parent, final int chunkCount) {
        this.sampleSize = parent.values().size();
        final List<? extends List<E>> sampleChunks = Lists.splitIntoPackageCount(parent.values(), chunkCount);
        double chunkWeightsSum = 0D;
        for (double i = 1; i <= chunkCount; i++) {
            chunkWeightsSum += i;
        }
        double chunkProbabilitiesSum = 0D;
        threshold_chunk = new Pair[chunkCount];
        for (int i = 1; i <= chunkCount; i++) {
            final double chunkWeight = i;
            final double chunkProbability = chunkWeight / chunkWeightsSum;
            chunkProbabilitiesSum += chunkProbability;
            final int chunkIndex = i - 1;
            threshold_chunk[chunkIndex] = Pair.of(chunkProbabilitiesSum, sampleChunks.get(chunkIndex));
        }
    }

    @Override
    public Iterator<E> randomize(final RandomGenerator random) {
        return new Iterator<E>() {

            private int resampleIdx = 0;

            @Override
            public boolean hasNext() {
                return resampleIdx < sampleSize;
            }

            @Override
            public E next() {
                final List<E> sampleChunk = getSampleChunk(random);
                final int sourceIdx = random.nextInt(sampleChunk.size());
                resampleIdx++;
                return sampleChunk.get(sourceIdx);
            }

        };
    }

    private List<E> getSampleChunk(final RandomGenerator random) {
        final double chunkThreshold = random.nextDouble();
        for (int i = 0; i < threshold_chunk.length; i++) {
            final Pair<Double, ? extends List<E>> pair = threshold_chunk[i];
            final double threshold = pair.getFirst();
            if (chunkThreshold <= threshold) {
                return pair.getSecond();
            }
        }
        throw new IllegalStateException("No chunk found for threshold: " + chunkThreshold);
    }

}
