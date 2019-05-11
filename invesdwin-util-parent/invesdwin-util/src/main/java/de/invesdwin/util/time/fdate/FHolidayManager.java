package de.invesdwin.util.time.fdate;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.HolidayType;
import de.jollyday.ManagerParameters;

@ThreadSafe
public final class FHolidayManager {

    /**
     * http://zorro-project.com/manual/en/weekend.htm
     * 
     * Holidays on 1.January and 25.December only
     */
    public static final FHolidayManager ZORRO;
    public static final FHolidayManager GERMANY;

    private static final ALoadingCache<String, FHolidayManager> ID_MANAGER;

    static {
        ID_MANAGER = new ALoadingCache<String, FHolidayManager>() {

            @Override
            protected FHolidayManager loadValue(final String key) {
                try {
                    return new FHolidayManager(key);
                } catch (final Throwable t) {
                    throw new RuntimeException("Invalid " + FHolidayManager.class.getSimpleName() + " id: " + key);
                }
            }
        };
        ZORRO = getInstance("zorro");
        GERMANY = getInstance(HolidayCalendar.GERMANY);
    }

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

    private final String calendarId;

    private FHolidayManager(final String calendarId) {
        this.calendarId = calendarId;
        this.delegate = HolidayManager.getInstance(ManagerParameters.create(calendarId));
    }

    public String getCalendarId() {
        return calendarId;
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
