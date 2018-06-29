package de.invesdwin.util.math.decimal.internal.randomizers;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.randomizers.IDecimalAggregateRandomizers;

@Immutable
public final class DummyDecimalAggregateRandomizers<E extends ADecimal<E>> implements IDecimalAggregateRandomizers<E> {

    @SuppressWarnings("rawtypes")
    public static final DummyDecimalAggregateRandomizers INSTANCE = new DummyDecimalAggregateRandomizers();

    private DummyDecimalAggregateRandomizers() {}

    @Override
    public Iterator<E> shuffle(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> bootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> circularBlockBootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> stationaryBootstrap(final RandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> weightedChunksDescending(final RandomGenerator random, final int chunkCount) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> weightedChunksAscending(final RandomGenerator random, final int chunkCount) {
        return EmptyCloseableIterator.getInstance();
    }

}
