package de.invesdwin.util.assertions;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

import de.invesdwin.util.assertions.AllocationAssertions.IAllocationProbe;
import de.invesdwin.util.math.Longs;

/**
 * Tests for {@link AllocationAssertions#assertAllocationBounded(String, long, int, IAllocationProbe)} — the bounded
 * companion of the zero-allocation gate. Two arms, one pair of hot paths over the SAME probe:
 * <ul>
 * <li>the gate PASSES when the declared bound equals the calibrated true cost (bound = iterations × the calibrated
 * per-object ceiling; the calibration's ceiling division is the ONLY margin — no hand-picked headroom — exactly the
 * way the ring-layer miss gates calibrate their ceiling);</li>
 * <li>the gate TRIPS when the declared bound sits clearly below that cost — the negative control that proves the
 * ceiling is enforced and that the passing arm above is not vacuous (a gate which never fails also never
 * means anything).</li>
 * </ul>
 *
 * <p>
 * The probe returns a value derived from the operation under test, so the harness's anti-elimination barrier
 * consumes a real sum; a probe over a discarded result would let the measured region be dead-code-eliminated and
 * the bound would then be satisfied by an optimized-away loop. A VM without
 * {@code com.sun.management.ThreadMXBean} thread-allocation instrumentation skips visibly (no gate literal is
 * printed on the skip path).
 */
@NotThreadSafe
public class AllocationAssertionsBoundedTest {

    /** Measured iterations of the bounded gate (the warm-up region is the harness' own constant). */
    private static final int GATE_ITERATIONS = 20_000;

    /** Iterations of the per-object cost calibration loop (same recipe as the ring-layer miss gates). */
    private static final int CALIBRATION_ITERATIONS = 10_000;

    @Test
    public void testBoundedGatePassesWithCalibratedBound() {
        final long perPayloadBytes = calibratedPerPayloadBytes();
        final long bound = (long) GATE_ITERATIONS * perPayloadBytes;
        //one payload per call, bound = the calibrated true cost with zero slack: a second per-call object, or a
        //wrongly hoisted allocation, trips this
        AllocationAssertions.assertAllocationBounded("util-bounded-gate-within-calibrated-bound", bound,
                GATE_ITERATIONS, new PayloadAllocatingProbe());
    }

    @Test
    public void testBoundedGateTripsClearlyBelowTheRealCost() {
        final long perPayloadBytes = calibratedPerPayloadBytes();
        final long perCallBound = Longs.max(1L, perPayloadBytes / 2L);
        final long bound = (long) GATE_ITERATIONS * perCallBound;
        //negative control: the same probe, a bound at half the calibrated per-call cost — the ceiling must be
        //enforced BEFORE the nightly literal, so this arm fails loudly and prints nothing
        Assertions.assertThatThrownBy(() -> AllocationAssertions.assertAllocationBounded(
                "util-bounded-gate-negative-control", bound, GATE_ITERATIONS, new PayloadAllocatingProbe())).as(
                        "bounded gate must trip when the measured delta exceeds the declared bound").isInstanceOf(
                                AssertionError.class);
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Calibrates the per-object cost on THIS vm/config: one {@link Payload} per iteration into an
     * {@link AtomicReference} (its volatile store defeats C2 scalar replacement, so the counted delta is the real
     * object size), ceiling division so the calibration's own rounding never makes a bound smaller than a
     * legitimate single-object loop.
     */
    private static long calibratedPerPayloadBytes() {
        final ThreadMXBean allocBean = allocatedBytesBeanOrNull();
        Assumptions.assumeTrue(allocBean != null,
                "bounded alloc gate skipped: thread allocation counter unavailable on this VM");
        final long threadId = Thread.currentThread().getId();
        final AtomicReference<Payload> calibrationSink = new AtomicReference<>();
        for (int i = 0; i < 100; i++) {
            calibrationSink.set(new Payload(i));
        }
        allocBean.getThreadAllocatedBytes(threadId);
        final long before = allocBean.getThreadAllocatedBytes(threadId);
        long sink = 0L;
        for (int i = 0; i < CALIBRATION_ITERATIONS; i++) {
            calibrationSink.set(new Payload(i));
            sink += i;
        }
        final long after = allocBean.getThreadAllocatedBytes(threadId);
        Assertions.checkTrue(sink != 0L, "calibration loop was eliminated - per-object cost is meaningless");
        return Longs.max(1L,
                (after - before + CALIBRATION_ITERATIONS - 1L) / CALIBRATION_ITERATIONS);
    }

    private static ThreadMXBean allocatedBytesBeanOrNull() {
        final java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean instanceof ThreadMXBean) {
            final ThreadMXBean sunBean = (ThreadMXBean) bean;
            if (sunBean.isThreadAllocatedMemorySupported()) {
                if (!sunBean.isThreadAllocatedMemoryEnabled()) {
                    sunBean.setThreadAllocatedMemoryEnabled(true);
                }
                if (sunBean.isThreadAllocatedMemoryEnabled()) {
                    return sunBean;
                }
            }
        }
        return null;
    }

    /** The allocated carrier of the probe: one long field, the object whose bytes the gate bounds. */
    private static final class Payload {
        private final long value;

        private Payload(final long value) {
            super();
            this.value = value;
        }
    }

    /**
     * Hoisted op: allocates exactly one {@link Payload} per call and returns a value derived from its own state, so
     * the harness' anti-elimination barrier has a non-constant sum to consume.
     */
    private static final class PayloadAllocatingProbe implements IAllocationProbe {

        private final AtomicReference<Payload> sink = new AtomicReference<>();
        private long counter = 1L;

        @Override
        public long run() {
            this.sink.set(new Payload(this.counter));
            return this.counter++;
        }
    }

}
