package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

/**
 * https://www.xetra.com/xetra-de/newsroom/handelskalender
 *
 * also weekends and quarterly rollovers are treated as holidays
 */
@Immutable
public class XetraFuturesWithRolloversHolidayManager implements IHolidayManager {

    public static final XetraFuturesWithRolloversHolidayManager INSTANCE = new XetraFuturesWithRolloversHolidayManager();

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager xetra = HolidayManagers.XETRA;
    private final IHolidayManager quarterlyExpirationDay = new HolidayAdjustedExpirationDayHolidayManager(xetra,
            QuarterlyExpirationDayHolidayManager.INSTANCE);

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (xetra.isHoliday(date)) {
            return true;
        }
        if (quarterlyExpirationDay.isHoliday(date)) {
            return true;
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return "XETRA_FUTURES_WITH_ROLLOVERS";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
