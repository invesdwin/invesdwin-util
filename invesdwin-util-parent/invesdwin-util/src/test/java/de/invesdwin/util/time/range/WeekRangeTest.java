package de.invesdwin.util.time.range;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FWeekTime;
import de.invesdwin.util.time.date.FWeekday;

@NotThreadSafe
public class WeekRangeTest {

    @Test
    public void testParse() {
        final FDate sunday = new FDate().setFWeekday(FWeekday.Sunday);
        final WeekRange range = new WeekRange(new FWeekTime(sunday.addDays(-1)), new FWeekTime(sunday));
        final String str = range.toString();
        final WeekRange parsed = WeekRange.valueOf(str);
        Assertions.checkEquals(range, parsed);
        Assertions.checkEquals(str, parsed.toString());
    }

}
