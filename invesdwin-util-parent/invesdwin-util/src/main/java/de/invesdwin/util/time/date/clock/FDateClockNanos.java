package de.invesdwin.util.time.date.clock;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDatePicos;

@Immutable
public final class FDateClockNanos implements IFDateClock {

    public static final FDateClockNanos INSTANCE = new FDateClockNanos();

    private FDateClockNanos() {}

    @Override
    public FDate now() {
        return FDate.valueOf(java.time.Instant.now());
    }

    @Override
    public void now(final IFDateUpdater updater) {
        final java.time.Instant instant = java.time.Instant.now();

        final long seconds = instant.getEpochSecond();
        final int nanos = instant.getNano();

        final long picosMaybeOverflow = nanos * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millis = seconds * FTimeUnit.MILLISECONDS_IN_SECOND + millisOverflow;

        updater.update(millis, picos);
    }

    @Override
    public long nowMillis() {
        return System.currentTimeMillis();
    }

}
