package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public class EurexFuturesWithRolloversHolidayManager implements IHolidayManager {

    public static final EurexFuturesWithRolloversHolidayManager INSTANCE = new EurexFuturesWithRolloversHolidayManager();

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager holidays = HolidayManagers.EUREX;
    private final IHolidayManager quarterlyExpirationDay = new HolidayAdjustedExpirationDayHolidayManager(holidays,
            QuarterlyExpirationDayHolidayManager.INSTANCE);

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (holidays.isHoliday(date)) {
            return true;
        }
        if (quarterlyExpirationDay.isHoliday(date)) {
            return true;
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return "EUREX_FUTURES_WITH_ROLLOVERS";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}