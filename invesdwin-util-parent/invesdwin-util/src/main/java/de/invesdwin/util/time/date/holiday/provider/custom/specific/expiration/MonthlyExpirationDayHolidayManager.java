package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FWeekday;

/**
 * Am kleinen Verfallstag verfallen an den Terminbörsen Optionen auf Aktien und Indizes. Im Gegensatz zum großen
 * Verfallstag (Hexensabbat) sind am kleinen Futures/Terminkontrakte nicht betroffen. Dementsprechend hat der kleine
 * Verfallstag eine geringere Wirkung auf die Börsenkurse als das große Pendant. Der kleine Verfallstag findet an jedem
 * dritten Freitag eines Monats statt.
 * 
 * Source: https://www.boerse.de/boersenlexikon/Kleiner-Verfallstag
 */
@Immutable
public final class MonthlyExpirationDayHolidayManager extends AWeekdayOfMonthHolidayManager {

    public static final MonthlyExpirationDayHolidayManager INSTANCE = new MonthlyExpirationDayHolidayManager();

    private MonthlyExpirationDayHolidayManager() {
        super(FWeekday.Friday, 3);
    }

    @Override
    public String getHolidayCalendarId() {
        return "MONTHLY_EXPIRATION_DAY";
    }

}
