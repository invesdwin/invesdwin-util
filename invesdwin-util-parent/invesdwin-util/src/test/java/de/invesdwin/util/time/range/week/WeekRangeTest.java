package de.invesdwin.util.time.range.week;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.millis.WeekAdjustment;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class WeekRangeTest {

    @Test
    public void testGetDuration() {
        final FWeekTime tueTen = new FWeekTime(FWeekday.Tuesday, 10, 0, 0, 0);
        final FWeekTime satTwenty = new FWeekTime(FWeekday.Saturday, 20, 0, 0, 0);
        Assertions.assertThat(new WeekRange(tueTen, satTwenty).getDuration())
                .isEqualTo(new Duration(4, FTimeUnit.DAYS).add(new Duration(10, FTimeUnit.HOURS)));
        Assertions.assertThat(new WeekRange(satTwenty, tueTen).getDuration())
                .isEqualTo(new Duration(2, FTimeUnit.DAYS).add(new Duration(14, FTimeUnit.HOURS)));
    }

    @Test
    public void testParse() {
        final FDate sunday = new FDate().setFWeekday(FWeekday.Sunday, WeekAdjustment.PREVIOUS);
        final WeekRange range = new WeekRange(new FWeekTime(sunday.addDays(-1)), new FWeekTime(sunday));
        final String str = range.toString();
        final WeekRange parsed = WeekRange.valueOf(str);
        Assertions.checkEquals(range, parsed);
        Assertions.checkEquals(str, parsed.toString());
    }

}
