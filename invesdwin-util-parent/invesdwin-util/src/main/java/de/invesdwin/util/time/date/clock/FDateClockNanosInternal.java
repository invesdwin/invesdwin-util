package de.invesdwin.util.time.date.clock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDatePicos;

/**
 * Zero-Allocating version of {@link FDateClockNanos} using internal VM APIs. This is faster than
 * {@link FDateClockNanos} but may not be available on all platforms and may break in future Java versions.
 * 
 * Adapted from java.time.Clock.currentInstant()
 * 
 * See also https://issues.apache.org/jira/browse/LOG4J2-3079
 */
@SuppressWarnings("restriction")
@ThreadSafe
public final class FDateClockNanosInternal implements IFDateClock {

    public static final FDateClockNanosInternal INSTANCE = new FDateClockNanosInternal();

    private static final long OFFSET_SEED = newEpochSecond();

    @GuardedBy("none for performance")
    private static long localOffset = OFFSET_SEED;

    private static long newEpochSecond() {
        //CHECKSTYLE:OFF
        return System.currentTimeMillis() / FTimeUnit.MILLISECONDS_IN_SECOND - 1024;
        //CHECKSTYLE:ON
    }

    @Override
    public FDate now() {
        long epochSecond = localOffset;
        long nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);

        if (nanoAdjustment == -1) {
            epochSecond = newEpochSecond();
            nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);
            if (nanoAdjustment == -1) {
                throw new InternalError("LocalOffset " + epochSecond + " is not in range");
            } else {
                localOffset = epochSecond;
            }
        }

        final long seconds = epochSecond + Longs.floorDiv(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);
        final int nanos = Integers.floorMod(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);

        return FDate.valueOfEpochSeconds(seconds, nanos);
    }

    @Override
    public void now(final IFDateUpdater updater) {
        long epochSecond = localOffset;
        long nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);

        if (nanoAdjustment == -1) {
            epochSecond = newEpochSecond();
            nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);
            if (nanoAdjustment == -1) {
                throw new InternalError("LocalOffset " + epochSecond + " is not in range");
            } else {
                localOffset = epochSecond;
            }
        }

        final long seconds = epochSecond + Longs.floorDiv(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);
        final int nanos = Integers.floorMod(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);

        final long picosMaybeOverflow = nanos * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
        final long millisOverflow = FDatePicos.toMillisecondsOverflow(picosMaybeOverflow);
        final int picos = FDatePicos.toPicosWithoutOverflow(picosMaybeOverflow);
        final long millis = seconds * FTimeUnit.MILLISECONDS_IN_SECOND + millisOverflow;

        updater.update(millis, picos);
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
        return FTimeUnit.NANOSECONDS;
    }

}
