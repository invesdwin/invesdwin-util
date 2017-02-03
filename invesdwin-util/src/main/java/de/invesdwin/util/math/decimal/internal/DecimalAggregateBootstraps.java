package de.invesdwin.util.math.decimal.internal;

import java.util.Iterator;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.internal.resample.CaseReplacementResampler;
import de.invesdwin.util.math.decimal.internal.resample.CaseResampler;
import de.invesdwin.util.math.decimal.internal.resample.CircularResampler;
import de.invesdwin.util.math.decimal.internal.resample.StationaryResampler;

@ThreadSafe
public class DecimalAggregateBootstraps<E extends ADecimal<E>> {

    private final DecimalAggregate<E> parent;

    @GuardedBy("this")
    private CircularResampler<E> circularResampler;
    @GuardedBy("this")
    private StationaryResampler<E> stationaryResampler;

    public DecimalAggregateBootstraps(final DecimalAggregate<E> parent) {
        this.parent = parent;
    }

    public Iterator<E> randomizeShuffle(final RandomGenerator random) {
        return new CaseResampler<E>(parent).resample(random);
    }

    public Iterator<E> randomizeBootstrap(final RandomGenerator random) {
        return new CaseReplacementResampler<E>(parent).resample(random);
    }

    public Iterator<E> randomizeCircularBootstrap(final RandomGenerator random) {
        return getCircularResampler().resample(random);
    }

    private synchronized CircularResampler<E> getCircularResampler() {
        if (circularResampler == null) {
            circularResampler = new CircularResampler<E>(parent);
        }
        return circularResampler;
    }

    public Iterator<E> randomizeStationaryBootstrap(final RandomGenerator random) {
        return getStationaryResampler().resample(random);
    }

    private synchronized StationaryResampler<E> getStationaryResampler() {
        if (stationaryResampler == null) {
            stationaryResampler = new StationaryResampler<E>(parent);
        }
        return stationaryResampler;
    }

}
