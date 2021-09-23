package de.invesdwin.util.collections.performance;

import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Ignore;
import org.junit.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
@Ignore("manual test")
public class CaffeinePerformanceTest extends ADatabasePerformanceTest {

    @Test
    public void testMapDbPerformance() throws InterruptedException {
        final Cache<FDate, FDate> table = Caffeine.newBuilder().<FDate, FDate> build();

        final LoopInterruptedCheck loopCheck = new LoopInterruptedCheck(Duration.ONE_SECOND);
        final Instant writesStart = new Instant();
        int i = 0;
        for (final FDate date : newValues()) {
            table.put(date, date);
            i++;
            if (i % FLUSH_INTERVAL == 0) {
                if (loopCheck.check()) {
                    printProgress("Writes", writesStart, i, VALUES);
                }
            }
        }
        printProgress("WritesFinished", writesStart, VALUES, VALUES);

        readGet(table);
    }

    private void readGet(final Cache<FDate, FDate> table) throws InterruptedException {
        final LoopInterruptedCheck loopCheck = new LoopInterruptedCheck(Duration.ONE_SECOND);
        final List<FDate> values = Lists.toList(newValues());
        final Instant readsStart = new Instant();
        for (int reads = 1; reads <= READS; reads++) {
            FDate prevValue = null;
            for (int i = 0; i < values.size(); i++) {
                try {
                    final FDate value = table.getIfPresent(values.get(i));
                    if (prevValue != null) {
                        Assertions.checkTrue(prevValue.isBefore(value));
                    }
                    prevValue = value;
                } catch (final NoSuchElementException e) {
                    break;
                }
            }
            if (loopCheck.check()) {
                printProgress("Gets", readsStart, VALUES * reads, VALUES * READS);
            }
        }
        printProgress("GetsFinished", readsStart, VALUES * READS, VALUES * READS);
    }

}
