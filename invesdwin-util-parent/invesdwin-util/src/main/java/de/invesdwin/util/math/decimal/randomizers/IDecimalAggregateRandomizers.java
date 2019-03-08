package de.invesdwin.util.math.decimal.randomizers;

import java.util.Iterator;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;

public interface IDecimalAggregateRandomizers<E extends ADecimal<E>> {

    /**
     * Randomized the values without replacement
     */
    Iterator<E> shuffle(RandomGenerator random);

    /**
     * Randomized the values with replacement, thus can draw the same values multiple times
     */
    Iterator<E> bootstrap(RandomGenerator random);

    /**
     * Randomize the values with replacement blockwise (for dependent data). Since the random generator is used less
     * often here (only per block), the actual performance here is better than that of the normal bootstrap.
     */
    Iterator<E> circularBlockBootstrap(RandomGenerator random);

    /**
     * Randomize the values with replacement blockwise with randomized block length (for time series). Since the random
     * generator is used less often here (only per block), the actual performance here is better than that of the normal
     * bootstrap.
     */
    Iterator<E> stationaryBootstrap(RandomGenerator random);

    /**
     * Divides the given values into chunks (e.g. 1000 values in 4 chunks results in each chunk having 250 values).
     * These chunks will get a descending weight for being chosen as the basis for the next sample being taken (e.g.
     * with 40% probability it is chunk1, with 30% probability it is chunk2, with 20% probability it is chunk3 and with
     * 10% probability it is chunk4). The probabilities of the chunks with varying chunkCount is proportional to the
     * given example.
     */
    Iterator<E> weightedChunksDescending(RandomGenerator random, int chunkCount);

    /**
     * Divides the given values into chunks (e.g. 1000 values in 4 chunks results in each chunk having 250 values).
     * These chunks will get an ascending weight for being chosen as the basis for the next sample being taken (e.g.
     * with 10% probability it is chunk1, with 20% probability it is chunk2, with 30% probability it is chunk3 and with
     * 40% probability it is chunk4). The probabilities of the chunks with varying chunkCount is proportional to the
     * given example.
     */
    Iterator<E> weightedChunksAscending(RandomGenerator random, int chunkCount);

}
