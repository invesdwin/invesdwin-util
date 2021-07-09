package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FMonth;
import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

/**
 * Am kleinen Verfallstag verfallen an den Terminbörsen Optionen auf Aktien und Indizes. Im Gegensatz zum großen
 * Verfallstag (Hexensabbat) sind am kleinen Futures/Terminkontrakte nicht betroffen. Dementsprechend hat der kleine
 * Verfallstag eine geringere Wirkung auf die Börsenkurse als das große Pendant. Der kleine Verfallstag findet an jedem
 * dritten Freitag eines Monats statt.
 * 
 * Source: https://www.boerse.de/boersenlexikon/Kleiner-Verfallstag
 */
@Immutable
public final class MonthlyExpirationDayHolidayManager implements IHolidayManager {

    public static final MonthlyExpirationDayHolidayManager INSTANCE = new MonthlyExpirationDayHolidayManager();

    private static final int DECEMBER = FMonth.December.indexValue();
    private static final int SEPTEMBER = FMonth.September.indexValue();
    private static final int JUNE = FMonth.June.indexValue();
    private static final int MARCH = FMonth.March.indexValue();

    private static final int FRIDAY = FWeekday.Friday.indexValue();
    private static final int THIRD_WEEK_OF_MONTH = 3;

    private MonthlyExpirationDayHolidayManager() {
    }

    @Override
    public boolean isHoliday(final FDate date) {
        final int weekday = date.getWeekday();
        if (weekday != FRIDAY) {
            return false;
        }
        final int weekNumberOfMonth = date.getWeekNumberOfMonth();
        if (weekNumberOfMonth != THIRD_WEEK_OF_MONTH) {
            return false;
        }
        return true;
    }

    @Override
    public String getHolidayCalendarId() {
        return "MONTHLY_EXPIRATION_DAY";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
