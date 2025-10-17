package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FMonth;
import de.invesdwin.util.time.date.FWeekday;

@Immutable
public abstract class AQuarterlyWeekdayOfMonthHolidayManager extends AWeekdayOfMonthHolidayManager {

    private static final int DECEMBER = FMonth.December.indexValue();
    private static final int SEPTEMBER = FMonth.September.indexValue();
    private static final int JUNE = FMonth.June.indexValue();
    private static final int MARCH = FMonth.March.indexValue();

    protected AQuarterlyWeekdayOfMonthHolidayManager(final FWeekday weekday, final int weekNumberOfMonth) {
        super(weekday, weekNumberOfMonth);
    }

    @Override
    public boolean isHoliday(final FDate date) {
        final int month = date.getMonth();
        if (month != MARCH && month != JUNE && month != SEPTEMBER && month != DECEMBER) {
            return false;
        }
        return super.isHoliday(date);
    }

}
