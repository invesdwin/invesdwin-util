package de.invesdwin.util.time.date.holiday.provider.strata;

import javax.annotation.concurrent.Immutable;

import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendars;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public class StrataHolidayManager implements IHolidayManager {

    private final String calendarId;
    private final HolidayCalendar holidayCalendar;

    public StrataHolidayManager(final String calendarId) {
        this(calendarId, HolidayCalendars.of(calendarId));
    }

    public StrataHolidayManager(final String calendarId, final HolidayCalendar holidayCalendar) {
        this.calendarId = calendarId;
        this.holidayCalendar = holidayCalendar;
    }

    @Override
    public String getHolidayCalendarId() {
        return calendarId;
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return holidayCalendar.isHoliday(date.javaDateValue());
    }

}
