package de.invesdwin.util.math.decimal.internal.randomizers;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.randomizers.IDecimalAggregateRandomizers;
import de.invesdwin.util.math.random.IRandomGenerator;

@Immutable
public final class DummyDecimalAggregateRandomizers<E extends ADecimal<E>> implements IDecimalAggregateRandomizers<E> {

    @SuppressWarnings("rawtypes")
    public static final DummyDecimalAggregateRandomizers INSTANCE = new DummyDecimalAggregateRandomizers();

    private DummyDecimalAggregateRandomizers() {
    }

    @Override
    public Iterator<E> shuffle(final IRandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> bootstrap(final IRandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> circularBlockBootstrap(final IRandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> stationaryBootstrap(final IRandomGenerator random) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> weightedChunksDescending(final IRandomGenerator random, final int chunkCount) {
        return EmptyCloseableIterator.getInstance();
    }

    @Override
    public Iterator<E> weightedChunksAscending(final IRandomGenerator random, final int chunkCount) {
        return EmptyCloseableIterator.getInstance();
    }

}
