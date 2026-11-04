package de.invesdwin.util.time.flow;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * A recorded attempt must hold the gap it just asked for until the next one, grow that gap with every consecutive
 * attempt of the same incident, never let it pass the configured maximum, and start from its minimum again as soon as
 * the operation is proven good. All on supplied epoch milliseconds, without ever sleeping.
 */
@NotThreadSafe
public class ExponentialBackoffTest {

    private static final Duration MIN_BACKOFF = Duration.THIRTY_SECONDS;
    private static final Duration MAX_BACKOFF = Duration.FIVE_MINUTES;

    @Test
    public void consecutiveAttemptsGrowTheGapOnlyUpToTheCap() {
        final ExponentialBackoff backoff = newBackoff();
        //the first attempt of an incident is not held back by the ladder
        Assertions.checkTrue(backoff.isDue(0), "the first attempt of an incident must be due immediately");
        long nowMillis = 0;
        //30s, 1m, 2m, 4m and then the 5m cap for the rest of the outage
        final long[] expectedGapsMillis = { toMillis(MIN_BACKOFF), toMillis(MIN_BACKOFF) * 2, toMillis(MIN_BACKOFF) * 4,
                240_000, 300_000, 300_000, 300_000 };
        for (final long expectedGapMillis : expectedGapsMillis) {
            Assertions.checkTrue(backoff.isDue(nowMillis), "attempt at [%s] ms must be due", nowMillis);
            assertGap(backoff.recordRestart(nowMillis), expectedGapMillis);
            //one millisecond before the gap is over there must not be another attempt yet
            Assertions.checkFalse(backoff.isDue(nowMillis + expectedGapMillis - 1),
                    "attempt at [%s] ms must still be held back by [%s] ms", nowMillis, expectedGapMillis);
            nowMillis += expectedGapMillis;
        }
        //a long outage may never push the gap past the cap either, it just keeps attempting at the cap
        for (int i = 0; i < 50; i++) {
            assertGap(backoff.recordRestart(nowMillis), toMillis(MAX_BACKOFF));
            nowMillis += toMillis(MAX_BACKOFF);
        }
    }

    @Test
    public void aProvenGoodOperationStartsTheLadderFromItsMinimumAgain() {
        final ExponentialBackoff backoff = newBackoff();
        backoff.recordRestart(0);
        backoff.recordRestart(toMillis(MIN_BACKOFF));
        //the ladder is already growing, so the next gap is two minutes and this moment is still held back
        final long heldBackMillis = toMillis(MIN_BACKOFF) + 60_000 - 1;
        Assertions.checkFalse(backoff.isDue(heldBackMillis), "the growing ladder must still hold the attempt back");
        //the operation proved good again, which ends the incident
        backoff.reset();
        Assertions.checkTrue(backoff.isDue(heldBackMillis),
                "an attempt after a proven good operation must not be held back by the previous incident");
        assertGap(backoff.recordRestart(heldBackMillis), toMillis(MIN_BACKOFF));
        Assertions.checkFalse(backoff.isDue(heldBackMillis + toMillis(MIN_BACKOFF) - 1),
                "the reset ladder must grow from its minimum again instead of from the previous incident");
        Assertions.checkTrue(backoff.isDue(heldBackMillis + toMillis(MIN_BACKOFF)),
                "the reset ladder must hold the next attempt back by exactly its minimum gap");
    }

    @Test
    public void theLadderGrowsStrictlyUntilItReachesTheCap() {
        final ExponentialBackoff backoff = newBackoff();
        final long capMillis = toMillis(MAX_BACKOFF);
        long nowMillis = 0;
        long previousGapMillis = 0;
        long gapMillis = -1;
        for (int attempts = 0; attempts < 128; attempts++) {
            gapMillis = toMillis(backoff.recordRestart(nowMillis));
            Assertions.checkTrue(gapMillis <= capMillis, "gap [%s] ms must never exceed the cap [%s] ms", gapMillis,
                    capMillis);
            nowMillis += gapMillis;
            if (gapMillis == capMillis) {
                return;
            }
            Assertions.checkTrue(gapMillis > previousGapMillis, "the ladder must grow until it reaches the cap");
            previousGapMillis = gapMillis;
        }
        Assertions.fail("the ladder never reached the configured cap of " + capMillis + " ms");
    }

    @Test
    public void theDefaultLadderStartsAtTheDefaultMinimum() {
        final ExponentialBackoff backoff = new ExponentialBackoff(MAX_BACKOFF);
        assertGap(backoff.getNextBackoff(), toMillis(ExponentialBackoff.DEFAULT_MIN_BACKOFF));
        assertGap(backoff.recordRestart(0), toMillis(ExponentialBackoff.DEFAULT_MIN_BACKOFF));
        //and it doubles towards the cap like any other ladder
        assertGap(backoff.getNextBackoff(), toMillis(ExponentialBackoff.DEFAULT_MIN_BACKOFF) * 2);
    }

    @Test
    public void aCapBelowTheMinimumClampsToTheMinimumAsFloor() {
        //a maximum below the minimum would make the ladder shrink on every escalation, which is what the clamp prevents
        final ExponentialBackoff backoff = new ExponentialBackoff(MIN_BACKOFF, Duration.ONE_SECOND);
        long nowMillis = 0;
        for (int attempts = 0; attempts < 10; attempts++) {
            assertGap(backoff.recordRestart(nowMillis), toMillis(MIN_BACKOFF));
            nowMillis += toMillis(MIN_BACKOFF);
        }
    }

    @Test
    public void theLadderNeverMovesTheClockByItself() {
        //isDue only reads: asking repeatedly whether an attempt is due must not consume the gap
        final ExponentialBackoff backoff = newBackoff();
        backoff.recordRestart(0);
        final long heldBackMillis = toMillis(MIN_BACKOFF) - 1;
        for (int checks = 0; checks < 10; checks++) {
            Assertions.checkFalse(backoff.isDue(heldBackMillis),
                    "checking must not consume the gap, the attempt at [%s] ms must still be held back",
                    heldBackMillis);
        }
        Assertions.checkTrue(backoff.isDue(toMillis(MIN_BACKOFF)),
                "the gap must be over exactly at its end without any sleep involved");
    }

    private static ExponentialBackoff newBackoff() {
        return new ExponentialBackoff(MIN_BACKOFF, MAX_BACKOFF);
    }

    private static void assertGap(final Duration gap, final long expectedGapMillis) {
        Assertions.assertThat(toMillis(gap)).as("attempt gap").isEqualTo(expectedGapMillis);
    }

    private static long toMillis(final Duration duration) {
        return duration.longValue(FTimeUnit.MILLISECONDS);
    }

}
