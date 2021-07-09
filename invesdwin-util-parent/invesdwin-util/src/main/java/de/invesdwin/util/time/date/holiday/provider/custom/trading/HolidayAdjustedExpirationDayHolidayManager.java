package de.invesdwin.util.time.date.holiday.provider.custom.trading;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.holiday.IHolidayManager;

/**
 * Fällt der dritte Freitag des dritten Monats eines Quartals auf einen Feiertag, so gelten besondere Regelungen. An der
 * Terminbörse Eurex ist der Verfallstag für viele Produkte dann der davor liegende Börsentag.
 * 
 * Dem großen Verfallstag steht der kleine Verfallstag gegenüber, der auf den dritten Freitag jedes Monats bzw. bei
 * Feiertagen auch auf den davor liegenden Börsentag fällt. An diesem Tag laufen einige Serien von Terminprodukten aus.
 * 
 * Source: https://de.wikipedia.org/wiki/Hexensabbat_(B%C3%B6rse)
 */
@Immutable
public class HolidayAdjustedExpirationDayHolidayManager implements IHolidayManager {

    private final IHolidayManager holidays;
    private final IHolidayManager expirationDays;

    public HolidayAdjustedExpirationDayHolidayManager(final IHolidayManager holidays,
            final IHolidayManager expirationDays) {
        this.holidays = holidays;
        this.expirationDays = expirationDays;
    }

    @Override
    public boolean isHoliday(final FDate date) {
        if (holidays.isHoliday(date)) {
            return false;
        }
        if (expirationDays.isHoliday(date)) {
            return true;
        }
        final FDate nextDay = date.addDays(1);
        if (expirationDays.isHoliday(nextDay) && holidays.isHoliday(nextDay)) {
            //one day earlier
            return true;
        }
        return false;
    }

    @Override
    public String getHolidayCalendarId() {
        return expirationDays.getHolidayCalendarId() + "[" + holidays.getHolidayCalendarId() + "]";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
