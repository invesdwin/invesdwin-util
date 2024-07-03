package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

public class EurexFuturesHolidayWithRolloversManager implements IHolidayManager {

    public static final EurexFuturesHolidayManager INSTANCE = new EurexFuturesHolidayManager();

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager holidaysAndQuarterlyExpirationDays = new HolidayAdjustedExpirationDayHolidayManager(
            HolidayManagers.EUREX, QuarterlyExpirationDayHolidayManager.INSTANCE);

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (holidaysAndQuarterlyExpirationDays.isHoliday(date)) {
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