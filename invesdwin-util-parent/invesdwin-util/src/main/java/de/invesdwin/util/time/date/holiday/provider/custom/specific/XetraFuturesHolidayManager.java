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
public class XetraFuturesHolidayManager implements IHolidayManager {

    public static final XetraFuturesHolidayManager INSTANCE = new XetraFuturesHolidayManager();

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager holidays = HolidayManagers.XETRA;

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (holidays.isHoliday(date)) {
            return true;
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return "XETRA_FUTURES";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
