package de.invesdwin.util.time.date.holiday.provider;

import de.invesdwin.util.time.date.holiday.IHolidayManager;

public interface IHolidayManagerProvider {

    String AVAILABLE_CALENDAR_ID_SEPARATOR = " | ";

    IHolidayManager getInstance(String holidayCalendarId);

    String lookupHolidayCalendarId(String holidayCalendarId);

    String getAvailableCalendarIdsInfo();

}
