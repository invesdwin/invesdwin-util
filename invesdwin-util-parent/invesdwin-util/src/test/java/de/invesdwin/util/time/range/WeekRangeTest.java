package de.invesdwin.util.time.range;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FWeekTime;

@NotThreadSafe
public class WeekRangeTest {

    @Test
    public void testParse() {
        final WeekRange range = new WeekRange(new FWeekTime(new FDate().addDays(-1)), new FWeekTime(new FDate()));
        final String str = range.toString();
        final WeekRange parsed = WeekRange.valueOf(str);
        Assertions.checkEquals(range, parsed);
        Assertions.checkEquals(str, parsed.toString());
    }

}
