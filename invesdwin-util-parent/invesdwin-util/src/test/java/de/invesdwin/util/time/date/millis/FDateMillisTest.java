package de.invesdwin.util.time.date.millis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class FDateMillisTest {

    @Test
    public void testWeekOfMonth() {
        assertEquals(1, FDateBuilder.newDate(2024, 6, 1).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 2).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 3).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 4).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 5).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 6).getWeekNumberOfMonth());
        assertEquals(1, FDateBuilder.newDate(2024, 6, 7).getWeekNumberOfMonth());
        assertEquals(2, FDateBuilder.newDate(2024, 6, 8).getWeekNumberOfMonth());
        assertEquals(2, FDateBuilder.newDate(2024, 6, 9).getWeekNumberOfMonth());
        assertEquals(2, FDateBuilder.newDate(2024, 6, 10).getWeekNumberOfMonth());
    }
}
