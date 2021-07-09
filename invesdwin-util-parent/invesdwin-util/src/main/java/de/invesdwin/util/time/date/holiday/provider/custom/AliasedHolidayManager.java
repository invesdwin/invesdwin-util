package de.invesdwin.util.time.date.holiday.provider.custom;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public class AliasedHolidayManager implements IHolidayManager {

    private final String holidayCalendarId;
    private final IHolidayManager delegate;

    public AliasedHolidayManager(final String holidayCalendarId, final IHolidayManager delegate) {
        this.holidayCalendarId = holidayCalendarId;
        this.delegate = delegate;
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return delegate.isHoliday(date);
    }

    @Override
    public String getHolidayCalendarId() {
        return holidayCalendarId;
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
