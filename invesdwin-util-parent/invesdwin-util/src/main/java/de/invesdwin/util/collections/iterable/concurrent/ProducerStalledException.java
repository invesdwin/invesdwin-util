package de.invesdwin.util.collections.iterable.concurrent;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Thrown by a producer when its bounded queue stays full and the consumer makes NO draining progress for PRODUCER_STALL_TIMEOUT: a consumer that is merely slow drains eventually; a consumer that never drains is parked (typically blocked behind a lock/future held by this very producer chain). Aborting the producer converts such a deadlock ring into a failed update that callers can retry from a clean state, instead of holding heartbeats and locks forever.
 */
@NotThreadSafe
public class ProducerStalledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProducerStalledException(final String message) {
        super(message);
    }

}
