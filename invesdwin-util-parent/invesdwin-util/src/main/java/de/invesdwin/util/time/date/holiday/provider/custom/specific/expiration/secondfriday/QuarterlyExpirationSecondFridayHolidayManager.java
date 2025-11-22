package de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.secondfriday;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.holiday.provider.custom.specific.expiration.AQuarterlyWeekdayOfMonthHolidayManager;

/**
 * S&P e-mini expires on second friday of the month, not third friday as FDAX.
 */
@Immutable
public final class QuarterlyExpirationSecondFridayHolidayManager extends AQuarterlyWeekdayOfMonthHolidayManager {

    public static final String HOLIDAY_CALENDAR_ID = "QUARTERLY_EXPIRATION_SECOND_FRIDAY";

    public static final QuarterlyExpirationSecondFridayHolidayManager INSTANCE = new QuarterlyExpirationSecondFridayHolidayManager();

    private QuarterlyExpirationSecondFridayHolidayManager() {
        super(MonthlyExpirationSecondFridayHolidayManager.INSTANCE.getWeekday(),
                MonthlyExpirationSecondFridayHolidayManager.INSTANCE.getWeekNumberOfMonth());
    }

    @Override
    public String getHolidayCalendarId() {
        return HOLIDAY_CALENDAR_ID;
    }

}
