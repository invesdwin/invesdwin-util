package de.invesdwin.util.time.date.holiday;

import de.invesdwin.util.time.date.FDate;

public interface IHolidayManager {

    default boolean isHoliday(final long millis) {
        return isHoliday(new FDate(millis));
    }

    boolean isHoliday(FDate date);

    String getHolidayCalendarId();

}
