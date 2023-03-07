package de.invesdwin.util.collections.iterable.buffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.time.Instant;

@NotThreadSafe
@Disabled
public class BufferingIteratorIterateTest {

    private static final int COUNT_RAND = 100;
    private static final int COUNT_LOOPS = 1000;

    @Test
    public void testListVsBufferingIteratorPerformance() throws InterruptedException {
        final AtomicLong date = new AtomicLong();
        final AtomicLong l = new AtomicLong();
        final int runs = 100;
        final Runnable dateT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    date.addAndGet(iteratorPerformanceTestBuffering(i));
                }
            };
        };
        final Runnable longT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    l.addAndGet(iteratorPerformanceTestList(i));
                }
            };
        };
        final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool("testLongVsDateHashcodePerformance");
        Futures.submitAndWait(newCachedThreadPool, Arrays.asList(dateT, longT));
        newCachedThreadPool.shutdown();
        newCachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        final boolean dateIsFaster = date.get() < l.get();
        if (dateIsFaster) {
            System.out.println(String.format("bufferingIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(l.get() - date.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(date.get()), Decimal.valueOf(l.get()))
                            .getValue(PercentScale.PERCENT)));
        } else {
            System.out.println(String.format("listIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(date.get() - l.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(l.get()), Decimal.valueOf(date.get()))
                            .getValue(PercentScale.PERCENT)));
        }
    }

    private static long iteratorPerformanceTestBuffering(final int curTest) {
        final Instant start = new Instant();
        final BufferingIterator<Long> list = new BufferingIterator<Long>();
        final IRandomGenerator r = PseudoRandomGenerators.newPseudoRandom();
        for (long ilong = 0L; ilong < COUNT_RAND; ilong++) {
            list.add(r.nextLong(Long.MIN_VALUE, Long.MAX_VALUE));
        }
        for (int i = 0; i < COUNT_LOOPS; i++) {
            final ICloseableIterator<Long> iterator = list.iterator();
            try {
                while (true) {
                    iterator.next().hashCode();
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
        return start.toDurationNanos();
    }

    private static long iteratorPerformanceTestList(final int curTest) {
        final Instant start = new Instant();
        final ArrayList<Long> list = new ArrayList<Long>();
        final IRandomGenerator r = PseudoRandomGenerators.newPseudoRandom();
        for (long ilong = 0L; ilong < COUNT_RAND; ilong++) {
            list.add(r.nextLong(Long.MIN_VALUE, Long.MAX_VALUE));
        }
        //        for (int i = 0; i < COUNT_LOOPS; i++) {
        //            for (int idx = 0; idx < list.size(); idx++) {
        //                list.get(idx).hashCode();
        //            }
        //        }
        for (int i = 0; i < COUNT_LOOPS; i++) {
            final Iterator<Long> iterator = list.iterator();
            try {
                while (true) {
                    iterator.next().hashCode();
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
        return start.toDurationNanos();
    }

}
