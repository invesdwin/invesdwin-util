package de.invesdwin.util.time.date.holiday.provider.custom.specific;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateField;
import de.invesdwin.util.time.date.holiday.HolidayManagers;
import de.invesdwin.util.time.date.holiday.IHolidayManager;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.range.day.FDayTime;

@Immutable
public class EurexFuturesHolidayManager implements IHolidayManager {

    public static final EurexFuturesHolidayManager INSTANCE = new EurexFuturesHolidayManager();

    /**
     * Config-declared Eurex regular derivatives session open — 08:50 Europe/Berlin (CET/CEST wall clock). The
     * calendar itself (weekend + EUREX jollyday holidays) is day-granular; the begin time is a calendar constant,
     * not provider data.
     */
    public static final FDayTime SESSION_OPEN = new FDayTime(8, 50, 0, 0, 0, 0, 0);

    /**
     * Wall-clock zone the {@link #SESSION_OPEN} constant is expressed in.
     */
    public static final FTimeZone SESSION_OPEN_TIMEZONE = FTimeZone.EUROPE_BERLIN;

    /**
     * Bounded day-walk back to the previous open session day (covers holidays plus a long weekend; beyond this
     * window the search reports empty instead of looping unbounded).
     */
    public static final int MAX_SESSION_OPEN_LOOKBACK_DAYS = 30;

    /**
     * Local-noon day probe for the day-granular holiday check (stable calendar-day identity between the session
     * timezone and the host default timezone).
     */
    private static final FDayTime DAY_PROBE = new FDayTime(12, 0, 0, 0, 0, 0, 0);

    private final IHolidayManager weekend = WeekendHolidayManager.INSTANCE;
    private final IHolidayManager holidays = HolidayManagers.EUREX;

    @Override
    public boolean isHoliday(final FDate date) {
        if (weekend.isHoliday(date)) {
            return true;
        }
        if (holidays.isHoliday(date)) {
            return true;
        }
        return false;
    }

    /**
     * Session-open arm for this calendar: walks the candidate days back from the probe day and answers the first
     * non-holiday day's {@link #SESSION_OPEN} at or before the probe. The day-level holiday check is probed at
     * local noon (weekend/jollyday arms are day-granular; a noon probe keeps the calendar day identity stable
     * across the host timezone vs. the session timezone).
     */
    @Override
    public Optional<FDate> getOrPreviousSessionOpen(final FDate date) {
        if (date == null) {
            return Optional.empty();
        }
        final FDate probeDayStart = date.withoutTime(SESSION_OPEN_TIMEZONE);
        for (int daysBack = 0; daysBack <= MAX_SESSION_OPEN_LOOKBACK_DAYS; daysBack++) {
            final FDate candidateDayStart = probeDayStart.add(FDateField.Day, -daysBack, SESSION_OPEN_TIMEZONE);
            final FDate sessionOpen = candidateDayStart.setFDayTime(SESSION_OPEN, SESSION_OPEN_TIMEZONE);
            if (sessionOpen.isAfterNotNullSafe(date)) {
                //the probe sits before this day's open — continue to the previous day
                continue;
            }
            final FDate dayProbe = candidateDayStart.setFDayTime(DAY_PROBE, SESSION_OPEN_TIMEZONE);
            if (!isHoliday(dayProbe)) {
                return Optional.of(sessionOpen);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getHolidayCalendarId() {
        return "EUREX_FUTURES";
    }

    @Override
    public String toString() {
        return getHolidayCalendarId();
    }

}
