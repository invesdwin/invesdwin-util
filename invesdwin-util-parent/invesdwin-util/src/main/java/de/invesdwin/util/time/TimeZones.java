package de.invesdwin.util.time;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;

@Immutable
public final class TimeZones {

    public static final TimeZone UTC = getTimeZone("UTC");

    private TimeZones() {}

    public static TimeZone getTimeZone(final String id) {
        //CHECKSTYLE:OFF
        final TimeZone tz = TimeZone.getTimeZone(id);
        //CHECKSTYLE:ON
        Assertions.assertThat(tz.getID()).isEqualToIgnoringCase(id);
        return tz;
    }

}
