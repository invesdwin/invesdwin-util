package de.invesdwin.util.time.date;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
@Disabled
public class FDatesRegexTest {

    private static final int COUNT_LOOPS = 100000;

    @Test
    public void testRegexVsParserPerformance() throws InterruptedException {
        final AtomicLong parser = new AtomicLong();
        final AtomicLong regex = new AtomicLong();
        final int runs = 100;
        final Runnable dateT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    parser.addAndGet(performanceTestParser(i));
                }
            };
        };
        final Runnable longT = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    regex.addAndGet(performanceTestRegex(i));
                }
            };
        };
        final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool("testRegexVsParserPerformance");
        Futures.submitAndWait(newCachedThreadPool, Arrays.asList(dateT, longT));
        newCachedThreadPool.shutdown();
        newCachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        final boolean dateIsFaster = parser.get() < regex.get();
        System.out.println("regex: " + new Duration(regex.get(), FTimeUnit.NANOSECONDS));//SUPPRESS CHECKSTYLE single line
        System.out.println("parser: " + new Duration(parser.get(), FTimeUnit.NANOSECONDS));//SUPPRESS CHECKSTYLE single line
        if (dateIsFaster) {
            System.out.println(String.format("parserIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(regex.get() - parser.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(parser.get()), Decimal.valueOf(regex.get()))
                            .getValue(PercentScale.PERCENT)));
        } else {
            System.out.println(String.format("regexIsFaster " //SUPPRESS CHECKSTYLE single line
                    + TimeUnit.MILLISECONDS.convert(parser.get() - regex.get(), TimeUnit.NANOSECONDS) + "ms "
                    + new Percent(Decimal.valueOf(regex.get()), Decimal.valueOf(parser.get()))
                            .getValue(PercentScale.PERCENT)));
        }
    }

    private static long performanceTestParser(final int curTest) {
        final Instant start = new Instant();
        long countTrue = 0;
        for (int i = 0; i < COUNT_LOOPS; i++) {
            if (FDates.startsWithIsoDateTimeMsFormat("ASDF2016-08-04T06:05:00.000ASDF", 4)) {
                countTrue++;
            }
        }
        Assertions.checkEquals(COUNT_LOOPS, countTrue);
        return start.toDurationNanos();
    }

    private static long performanceTestRegex(final int curTest) {
        final Instant start = new Instant();
        long countTrue = 0;
        //"(.*)(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3})(.*)(-)([CW]{0,1})(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3})(.*)"
        //".*[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9].[0-9][0-9][0-9].*"
        final Pattern pattern = Pattern.compile("(.*)(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3})(.*)");
        final Matcher matcher = pattern.matcher("ASDF2016-08-04T06:05:00.000ASDF");
        for (int i = 0; i < COUNT_LOOPS; i++) {
            if (matcher.matches()) {
                countTrue++;
            }
            matcher.reset("ASDF2016-08-04T06:05:00.000ASDF");
        }
        Assertions.checkEquals(COUNT_LOOPS, countTrue);
        return start.toDurationNanos();
    }

}
