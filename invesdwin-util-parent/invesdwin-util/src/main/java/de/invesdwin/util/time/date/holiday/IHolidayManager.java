package de.invesdwin.util.time.date.holiday;

import java.util.Optional;

import de.invesdwin.util.time.date.FDate;

public interface IHolidayManager {

    default boolean isHoliday(final long millis) {
        return isHoliday(new FDate(millis, 0));
    }

    boolean isHoliday(FDate date);

    String getHolidayCalendarId();

    /**
     * Session-open arm of managers whose calendar carries intraday session data: the most recent session open at
     * or before the probe instant (inclusive — a probe exactly on an open answers that open). Day-level calendars
     * (jollyday, strata) hold no begin times, so the interface default reports empty; a session-anchored concrete
     * calendar overrides it. Callers must treat an empty answer as “this calendar cannot anchor a session grid”
     * and never fabricate an open time from the holiday arm.
     *
     * @param date
     *            probe instant
     * @return the latest session open at or before the probe, or {@link Optional#empty()} when this calendar has
     *         no session-open data (or no open inside the manager's bounded search window)
     */
    default Optional<FDate> getOrPreviousSessionOpen(final FDate date) {
        return Optional.empty();
    }

}
