package de.invesdwin.util.concurrent.loop;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.util.concurrent.LoopInterruptedCheckMillis;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@Ignore("manual test")
@NotThreadSafe
public class LoopInterruptedCheckTest {

    private static final int SECONDS = 1;

    @Test
    public void test() throws InterruptedException {
        testLoopAlwaysDate();
        testLoopAlwaysInstant();
        testLoopAlwaysCheck();
        testLoopAlwaysCheckMs();
    }

    private void testLoopAlwaysCheck() throws InterruptedException {
        final Instant start = new Instant();
        final LoopInterruptedCheck check = new LoopInterruptedCheck(Duration.ONE_SECOND);
        int iterations = 0;
        for (int i = 0; i < SECONDS;) {
            if (check.check()) {
                i++;
            }
            iterations++;
        }
        //CHECKSTYLE:OFF
        System.out.println("testLoopAlwaysCheck    " + iterations + ": " + start);
        //CHECKSTYLE:ON
    }

    private void testLoopAlwaysCheckMs() throws InterruptedException {
        final Instant start = new Instant();
        final LoopInterruptedCheckMillis check = new LoopInterruptedCheckMillis(Duration.ONE_SECOND);
        int iterations = 0;
        for (int i = 0; i < SECONDS;) {
            if (check.check()) {
                i++;
            }
            iterations++;
        }
        //CHECKSTYLE:OFF
        System.out.println("testLoopAlwaysCheckMs  " + iterations + ": " + start);
        //CHECKSTYLE:ON
    }

    private void testLoopAlwaysInstant() throws InterruptedException {
        final Instant start = new Instant();
        Instant prevInterruptCheck = new Instant();
        int iterations = 0;
        for (int i = 0; i < SECONDS;) {
            final Instant curTime = new Instant();
            if (new Duration(prevInterruptCheck, curTime).isGreaterThanOrEqualTo(Duration.ONE_SECOND)) {
                Threads.throwIfInterrupted();
                prevInterruptCheck = curTime;
                i++;
            }
            iterations++;
        }
        //CHECKSTYLE:OFF
        System.out.println("testLoopAlwaysInstant  " + iterations + ": " + start);
        //CHECKSTYLE:ON
    }

    private void testLoopAlwaysDate() throws InterruptedException {
        final Instant start = new Instant();
        FDate prevInterruptCheck = new FDate();
        int iterations = 0;
        for (int i = 0; i < SECONDS;) {
            final FDate curTime = new FDate();
            if (new Duration(prevInterruptCheck, curTime).isGreaterThanOrEqualTo(Duration.ONE_SECOND)) {
                Threads.throwIfInterrupted();
                prevInterruptCheck = curTime;
                i++;
            }
            iterations++;
        }
        //CHECKSTYLE:OFF
        System.out.println("testLoopAlwaysDate     " + iterations + ": " + start);
        //CHECKSTYLE:ON
    }

}
