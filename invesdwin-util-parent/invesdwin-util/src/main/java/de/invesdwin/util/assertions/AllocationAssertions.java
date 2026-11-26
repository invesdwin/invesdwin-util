package de.invesdwin.util.assertions;

import java.lang.management.ManagementFactory;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Assumptions;

/**
 * Zero-allocation gate for hot read paths: raw allocated-bytes delta over the measured region must be exactly zero.
 * Mechanism — {@code com.sun.management.ThreadMXBean.getThreadAllocatedBytes} (JDK
 * 17, no new dep; NOT JFR, sampling cannot certify zero), hoisted op outside the measured region, raw delta == 0
 * asserted over 100k iterations (default) through the {@link Assertions} facade next door.
 *
 * <p>
 * Test utilities live in main trees here (cf. {@link Assertions}), so the gate is on every consumer's
 * compile classpath without a tests-jar. The JUnit dependency is the same provided-scope
 * {@code junit-jupiter-engine} that already backs the main-tree {@link Assertions}/{@code ParallelSuite} facades (pom
 * comment "optional dependency for Assertions"), so the skip contract travels with it and no new dependency appears.
 *
 * <p>
 * {@link #assertAllocationBounded(String, long, int, IAllocationProbe)} is the bounded companion: the same
 * instrumentation and the same barriers with a declared ceiling for the measured region instead of the zero
 * ceiling — it is what gates an operation that legitimately allocates a bounded amount can use.
 *
 * <p>
 * On a successful gate run this prints the literal {@code ALLOC-GATE executed} — nightly runs grep for it, so an
 * {@code assumeTrue} skip on a non-HotSpot VM can never look like a pass. The literal is NEVER
 * printed on the skip path, and NEVER printed when an asserted ceiling is breached.
 *
 * <p>
 * Anti-elimination: {@link IAllocationProbe} RETURNS the read's value and this harness consumes the accumulation
 * (volatile sink store + non-zero measured-sum assertion before the literal), so a delta of zero can only mean "the arm
 * genuinely allocated nothing", never "the loop was optimized away".
 *
 */
@ThreadSafe
public final class AllocationAssertions {

    /**
     * Raw delta == 0 asserted over 100k iterations (default).
     */
    public static final int DEFAULT_ITERATIONS = 100000;

    /**
     * Warmup outside the measured region: triggers lazy class loading and gets the probe plus the
     * {@code getThreadAllocatedBytes} call JIT-compiled, otherwise the interpreter allocates what C2 would later
     * eliminate and the gate reports false failures.
     */
    private static final int WARMUP_ITERATIONS = 10000;

    /**
     * The operation under the gate. Callers MUST construct it (lambda capture included) BEFORE calling — the
     * "hoisted op" rule: no allocation belonging to the call setup may sit inside the measured region, and a fresh
     * lambda is allocated per call site evaluation.
     *
     * <p>
     * The probe RETURNS the value produced by the read under test: {@link #assertZeroAllocation} accumulates every
     * return and consumes the accumulation through a barrier the JIT cannot eliminate (a {@code static volatile} store
     * plus a non-zero-magnitude assertion on the measured sum). A void probe with a caller-discarded sink let the
     * measured arm be dead-code-eliminated — delta==0 then certified "the loop was optimized away" instead of "the arm
     * allocates nothing" (anti-elimination sink). A probe whose result is legitimately
     * constant-zero on this data must use a probe that consumes the read differently — the gate fails loudly instead of
     * passing vacuously.
     */
    @FunctionalInterface
    public interface IAllocationProbe {
        long run();
    }

    /**
     * Anti-elimination sink: the store a C2-compiled measured loop cannot remove, keeping the probe accumulation (and
     * therefore the measured call) alive.
     */
    private static volatile long consumedSink;

    private AllocationAssertions() {}

    /**
     * Asserts zero allocation over {@link #DEFAULT_ITERATIONS} iterations.
     */
    public static void assertZeroAllocation(final String name, final IAllocationProbe probe) {
        assertZeroAllocation(name, DEFAULT_ITERATIONS, probe);
    }

    /**
     * Asserts the RAW allocated-bytes delta of {@code probe.run()} over {@code iterations} calls is exactly zero
     * (current thread, HotSpot thread-allocation instrumentation). Skips via {@link Assumptions#assumeTrue} on a VM
     * without that instrumentation — never silently passes.
     */
    public static void assertZeroAllocation(final String name, final int iterations, final IAllocationProbe probe) {
        final com.sun.management.ThreadMXBean allocBean = allocatedBytesBeanOrNull();
        //HotSpot-only guard: a skip stays visible (no ALLOC-GATE literal emitted), and the nightly
        //grep of "ALLOC-GATE executed" is the mitigation for silently dropped coverage.
        Assumptions.assumeTrue(allocBean != null,
                "no com.sun.management.ThreadMXBean thread-allocation instrumentation on this VM" + " - alloc gate '"
                        + name + "' SKIPPED (not passed); nightly grep catches lost coverage");
        final long threadId = Thread.currentThread().getId();
        //hoisted op: probe instance, bean lookup and threadId all prepared above;
        //below, the measured region is the bean reads and probe.run() calls only.
        long accumulated = 0L;
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            accumulated += probe.run();
        }
        //anti-elimination barrier on the warm-up accumulation too: a volatile store C2 cannot drop
        consumedSink = accumulated;
        allocBean.getThreadAllocatedBytes(threadId);
        final long before = allocBean.getThreadAllocatedBytes(threadId);
        final long measuredStart = accumulated;
        for (int i = 0; i < iterations; i++) {
            accumulated += probe.run();
        }
        final long after = allocBean.getThreadAllocatedBytes(threadId);
        final long measuredSum = accumulated - measuredStart;
        //anti-elimination barrier: the store keeps the accumulation (and with it the probe calls
        //feeding it) alive in the optimized loop — a discarded, unasserted sink let the whole
        //measured region be dead-code-eliminated while still reporting delta==0
        consumedSink = accumulated;
        final long deltaBytes = after - before;
        //expected-magnitude consumption check BEFORE the zero-delta assert and BEFORE the nightly
        //literal: zero sum over the measured region means the probe returned nothing to consume —
        //either the loop was optimized away or the probe is malformed; either way this run proves
        //nothing and must fail loudly, not print the gate literal
        Assertions.checkTrue(measuredSum != 0L,
                "alloc-gate '" + name + "': probe accumulation over the " + iterations
                        + " measured iterations is zero — the measured region may have been eliminated by the JIT"
                        + " or the probe returns no value derived from the read under test (anti-elimination barrier)");
        Assertions.checkEquals(0L, deltaBytes,
                "alloc-gate '" + name + "': raw bytes allocated over " + iterations + " iterations must be zero");
        //nightly grep target — printed ONLY on a successful gate run.
        System.out.println("ALLOC-GATE executed " + name + " iterations=" + iterations); //SUPPRESS CHECKSTYLE single line
    }

    /**
     * Asserts zero-delta allocation over {@link #DEFAULT_ITERATIONS} with a declared ceiling instead of the zero
     * ceiling — see {@link #assertAllocationBounded(String, long, int, IAllocationProbe)}.
     */
    public static void assertAllocationBounded(final String name, final long boundBytes,
            final IAllocationProbe probe) {
        assertAllocationBounded(name, boundBytes, DEFAULT_ITERATIONS, probe);
    }

    /**
     * Bounded companion of {@link #assertZeroAllocation(String, int, IAllocationProbe)}: the RAW allocated-bytes
     * delta of {@code probe.run()} over {@code iterations} calls must stay at or below {@code boundBytes} — the
     * ceiling is for the WHOLE measured region, not per call — instead of being pinned to exactly zero. The
     * measured region, the warm-up rule, the hoisted-op rule, the skip contract on a VM without the
     * thread-allocation instrumentation and the anti-elimination barrier (the probe's returned values are
     * accumulated, the accumulation is stored through a volatile sink and a zero measured sum fails the gate)
     * are the same as in the zero-alloc harness; only the ceiling differs.
     *
     * <p>
     * The delta assert runs BEFORE the {@code ALLOC-GATE executed} literal is printed, so a breached bound never
     * reaches the nightly grep line, and the probe must return a value derived from the operation under test — a
     * probe over an operation whose result is discarded lets the measured arm be dead-code-eliminated, which would
     * print a passing literal for a bound that was never actually exercised.
     *
     * @param name
     *            gate name — it is what the {@code ALLOC-GATE executed} grep line reports
     * @param boundBytes
     *            ceiling for the raw allocated-bytes delta over the whole measured region
     * @param iterations
     *            number of {@code probe.run()} calls inside the measured region
     * @param probe
     *            the operation under the gate, constructed before the call (hoisted op) and returning a consumed
     *            result
     */
    public static void assertAllocationBounded(final String name, final long boundBytes, final int iterations,
            final IAllocationProbe probe) {
        final com.sun.management.ThreadMXBean allocBean = allocatedBytesBeanOrNull();
        //HotSpot-only guard: a skip stays visible (no ALLOC-GATE literal emitted), and the nightly
        //grep of "ALLOC-GATE executed" is the mitigation for silently dropped coverage.
        Assumptions.assumeTrue(allocBean != null,
                "no com.sun.management.ThreadMXBean thread-allocation instrumentation on this VM"
                        + " - alloc gate '" + name + "' SKIPPED (not passed); nightly grep catches lost coverage");
        final long threadId = Thread.currentThread().getId();
        //hoisted op: probe instance, bean lookup and threadId all prepared above;
        //below, the measured region is the bean reads and probe.run() calls only.
        long accumulated = 0L;
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            accumulated += probe.run();
        }
        //anti-elimination barrier on the warm-up accumulation too: a volatile store C2 cannot drop
        consumedSink = accumulated;
        allocBean.getThreadAllocatedBytes(threadId);
        final long before = allocBean.getThreadAllocatedBytes(threadId);
        final long measuredStart = accumulated;
        for (int i = 0; i < iterations; i++) {
            accumulated += probe.run();
        }
        final long after = allocBean.getThreadAllocatedBytes(threadId);
        final long measuredSum = accumulated - measuredStart;
        //anti-elimination barrier: the store keeps the accumulation (and with it the probe calls
        //feeding it) alive in the optimized loop — a discarded, unasserted sink let the whole
        //measured region be dead-code-eliminated while still reporting delta==0
        consumedSink = accumulated;
        final long deltaBytes = after - before;
        //expected-magnitude consumption check BEFORE the bound assert and BEFORE the nightly
        //literal: zero sum over the measured region means the probe returned nothing to consume —
        //either the loop was optimized away or the probe is malformed; either way this run proves
        //nothing and must fail loudly, not print the gate literal
        Assertions.checkTrue(measuredSum != 0L,
                "alloc-gate '" + name + "': probe accumulation over the " + iterations
                        + " measured iterations is zero — the measured region may have been eliminated by the JIT"
                        + " or the probe returns no value derived from the read under test (anti-elimination barrier)");
        Assertions.checkTrue(deltaBytes >= 0L && deltaBytes <= boundBytes,
                "alloc-gate '" + name + "': raw bytes allocated over " + iterations + " iterations must stay within"
                        + " [0, " + boundBytes + "] B, was " + deltaBytes + " B");
        //nightly grep target — printed ONLY on a successful gate run, never on a breach or a skip.
        //CS:OFF Regexp
        System.out.println("ALLOC-GATE executed " + name + " delta=" + deltaBytes + " bound=" + boundBytes
                + " iterations=" + iterations);
        //CS:ON Regexp
    }

    private static com.sun.management.ThreadMXBean allocatedBytesBeanOrNull() {
        final java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean instanceof com.sun.management.ThreadMXBean) {
            final com.sun.management.ThreadMXBean sunBean = (com.sun.management.ThreadMXBean) bean;
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

}
