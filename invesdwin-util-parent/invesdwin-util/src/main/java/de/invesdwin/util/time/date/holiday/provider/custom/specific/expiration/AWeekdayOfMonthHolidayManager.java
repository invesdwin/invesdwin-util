package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.millis.FDateMillis;

@Immutable
public abstract class AWeekdayOfMonthHolidayManager implements IHolidayManager {

    private final FWeekday weekday;
    private final int weekNumberOfMonth;
    private final int addWeeksAfterFirst;

    protected AWeekdayOfMonthHolidayManager(final FWeekday weekday, final int weekNumberOfMonth) {
        this.weekday = weekday;
        Assertions.assertThat(weekNumberOfMonth).isGreaterThanOrEqualTo(1);
        this.weekNumberOfMonth = weekNumberOfMonth;
        this.addWeeksAfterFirst = weekNumberOfMonth - 1;
    }

    public FWeekday getWeekday() {
        return weekday;
    }

    public int getWeekNumberOfMonth() {
        return weekNumberOfMonth;
    }

    @Override
    public boolean isHoliday(final FDate date) {
        final int weekday = date.getWeekday();
        if (weekday != this.weekday.indexValue()) {
            return false;
        }
        final long dayMillis = FDateMillis.withoutTime(date.millisValue());
        final long firstWeekdayOfMonth = FDateMillis.getFirstWeekdayOfMonth(dayMillis, this.weekday);
        final long targetWeekdayOfMonth = FDateMillis.addWeeks(firstWeekdayOfMonth, addWeeksAfterFirst);
        if (dayMillis != targetWeekdayOfMonth) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
