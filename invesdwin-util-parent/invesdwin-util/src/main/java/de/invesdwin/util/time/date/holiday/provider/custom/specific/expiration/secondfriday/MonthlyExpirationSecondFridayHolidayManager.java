package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.secondfriday;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FWeekday;
import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.AWeekdayOfMonthHolidayManager;

/**
 * S&P e-mini expires on second friday of the month, not third friday as FDAX.
 */
@Immutable
public final class MonthlyExpirationSecondFridayHolidayManager extends AWeekdayOfMonthHolidayManager {

    public static final MonthlyExpirationSecondFridayHolidayManager INSTANCE = new MonthlyExpirationSecondFridayHolidayManager();

    private MonthlyExpirationSecondFridayHolidayManager() {
        super(FWeekday.Friday, 2);
    }

    @Override
    public String getHolidayCalendarId() {
        return "MONTHLY_EXPIRATION_DAY_SECOND_FRIDAY";
    }

}
