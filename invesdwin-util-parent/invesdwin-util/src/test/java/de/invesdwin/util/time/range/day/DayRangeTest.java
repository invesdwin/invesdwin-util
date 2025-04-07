package de.invesdwin.util.time.range.day;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DayRangeTest {

    @Test
    public void testGetDuration() {
        final FDayTime ten = new FDayTime(10, 0, 0, 0);
        final FDayTime twenty = new FDayTime(20, 0, 0, 0);
        Assertions.assertThat(new DayRange(ten, twenty).getDuration()).isEqualTo(new Duration(10, FTimeUnit.HOURS));
        Assertions.assertThat(new DayRange(twenty, ten).getDuration()).isEqualTo(new Duration(14, FTimeUnit.HOURS));
    }

}
