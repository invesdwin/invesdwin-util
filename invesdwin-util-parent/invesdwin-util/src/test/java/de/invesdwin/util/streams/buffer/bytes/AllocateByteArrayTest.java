package de.invesdwin.util.streams.buffer.bytes;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.Instant;

@NotThreadSafe
@Disabled
public class AllocateByteArrayTest {

    private static final int COUNT_BYTES = 100000000;
    private static final int COUNT_LOOPS = 10;

    static {
        //CHECKSTYLE:OFF
        System.setProperty("io.netty.tryReflectionSetAccessible", "true");
        System.setProperty("io.netty.uninitializedArrayAllocationThreshold", "1");
        //CHECKSTYLE:ON
        //java 16 otherwise requires --illegal-access=permit --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Test
    public void testAllocateByteArrayPerformance() throws InterruptedException {
        final AtomicLong uninitialized = new AtomicLong();
        final AtomicLong initialized = new AtomicLong();
        final int runs = 100;
        final Runnable uninitializedT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    uninitialized.addAndGet(iteratorPerformanceTestUninitialized(i));
                }
            };
        };
        final Runnable initializedT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    initialized.addAndGet(iteratorPerformanceTestInitialized(i));
                }
            };
        };
        final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool("testAllocateByteArrayPerformance");
        Futures.submitAndWait(newCachedThreadPool, Arrays.asList(uninitializedT, initializedT));
        newCachedThreadPool.shutdown();
        newCachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        final boolean uninitializedIsFaster = uninitialized.get() < initialized.get();
        if (uninitializedIsFaster) {
            System.out.println(String.format("uninitializedIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(initialized.get() - uninitialized.get(), TimeUnit.NANOSECONDS)
                    + "ms " + new Percent(Decimal.valueOf(uninitialized.get()), Decimal.valueOf(initialized.get()))
                            .getValue(PercentScale.PERCENT)));
        } else {
            System.out.println(String.format("initializedIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(uninitialized.get() - initialized.get(), TimeUnit.NANOSECONDS)
                    + "ms " + new Percent(Decimal.valueOf(initialized.get()), Decimal.valueOf(uninitialized.get()))
                            .getValue(PercentScale.PERCENT)));
        }
    }

    private static long iteratorPerformanceTestUninitialized(final int curTest) {
        final Instant start = new Instant();
        for (int i = 0; i < COUNT_LOOPS; i++) {
            final byte[] bytes = ByteBuffers.allocateByteArray(COUNT_BYTES);
            Assertions.checkNotNull(bytes);
        }
        return start.longValue();
    }

    private static long iteratorPerformanceTestInitialized(final int curTest) {
        final Instant start = new Instant();
        for (int i = 0; i < COUNT_LOOPS; i++) {
            final byte[] bytes = new byte[COUNT_BYTES];
            Assertions.checkNotNull(bytes);
        }
        return start.longValue();
    }

    @Test
    public void testSimple() {
        final int iterations = 100000000;
        final Instant startUninitialized = new Instant();
        for (int i = 0; i < iterations; i++) {
            final byte[] arr = ByteBuffers.allocateByteArray(1000);
            Assertions.checkNotNull(arr);
        }
        //CHECKSTYLE:OFF
        System.out.println(startUninitialized);
        //CHECKSTYLE:ON
    }

}
