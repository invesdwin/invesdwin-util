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
        for (final String prefix : new String[] { "UTC-", "UTC+", "GMT-", "GMT+", "UT-", "UT+" }) {
            if (Strings.startsWithIgnoreCase(id, prefix)) {
                return prefix + Strings.removeStart(id, prefix.length());
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

}
