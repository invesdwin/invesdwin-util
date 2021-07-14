package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public final class WeekendHolidayManager implements IHolidayManager {

    public static final WeekendHolidayManager INSTANCE = new WeekendHolidayManager();

    private WeekendHolidayManager() {
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return date.getFWeekday().isWeekend();
    }

    @Override
    public String getHolidayCalendarId() {
        return "WEEKEND";
    }

}
