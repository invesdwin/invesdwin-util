package de.invesdwin.util.time;

import java.time.ZoneId;
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

    public static ZoneId getZoneId(final String id) {
        if (Strings.isBlank(id)) {
            return null;
        }
        final TimeZone timeZone = getTimeZoneOrNull(id);
        if (timeZone != null) {
            return timeZone.toZoneId();
        }
        //CHECKSTYLE:OFF
        final ZoneId tz = ZoneId.of(id);
        //CHECKSTYLE:ON
        return tz;
    }

    public static ZoneId getZoneIdOrNull(final String id) {
        if (Strings.isBlankOrNullText(id)) {
            return null;
        }
        try {
            final TimeZone timeZone = getTimeZoneOrNull(id);
            if (timeZone != null) {
                return timeZone.toZoneId();
            }
            //CHECKSTYLE:OFF
            final ZoneId tz = ZoneId.of(id);
            //CHECKSTYLE:ON
            return tz;
        } catch (final Throwable t) {
            return null;
        }
    }

    public static int getOffsetSeconds(final ZoneId timeZone, final long millis) {
        return timeZone.getRules().getOffset(java.time.Instant.ofEpochMilli(millis)).getTotalSeconds();
    }

}
