package de.invesdwin.util.time.date.holiday;

import de.invesdwin.util.time.date.FDate;

public interface IHolidayManager {

    boolean isHoliday(long millis);

    boolean isHoliday(FDate date);

    String getHolidayCalendarId();

}
