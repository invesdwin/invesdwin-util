package de.invesdwin.util.time;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeZone;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;

@Immutable
public final class TimeZones {

    public static final TimeZone UTC = getTimeZone("UTC");
    public static final TimeZone EET = getTimeZone("EET");

    private static final String[] SEARCH_PREFIXES = new String[] { "UTC-", "UTC+", "GMT-", "GMT+", "UT-", "UT+" };
    private static final String[] REPLACE_PREFIXES = new String[] { "UTC-", "UTC+", "UTC-", "UTC+", "UTC-", "UTC+" };

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
        if (Strings.isBlankOrNullText(id)) {
            return null;
        }
        final String adjId = maybeReplacePrefix(id);
        final TimeZone timeZone = getTimeZoneOrNull(adjId);

        ZoneId tz = null;
        if (timeZone != null) {
            tz = timeZone.toZoneId();
        }
        if (timeZone == null) {
            //CHECKSTYLE:OFF
            tz = ZoneId.of(adjId);
            //CHECKSTYLE:ON
        }
        return maybeNormalizeZoneId(tz);
    }

    private static ZoneId maybeNormalizeZoneId(final ZoneId tz) {
        final String tzId = tz.getId();
        final String adjTzId = maybeReplacePrefix(tzId);
        if (!tzId.equals(adjTzId)) {
            //this adjusted zone id exists for sure
            return getZoneId(adjTzId);
        } else {
            return tz;
        }
    }

    private static String maybeReplacePrefix(final String id) {
        if (Strings.startsWithAny(id, "-", "+")) {
            return "UTC" + id;
        }
        for (int i = 0; i < SEARCH_PREFIXES.length; i++) {
            final String searchPrefix = SEARCH_PREFIXES[i];
            if (Strings.startsWithIgnoreCase(id, searchPrefix)) {
                final String replacePrefix = REPLACE_PREFIXES[i];
                return replacePrefix + Strings.removeStart(id, searchPrefix.length());
            }
        }
        if (Strings.equalsAnyIgnoreCase(id, "UTC", "GMT", "UT")) {
            return "UTC";
        }
        return id;
    }

    public static ZoneId getZoneIdOrNull(final String id) {
        if (Strings.isBlankOrNullText(id)) {
            return null;
        }
        try {
            return getZoneId(id);
        } catch (final Throwable t) {
            return null;
        }
    }

    public static int getOffsetSeconds(final ZoneId timeZone, final long millis) {
        return timeZone.getRules().getOffset(java.time.Instant.ofEpochMilli(millis)).getTotalSeconds();
    }

    public static TimeZone getTimeZone(final ZoneId timeZone) {
        if (timeZone == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            return TimeZone.getTimeZone(timeZone);
            //CHECKSTYLE:ON
        }
    }

    public static DateTimeZone getDateTimeZone(final ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        } else {
            return DateTimeZone.forID(zoneId.getId());
        }
    }

}
