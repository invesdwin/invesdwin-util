package de.invesdwin.util.time;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;

@Immutable
public final class TimeZones {

    public static final TimeZone UTC = getTimeZone("UTC");

    private TimeZones() {
    }

    public static TimeZone getTimeZone(final String id) {
        if (Strings.isBlank(id)) {
            return null;
        }
        //CHECKSTYLE:OFF
        final TimeZone tz = TimeZone.getTimeZone(id);
        //CHECKSTYLE:ON
        Assertions.assertThat(tz.getID()).as("Invalid timeZoneId: %s", id).isEqualToIgnoringCase(id);
        return tz;
    }

    public static TimeZone getTimeZoneOrNull(final String id) {
        if (Strings.isBlankOrNullText(id)) {
            return null;
        }
        //CHECKSTYLE:OFF
        final TimeZone tz = TimeZone.getTimeZone(id);
        //CHECKSTYLE:ON
        if (!tz.getID().equalsIgnoreCase(id)) {
            return null;
        }
        return tz;
    }

}
