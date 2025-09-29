package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.thirdfriday;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.AQuarterlyWeekdayOfMonthHolidayManager;

/**
 * Als dreifacher Hexensabbat (Tripe-Witching-Day) wird der große Verfallstag an den Terminbörsen bezeichnet, der
 * viermal im Jahr - stets am dritten Freitag der Monate März, Juni, September und Dezember – stattfindet. Dabei laufen
 * an der deutschen Terminbörse (Eurex) alle vier Derivate-Typen – Optionen und Futures auf Indizes und einzelne Aktien
 * – gleichzeitig aus. Um 12.00 Uhr verfallen zunächst die Index-Optionen, um 13.00 Uhr folgen die Futures sowie
 * Optionen auf den Dax und den TecDAX. Erst gegen Ende des Handelstages um 17.30 Uhr laufen dann die Optionen und
 * Futures auf einzelne Aktien aus. Üblicherweise wird der Hexensabbat von vergleichsweise hohen Umsätzen und
 * gesteigerter Volatilität begleitet. Dabei kann es zu deutlichen Kursschwankungen kommen, die nicht auf Unternehmens-
 * oder Konjunkturnachrichten zurückzuführen sind. Der Hexensabbat findet an fast jeder Terminbörse weltweit statt.
 * Pendant zum dreifachen Hexensabbat ist der kleine Verfallstag.
 * 
 * Source: https://www.boerse.de/boersenlexikon/Hexensabbat
 */
@Immutable
public final class QuarterlyExpirationThirdFridayHolidayManager extends AQuarterlyWeekdayOfMonthHolidayManager {

    public static final String HOLIDAY_CALENDAR_ID = "QUARTERLY_EXPIRATION_THIRD_FRIDAY";

    public static final QuarterlyExpirationThirdFridayHolidayManager INSTANCE = new QuarterlyExpirationThirdFridayHolidayManager();

    private QuarterlyExpirationThirdFridayHolidayManager() {
        super(MonthlyExpirationThirdFridayHolidayManager.INSTANCE.getWeekday(),
                MonthlyExpirationThirdFridayHolidayManager.INSTANCE.getWeekNumberOfMonth());
    }

    @Override
    public String getHolidayCalendarId() {
        return HOLIDAY_CALENDAR_ID;
    }

}
