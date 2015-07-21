package de.invesdwin.util.time;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDateBuilder;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public class DatesTest {

    @Test
    public void testIterable() {
        int iterationen = 0;
        for (final FDate date : FDate.iterable(new FDate(), new FDate(), FTimeUnit.Days, 1)) {
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterationen++;
        }
        Assertions.assertThat(iterationen).isEqualTo(1);
    }

    @Test
    public void testIterableYearly() {
        int iterations = 0;
        final FDate endDate = FDateBuilder.newDate(2012, 1, 27);
        final FDate startDate = FDateBuilder.newDate(1990, 1, 1);
        FDate lastDate = null;
        for (final FDate date : FDate.iterable(startDate, endDate, FTimeUnit.Years, 1)) {
            if (lastDate == null) {
                Assertions.assertThat(FDate.isSameMillisecond(startDate, date)).isTrue();
            }
            lastDate = date;
            System.out.println(String.format("%s", date)); //SUPPRESS CHECKSTYLE single line
            iterations++;
        }
        Assertions.assertThat(iterations).isEqualTo(24);
        Assertions.assertThat(FDate.isSameMillisecond(endDate, lastDate)).isTrue();
    }

}
