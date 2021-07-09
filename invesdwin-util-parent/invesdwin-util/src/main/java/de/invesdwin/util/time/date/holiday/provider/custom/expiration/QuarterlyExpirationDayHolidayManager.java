package de.invesdwin.util.time.date.holiday.provider.custom.expiration;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FMonth;
import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

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
public final class QuarterlyExpirationDayHolidayManager implements IHolidayManager {

    public static final QuarterlyExpirationDayHolidayManager INSTANCE = new QuarterlyExpirationDayHolidayManager();

    private static final int DECEMBER = FMonth.December.indexValue();
    private static final int SEPTEMBER = FMonth.September.indexValue();
    private static final int JUNE = FMonth.June.indexValue();
    private static final int MARCH = FMonth.March.indexValue();

    private static final int FRIDAY = FWeekday.Friday.indexValue();
    private static final int THIRD_WEEK_OF_MONTH = 3;

    private QuarterlyExpirationDayHolidayManager() {
    }

    @Override
    public boolean isHoliday(final FDate date) {
        final int month = date.getMonth();
        if (month != MARCH && month != JUNE && month != SEPTEMBER && month != DECEMBER) {
            return false;
        }
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
        return "QUARTERLY_EXPIRATION_DAY";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
