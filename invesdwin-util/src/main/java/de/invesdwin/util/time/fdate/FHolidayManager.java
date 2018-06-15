package de.invesdwin.util.time.fdate;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;

@ThreadSafe
public final class FHolidayManager {

    private static final ALoadingCache<String, FHolidayManager> ID_MANAGER = new ALoadingCache<String, FHolidayManager>() {

        @Override
        protected FHolidayManager loadValue(final String key) {
            return new FHolidayManager(key);
        }
    };

    private final HolidayManager delegate;

    private final ALoadingCache<FDate, Boolean> day_holiday = new ALoadingCache<FDate, Boolean>() {
        @Override
        protected Boolean loadValue(final FDate key) {
            return delegate.isHoliday(key.calendarValue(), HolidayType.OFFICIAL_HOLIDAY);
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return 1000;
        }
    };

    private FHolidayManager(final String calendarId) {
        this.delegate = HolidayManager.getInstance(ManagerParameters.create(calendarId));
    }

    public boolean isHoliday(final FDate date) {
        return day_holiday.get(date.withoutTime());
    }

    public static FHolidayManager getInstance(final String holidayCalendarId) {
        if (holidayCalendarId == null) {
            return null;
        }
        return ID_MANAGER.get(holidayCalendarId);
    }

    public static FHolidayManager getInstance(final HolidayCalendar holidayCalendar) {
        if (holidayCalendar == null) {
            return null;
        }
        return getInstance(holidayCalendar.getId());
    }

}
