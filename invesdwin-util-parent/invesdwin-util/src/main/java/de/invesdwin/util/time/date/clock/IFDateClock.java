package de.invesdwin.util.time.date.clock;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.IFDateProvider;

public interface IFDateClock extends IFDateProvider {

    FDate now();

    /**
     * Zero-Allocating version of {@link #now()}.
     */
    void now(IFDateUpdater updater);

    /**
     * Normally System.currentTimeMillis but may be overridden for testing or to use a different time source.
     */
    long nowMillis();

    /**
     * Normally System.nanoTime but may be overridden for testing or to use a different time source. Note that this is
     * not related to the epoch and should only be used for measuring elapsed time.
     */
    long elapsedNanos();

    @Override
    default FDate asFDate() {
        return now();
    }

    FTimeUnit getPrecision();

}
