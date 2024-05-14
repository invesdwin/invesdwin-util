package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public class EurexHolidayManager implements IHolidayManager {

    public static final EurexHolidayManager INSTANCE = new EurexHolidayManager();

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager eurex = HolidayManagers.EUREX;
    private final IHolidayManager quarterlyExpirationDay = new HolidayAdjustedExpirationDayHolidayManager(eurex,
            QuarterlyExpirationDayHolidayManager.INSTANCE);

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (eurex.isHoliday(date)) {
            return true;
        }
        if (quarterlyExpirationDay.isHoliday(date)) {
            return true;
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return "EUREX";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
