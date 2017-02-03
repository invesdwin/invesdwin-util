package de.invesdwin.util.math.decimal.internal;

import java.util.Iterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.internal.randomize.BootstrapRandomizer;
import de.invesdwin.util.math.decimal.internal.randomize.CircularBootstrapRandomizer;
import de.invesdwin.util.math.decimal.internal.randomize.ShuffleRandomizer;
import de.invesdwin.util.math.decimal.internal.randomize.StationaryBootstrapRandomizer;

@ThreadSafe
public class DecimalAggregateRandomizers<E extends ADecimal<E>> {

    private final DecimalAggregate<E> parent;

    @GuardedBy("this")
    private CircularBootstrapRandomizer<E> circularBootstrapRandomizer;
    @GuardedBy("this")
    private StationaryBootstrapRandomizer<E> stationaryBootstrapRandomizer;

    public DecimalAggregateRandomizers(final DecimalAggregate<E> parent) {
        this.parent = parent;
    }

    public Iterator<E> randomizeShuffle(final RandomGenerator random) {
        return new ShuffleRandomizer<E>(parent).randomize(random);
    }

    public Iterator<E> randomizeBootstrap(final RandomGenerator random) {
        return new BootstrapRandomizer<E>(parent).randomize(random);
    }

    public Iterator<E> randomizeCircularBootstrap(final RandomGenerator random) {
        return getCircularBootstrapRandomizer().randomize(random);
    }

    private synchronized CircularBootstrapRandomizer<E> getCircularBootstrapRandomizer() {
        if (circularBootstrapRandomizer == null) {
            circularBootstrapRandomizer = new CircularBootstrapRandomizer<E>(parent);
        }
        return circularBootstrapRandomizer;
    }

    public Iterator<E> randomizeStationaryBootstrap(final RandomGenerator random) {
        return getStationaryBootstrapRandomizer().randomize(random);
    }

    private synchronized StationaryBootstrapRandomizer<E> getStationaryBootstrapRandomizer() {
        if (stationaryBootstrapRandomizer == null) {
            stationaryBootstrapRandomizer = new StationaryBootstrapRandomizer<E>(parent);
        }
        return stationaryBootstrapRandomizer;
    }

}
