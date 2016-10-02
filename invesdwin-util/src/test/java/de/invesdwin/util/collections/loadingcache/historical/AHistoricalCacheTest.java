package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.Futures;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;

// CHECKSTYLE:OFF
@NotThreadSafe
@Ignore
public class AHistoricalCacheTest {
    //CHECKSTYLE:ON

    private static final int COUNT_RAND = 1000000;

    @Test
    public void testLongVsDateHashcodePerformance() throws InterruptedException {
        final AtomicLong date = new AtomicLong();
        final AtomicLong l = new AtomicLong();
        final int runs = 100;
        final Runnable dateT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    date.addAndGet(hashcodePerformanceTestDate(i));
                }
            };
        };
        final Runnable longT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    l.addAndGet(hashcodePerformanceTestLong(i));
                }
            };
        };
        final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool("testLongVsDateHashcodePerformance");
        Futures.submitAndWait(newCachedThreadPool, Arrays.asList(dateT, longT));
        newCachedThreadPool.shutdown();
        newCachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        final boolean dateIsFaster = date.get() < l.get();
        if (dateIsFaster) {
            System.out.println(String.format("dateIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(l.get() - date.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(date.get()), Decimal.valueOf(l.get()))
                            .getValue(PercentScale.PERCENT)));
        } else {
            System.out.println(String.format("longIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(date.get() - l.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(l.get()), Decimal.valueOf(date.get()))
                            .getValue(PercentScale.PERCENT)));
        }
    }

    private static long hashcodePerformanceTestDate(final int curTest) {
        final List<Long> list = new ArrayList<Long>();
        final RandomDataGenerator r = new RandomDataGenerator(new JDKRandomGenerator());
        for (long i = 0L; i < COUNT_RAND; i++) {
            list.add(r.nextLong(Long.MIN_VALUE, Long.MAX_VALUE));
        }
        final Instant start = new Instant();
        for (final Long l : list) {
            l.hashCode();
        }
        return start.longValue();
    }

    private static long hashcodePerformanceTestLong(final int curTest) {
        final List<FDate> list = new ArrayList<FDate>();
        final RandomDataGenerator r = new RandomDataGenerator(new JDKRandomGenerator());
        for (long i = 0L; i < COUNT_RAND; i++) {
            list.add(new FDate(r.nextLong(Long.MIN_VALUE, Long.MAX_VALUE)));
        }
        final Instant start = new Instant();
        for (final FDate l : list) {
            l.hashCode();
        }
        return start.longValue();
    }

}
