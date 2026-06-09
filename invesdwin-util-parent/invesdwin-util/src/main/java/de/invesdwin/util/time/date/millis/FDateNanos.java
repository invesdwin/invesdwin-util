package de.invesdwin.util.time.date.millis;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDates;

@Immutable
public final class FDateNanos {

    private FDateNanos() {}

    /**
     * Uses FDates.getDefaultClock().elapsedNanos() to allow for testing time machines and alternative time sources.
     * Normally System.nanoTime as the source.
     */
    public static long elapsedNanos() {
        return FDates.getDefaultClock().elapsedNanos();
    }

}
