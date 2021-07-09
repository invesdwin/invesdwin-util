package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FMonth;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

/**
 * Der 24. Mai, 24. Dezember sowie der 31. Dezember sind in 2021 Erfüllungstage.
 * 
 * Source: https://www.xetra.com/xetra-de/newsroom/handelskalender
 */
@Immutable
public final class NewYearsEveHolidayManager implements IHolidayManager {

    public static final NewYearsEveHolidayManager INSTANCE = new NewYearsEveHolidayManager();

    private static final int DECEMBER = FMonth.December.indexValue();
    private static final int NEW_YEARS_EVE_DAY_OF_MONTH = 31;

    private NewYearsEveHolidayManager() {
    }

    @Override
    public boolean isHoliday(final FDate date) {
        return date.getMonth() == DECEMBER && date.getDay() == NEW_YEARS_EVE_DAY_OF_MONTH;
    }

    @Override
    public String getHolidayCalendarId() {
        return "NEW_YEARS_EVE";
    }

}
