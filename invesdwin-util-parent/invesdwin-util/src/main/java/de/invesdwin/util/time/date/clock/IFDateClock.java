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

    long nowMillis();

    @Override
    default FDate asFDate() {
        return now();
    }

    FTimeUnit getPrecision();

}
