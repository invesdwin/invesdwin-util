package de.invesdwin.util.time.range.week;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class FWeekTimeTest {

    @Test
    public void testParse() {
        final FWeekTime value = new FWeekTime(new FDate().addDays(-1));
        final String str = value.toString();
        final FWeekTime parsed = FWeekTime.valueOf(str, false);
        Assertions.checkEquals(value, parsed);
        Assertions.checkEquals(str, parsed.toString());
    }

    @Test
    public void testParseLong() {
        final FWeekTime value = new FWeekTime(new FDate().addDays(-1));
        final long num = value.longValue();
        final FWeekTime parsed = FWeekTime.valueOf(num, false);
        Assertions.checkEquals(value, parsed);
        Assertions.checkEquals(num, parsed.longValue());
    }

}
