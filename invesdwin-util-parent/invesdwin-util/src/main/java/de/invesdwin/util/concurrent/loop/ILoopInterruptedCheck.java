package de.invesdwin.util.concurrent.loop;

public interface ILoopInterruptedCheck {

    void resetInterval();

    default boolean checkNoInterrupt() {
        try {
            return check();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks in the fastest way, skipping checks for a few cycles based on a counter if determined to be useful. This
     * can be inaccurate for scenarios where checks occur infrequently.
     */
    boolean check() throws InterruptedException;

    default boolean checkClockNoInterrupt() {
        try {
            return checkClock();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Actually checks against a timestamp without skipping based on a counter. This is still accurate despite scenarios
     * where checks occur infrequently.
     */
    boolean checkClock() throws InterruptedException;

}
