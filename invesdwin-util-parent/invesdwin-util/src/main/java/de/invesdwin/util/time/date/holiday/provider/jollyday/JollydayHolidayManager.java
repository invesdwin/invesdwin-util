package de.invesdwin.util.time.date.holiday.provider.jollyday;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import de.jollyday.util.ResourceUtil;

@Immutable
public class JollydayHolidayManager implements IHolidayManager {

    private final String calendarId;
    private final HolidayManager holidayManager;

    public JollydayHolidayManager(final String calendarId) {
        this(calendarId, HolidayManager.getInstance(ManagerParameters.create(calendarId)));
    }

    public JollydayHolidayManager(final String calendarId, final HolidayManager holidayManager) {
        this.calendarId = calendarId;
        this.holidayManager = holidayManager;
    }

    public JollydayHolidayManager valueOf(final String calendarId) {
        return new JollydayHolidayManager(calendarId, HolidayManager.getInstance(ManagerParameters.create(calendarId)));
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return holidayManager.isHoliday(date.calendarValue());
    }

    @Override
    public String getHolidayCalendarId() {
        return calendarId;
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

    public String getDescription() {
        return extractDescription(holidayManager);
    }

    public static String extractDescription(final HolidayManager holidayManager) {
        final String description = holidayManager.getCalendarHierarchy().getDescription(Locale.US);
        if (Strings.equals(ResourceUtil.UNDEFINED, description)) {
            return null;
        } else {
            return description;
        }
    }

}
