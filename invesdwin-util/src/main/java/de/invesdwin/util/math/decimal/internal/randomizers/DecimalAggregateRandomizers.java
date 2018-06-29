package de.invesdwin.util.math.decimal.internal.randomizers;

import java.util.Iterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.BootstrapRandomizer;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.CircularBootstrapRandomizer;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.ShuffleRandomizer;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.StationaryBootstrapRandomizer;
import de.invesdwin.util.math.decimal.internal.randomizers.impl.WeightedChunksAscendingRandomizer;
import de.invesdwin.util.math.decimal.randomizers.IDecimalAggregateRandomizers;

@ThreadSafe
public class DecimalAggregateRandomizers<E extends ADecimal<E>> implements IDecimalAggregateRandomizers<E> {

    private final IDecimalAggregate<E> parent;

    @GuardedBy("this")
    private CircularBootstrapRandomizer<E> circularBootstrapRandomizer;
    @GuardedBy("this")
    private StationaryBootstrapRandomizer<E> stationaryBootstrapRandomizer;

    public DecimalAggregateRandomizers(final IDecimalAggregate<E> parent) {
        this.parent = parent;
    }

    @Override
    public Iterator<E> shuffle(final RandomGenerator random) {
        return new ShuffleRandomizer<E>(parent).randomize(random);
    }

    @Override
    public Iterator<E> weightedChunksAscending(final RandomGenerator random, final int chunkCount) {
        return new WeightedChunksAscendingRandomizer<E>(parent, chunkCount).randomize(random);
    }

    @Override
    public Iterator<E> weightedChunksDescending(final RandomGenerator random, final int chunkCount) {
        return new WeightedChunksAscendingRandomizer<E>(parent.reverse(), chunkCount).randomize(random);
    }

    @Override
    public Iterator<E> bootstrap(final RandomGenerator random) {
        return new BootstrapRandomizer<E>(parent).randomize(random);
    }

    @Override
    public Iterator<E> circularBlockBootstrap(final RandomGenerator random) {
        return getCircularBootstrapRandomizer().randomize(random);
    }

    private synchronized CircularBootstrapRandomizer<E> getCircularBootstrapRandomizer() {
        if (circularBootstrapRandomizer == null) {
            circularBootstrapRandomizer = new CircularBootstrapRandomizer<E>(parent);
        }
        return circularBootstrapRandomizer;
    }

    @Override
    public Iterator<E> stationaryBootstrap(final RandomGenerator random) {
        return getStationaryBootstrapRandomizer().randomize(random);
    }

    private synchronized StationaryBootstrapRandomizer<E> getStationaryBootstrapRandomizer() {
        if (stationaryBootstrapRandomizer == null) {
            stationaryBootstrapRandomizer = new StationaryBootstrapRandomizer<E>(parent);
        }
        return stationaryBootstrapRandomizer;
    }

}
