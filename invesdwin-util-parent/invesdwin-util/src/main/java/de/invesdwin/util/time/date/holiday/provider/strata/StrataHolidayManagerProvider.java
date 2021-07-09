package de.invesdwin.util.time.date.holiday.provider.strata;

import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendars;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.holiday.provider.IHolidayManagerProvider;
import de.invesdwin.util.time.date.holiday.provider.custom.CachingHolidayManager;

@Immutable
public final class StrataHolidayManagerProvider implements IHolidayManagerProvider {

    public static final StrataHolidayManagerProvider INSTANCE = new StrataHolidayManagerProvider();

    private static final ALoadingCache<String, Optional<IHolidayManager>> ID_MANAGER = new ALoadingCache<String, Optional<IHolidayManager>>() {
        @Override
        protected Optional<IHolidayManager> loadValue(final String key) {
            try {
                return Optional.of(new CachingHolidayManager(new StrataHolidayManager(key)));
            } catch (final Throwable t) {
                return Optional.empty();
            }
        }
    };
    @GuardedBy("none for performance")
    private static String availableCalendarIdsInfo;

    private StrataHolidayManagerProvider() {
    }

    @Override
    public IHolidayManager getInstance(final String calendarId) {
        return ID_MANAGER.get(calendarId).orElse(null);
    }

    @Override
    public String getAvailableCalendarIdsInfo() {
        if (availableCalendarIdsInfo == null) {
            availableCalendarIdsInfo = newAvailableCalendarIdsInfo();
        }
        return availableCalendarIdsInfo;
    }

    private String newAvailableCalendarIdsInfo() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, HolidayCalendar> entry : HolidayCalendars.extendedEnum()
                .lookupAllNormalized()
                .entrySet()) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            final String calId = prepareHolidayCalendarId(entry.getKey());
            sb.append(calId);
        }
        return sb.toString();
    }

    public static String prepareHolidayCalendarId(final String holidayCalendarId) {
        if (Strings.isBlank(holidayCalendarId)) {
            return null;
        }
        return holidayCalendarId.trim().toUpperCase();
    }

    @Override
    public String lookupHolidayCalendarId(final String holidayCalendarId) {
        final String prepared = prepareHolidayCalendarId(holidayCalendarId);
        if (getInstance(prepared) != null) {
            return prepared;
        } else {
            return null;
        }
    }

}
