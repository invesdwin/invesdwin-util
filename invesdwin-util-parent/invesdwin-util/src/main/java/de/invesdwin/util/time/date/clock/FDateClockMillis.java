package de.invesdwin.util.time.date.clock;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public final class FDateClockMillis implements IFDateClock {

    public static final FDateClockMillis INSTANCE = new FDateClockMillis();

    private FDateClockMillis() {}

    @Override
    public FDate now() {
        return new FDate(nowMillis());
    }

    @Override
    public void now(final IFDateUpdater updater) {
        updater.update(nowMillis(), 0);
    }

    @Override
    public long nowMillis() {
        //CHECKSTYLE:OFF
        return System.currentTimeMillis();
        //CHECKSTYLE:ON
    }

    @Override
    public long elapsedNanos() {
        //CHECKSTYLE:OFF
        return System.nanoTime();
        //CHECKSTYLE:ON
    }

    @Override
    public FTimeUnit getPrecision() {
        return FTimeUnit.MILLISECONDS;
    }

}
