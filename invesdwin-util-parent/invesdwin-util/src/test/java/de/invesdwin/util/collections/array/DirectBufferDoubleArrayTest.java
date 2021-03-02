package de.invesdwin.util.collections.array;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.random.RandomGenerators;
import de.invesdwin.util.time.Instant;

@NotThreadSafe
public class DirectBufferDoubleArrayTest {

    private static final int COUNT_RAND = 10000;
    private static final int COUNT_LOOPS = 1000;

    @Test
    public void testDirectVsPlainArrayIteratorPerformance() throws InterruptedException {
        final AtomicLong direct = new AtomicLong();
        final AtomicLong plain = new AtomicLong();
        final int runs = 100;
        final Runnable directT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    direct.addAndGet(iteratorPerformanceTestDirect(i));
                }
            };
        };
        final Runnable plainT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    plain.addAndGet(iteratorPerformanceTestPlain(i));
                }
            };
        };
        final ExecutorService newCachedThreadPool = Executors
                .newCachedThreadPool("testDirectVsPlainArrayIteratorPerformance");
        Futures.submitAndWait(newCachedThreadPool, Arrays.asList(directT, plainT));
        newCachedThreadPool.shutdown();
        newCachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        final boolean directIsFaster = direct.get() < plain.get();
        if (directIsFaster) {
            System.out.println(String.format("directIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(plain.get() - direct.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(direct.get()), Decimal.valueOf(plain.get()))
                            .getValue(PercentScale.PERCENT)));
        } else {
            System.out.println(String.format("plainIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(direct.get() - plain.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(plain.get()), Decimal.valueOf(direct.get()))
                            .getValue(PercentScale.PERCENT)));
        }
    }

    private static long iteratorPerformanceTestDirect(final int curTest) {
        final Instant start = new Instant();
        final DirectBufferDoubleArray list = new DirectBufferDoubleArray(COUNT_RAND);
        final RandomDataGenerator r = new RandomDataGenerator(RandomGenerators.newDefaultRandom());
        for (int i = 0; i < COUNT_RAND; i++) {
            list.set(i, r.nextLong(0, Long.MAX_VALUE));
        }
        for (int i = 0; i < COUNT_LOOPS; i++) {
            for (int j = 0; j < COUNT_RAND; j++) {
                Assertions.checkTrue(list.get(j) >= 0);
            }
        }
        return start.longValue();
    }

    private static long iteratorPerformanceTestPlain(final int curTest) {
        final Instant start = new Instant();
        final PlainDoubleArray list = new PlainDoubleArray(COUNT_RAND);
        final RandomDataGenerator r = new RandomDataGenerator(RandomGenerators.newDefaultRandom());
        for (int i = 0; i < COUNT_RAND; i++) {
            list.set(i, r.nextLong(0, Long.MAX_VALUE));
        }
        for (int i = 0; i < COUNT_LOOPS; i++) {
            for (int j = 0; j < COUNT_RAND; j++) {
                Assertions.checkTrue(list.get(j) >= 0);
            }
        }
        return start.longValue();
    }

}
