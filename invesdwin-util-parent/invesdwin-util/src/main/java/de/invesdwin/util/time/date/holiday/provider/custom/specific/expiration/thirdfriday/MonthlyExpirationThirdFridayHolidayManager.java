package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.thirdfriday;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.AWeekdayOfMonthHolidayManager;

/**
 * Am kleinen Verfallstag verfallen an den Terminbörsen Optionen auf Aktien und Indizes. Im Gegensatz zum großen
 * Verfallstag (Hexensabbat) sind am kleinen Futures/Terminkontrakte nicht betroffen. Dementsprechend hat der kleine
 * Verfallstag eine geringere Wirkung auf die Börsenkurse als das große Pendant. Der kleine Verfallstag findet an jedem
 * dritten Freitag eines Monats statt.
 * 
 * Source: https://www.boerse.de/boersenlexikon/Kleiner-Verfallstag
 */
@Immutable
public final class MonthlyExpirationThirdFridayHolidayManager extends AWeekdayOfMonthHolidayManager {

    public static final MonthlyExpirationThirdFridayHolidayManager INSTANCE = new MonthlyExpirationThirdFridayHolidayManager();

    private MonthlyExpirationThirdFridayHolidayManager() {
        super(FWeekday.Friday, 3);
    }

    @Override
    public String getHolidayCalendarId() {
        return "MONTHLY_EXPIRATION_THIRD_FRIDAY";
    }

}
