package de.invesdwin.util.time.date.timezone;

import java.time.ZoneId;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeZone;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;

@Immutable
public final class TimeZones {

    public static final TimeZone UTC;
    public static final TimeZone EET;
    public static final TimeZone CET;
    public static final TimeZone EUROPE_BERLIN;
    public static final TimeZone AMERICA_NEWYORK;

    private static final String[] SEARCH_PREFIXES;
    private static final String[] REPLACE_PREFIXES;

    static {
        SEARCH_PREFIXES = new String[] { "UTC-", "UTC+", "GMT-", "GMT+", "UT-", "UT+" };
        REPLACE_PREFIXES = new String[] { "UTC-", "UTC+", "UTC-", "UTC+", "UTC-", "UTC+" };

        //CHECKSTYLE:OFF
        UTC = TimeZone.getTimeZone("UTC");
        //CHECKSTYLE:ON
        EET = getTimeZone("EET");
        CET = getTimeZone("CET");
        EUROPE_BERLIN = getTimeZone("Europe/Berlin");
        AMERICA_NEWYORK = getTimeZone("America/New_York");
    }

    private TimeZones() {
    }

    public static TimeZone getTimeZone(final String id) {
        if (Strings.isBlank(id)) {
            return null;
        }
        final TimeZone tz = adjustedGetTimeZone(id);
        if (tz == null) {
            throw new IllegalArgumentException("Unable to parse: " + id);
        }
        Assertions.assertThat(tz.getID()).as("Invalid timeZoneId: %s", id).isEqualToIgnoringCase(id);
        return tz;
    }

    public static TimeZone getTimeZoneOrNull(final String id) {
        if (Strings.isBlankOrNullText(id)) {
            return null;
        }
        final TimeZone tz = adjustedGetTimeZone(id);
        if (tz == null || !tz.getID().equalsIgnoreCase(id)) {
            return null;
        }
        return tz;
    }

    public static FTimeZone getFTimeZone(final String timeZoneId) {
        final ZoneId zoneId = getZoneId(timeZoneId);
        if (zoneId == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            return new FTimeZone(zoneId);
            //CHECKSTYLE:ON
        }
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
            final String id = timeZone.getId();
            return adjustedGetTimeZone(id);
        }
    }

    private static TimeZone adjustedGetTimeZone(final String id) {
        final String adjId = maybeReplacePrefix(id).replace("UTC", "GMT");
        if ("GMT".equals(adjId)) {
            return UTC;
        }
        //CHECKSTYLE:OFF
        final TimeZone timeZone = TimeZone.getTimeZone(adjId);
        //CHECKSTYLE:ON
        if (timeZone.getID().equals("GMT") && !"GMT".equals(adjId)) {
            //java internal fallback was used
            return null;
        }
        return timeZone;
    }

    public static DateTimeZone getDateTimeZone(final ZoneId zoneId) {
        if (zoneId == null) {
            return null;
        } else {
            return DateTimeZone.forID(zoneId.getId());
        }
    }

}
