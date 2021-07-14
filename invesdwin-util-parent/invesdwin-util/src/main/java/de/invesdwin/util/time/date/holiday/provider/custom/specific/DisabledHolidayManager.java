package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

@Immutable
public final class DisabledHolidayManager implements IHolidayManager {

    public static final DisabledHolidayManager INSTANCE = new DisabledHolidayManager();

    private DisabledHolidayManager() {
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return "DISABLED";
    }

}
