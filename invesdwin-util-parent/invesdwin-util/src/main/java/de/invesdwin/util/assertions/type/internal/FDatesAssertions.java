// CHECKSTYLE:OFF

package de.invesdwin.util.assertions.type.internal;

import static org.assertj.core.error.ShouldBeAfter.shouldBeAfter;
import static org.assertj.core.error.ShouldBeAfterOrEqualTo.shouldBeAfterOrEqualTo;
import static org.assertj.core.error.ShouldBeAfterYear.shouldBeAfterYear;
import static org.assertj.core.error.ShouldBeBefore.shouldBeBefore;
import static org.assertj.core.error.ShouldBeBeforeOrEqualTo.shouldBeBeforeOrEqualTo;
import static org.assertj.core.error.ShouldBeBeforeYear.shouldBeBeforeYear;
import static org.assertj.core.error.ShouldBeBetween.shouldBeBetween;
import static org.assertj.core.error.ShouldBeCloseTo.shouldBeCloseTo;
import static org.assertj.core.error.ShouldBeInSameDay.shouldBeInSameDay;
import static org.assertj.core.error.ShouldBeInSameHour.shouldBeInSameHour;
import static org.assertj.core.error.ShouldBeInSameHourWindow.shouldBeInSameHourWindow;
import static org.assertj.core.error.ShouldBeInSameMinute.shouldBeInSameMinute;
import static org.assertj.core.error.ShouldBeInSameMinuteWindow.shouldBeInSameMinuteWindow;
import static org.assertj.core.error.ShouldBeInSameMonth.shouldBeInSameMonth;
import static org.assertj.core.error.ShouldBeInSameSecond.shouldBeInSameSecond;
import static org.assertj.core.error.ShouldBeInSameSecondWindow.shouldBeInSameSecondWindow;
import static org.assertj.core.error.ShouldBeInSameYear.shouldBeInSameYear;
import static org.assertj.core.error.ShouldBeInTheFuture.shouldBeInTheFuture;
import static org.assertj.core.error.ShouldBeInThePast.shouldBeInThePast;
import static org.assertj.core.error.ShouldBeToday.shouldBeToday;
import static org.assertj.core.error.ShouldHaveTime.shouldHaveTime;
import static org.assertj.core.error.ShouldNotBeBetween.shouldNotBeBetween;

import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.Immutable;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.error.ShouldBeEqualWithTimePrecision;
import org.assertj.core.internal.ComparatorBasedComparisonStrategy;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.internal.Dates;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Objects;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.util.DateUtil;
import org.assertj.core.util.VisibleForTesting;

import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

/**
 * Reusable assertions for <code>{@link FDate}</code>s.
 * 
 * @author Joel Costigliola
 * @author William Delanoue
 */
@Immutable
public class FDatesAssertions extends Dates {

    private static final FDatesAssertions INSTANCE = new FDatesAssertions();

    @VisibleForTesting
    private final Failures failures = Failures.instance();

    private final ComparisonStrategy comparisonStrategy;

    @VisibleForTesting
    FDatesAssertions() {
        this(StandardComparisonStrategy.instance());
    }

    public FDatesAssertions(final ComparisonStrategy comparisonStrategy) {
        super(comparisonStrategy);
        this.comparisonStrategy = comparisonStrategy;
    }

    /**
     * Returns the singleton instance of this class.
     * 
     * @return the singleton instance of this class.
     */
    public static FDatesAssertions instance() {
        return INSTANCE;
    }

    @Override
    @VisibleForTesting
    public Comparator<?> getComparator() {
        if (comparisonStrategy instanceof ComparatorBasedComparisonStrategy) {
            return ((ComparatorBasedComparisonStrategy) comparisonStrategy).getComparator();
        }
        return null;
    }

    /**
     * Verifies that the actual {@code FDate} is strictly before the given one.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the other date to compare actual with.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly before the given one.
     */
    public void assertIsBefore(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (isBefore(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeBefore(FDates.toDate(actual), FDates.toDate(other), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is before or equal to the given one.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the other date to compare actual with.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not before or equal to the given one.
     */
    public void assertIsBeforeOrEqualTo(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (isBeforeOrEqualTo(actual, other)) {
            return;
        }
        throw failures.failure(info,
                shouldBeBeforeOrEqualTo(FDates.toDate(actual), FDates.toDate(other), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is strictly after the given one.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given FDate.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not strictly after the given one.
     */
    public void assertIsAfter(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (isAfter(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeAfter(FDates.toDate(actual), FDates.toDate(other), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is after or equal to the given one.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given FDate.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not after or equal to the given one.
     */
    public void assertIsAfterOrEqualTo(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (isAfterOrEqualTo(actual, other)) {
            return;
        }
        throw failures.failure(info,
                shouldBeAfterOrEqualTo(FDates.toDate(actual), FDates.toDate(other), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is equal to the given one with precision.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given FDate.
     * @param precision
     *            maximum precision for the comparison.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not equal to the given one.
     */
    public void assertIsEqualWithPrecision(final AssertionInfo info, final FDate actual, final FDate other,
            final TimeUnit precision) {
        assertNotNull(info, actual);
        final Calendar calendarActual = new FDate().calendarValue();
        calendarActual.setTime(actual.dateValue());
        final Calendar calendarOther = new FDate().calendarValue();
        calendarOther.setTime(other.dateValue());
        switch (precision) {
        case DAYS:
            calendarActual.set(Calendar.DAY_OF_WEEK, 0);
            calendarOther.set(Calendar.DAY_OF_WEEK, 0);
            // fall through
        case HOURS:
            calendarActual.set(Calendar.HOUR_OF_DAY, 0);
            calendarOther.set(Calendar.HOUR_OF_DAY, 0);
            // fall through
        case MINUTES:
            calendarActual.set(Calendar.MINUTE, 0);
            calendarOther.set(Calendar.MINUTE, 0);
            // fall through
        case SECONDS:
            calendarActual.set(Calendar.SECOND, 0);
            calendarOther.set(Calendar.SECOND, 0);
            // fall through
        case MILLISECONDS:
            calendarActual.set(Calendar.MILLISECOND, 0);
            calendarOther.set(Calendar.MILLISECOND, 0);
            // fall through
        case MICROSECONDS:
            break;
        default:
            break;
        }
        if (calendarActual.compareTo(calendarOther) != 0) {
            throw failures.failure(info, ShouldBeEqualWithTimePrecision.shouldBeEqual(FDates.toDate(actual),
                    FDates.toDate(other), precision));
        }
    }

    /**
     * Verifies that the actual {@code FDate} is in <i>start:end</i> period.<br>
     * start date belongs to the period if inclusiveStart is true.<br>
     * end date belongs to the period if inclusiveEnd is true.<br>
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param start
     *            the period start, expected not to be null.
     * @param end
     *            the period end, expected not to be null.
     * @param inclusiveStart
     *            whether to include start date in period.
     * @param inclusiveEnd
     *            whether to include end date in period.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in <i>start:end</i> period.
     */
    public void assertIsBetween(final AssertionInfo info, final FDate actual, final FDate start, final FDate end,
            final boolean inclusiveStart, final boolean inclusiveEnd) {
        if (actualIsBetweenGivenPeriod(info, actual, start, end, inclusiveStart, inclusiveEnd)) {
            return;
        }
        throw failures.failure(info,
                shouldBeBetween(actual, start, end, inclusiveStart, inclusiveEnd, comparisonStrategy));
    }

    private boolean actualIsBetweenGivenPeriod(final AssertionInfo info, final FDate actual, final FDate start,
            final FDate end, final boolean inclusiveStart, final boolean inclusiveEnd) {
        assertNotNull(info, actual);
        startFDateParameterIsNotNull(start);
        endFDateParameterIsNotNull(end);
        final boolean checkLowerBoundaryPeriod = inclusiveStart ? isAfterOrEqualTo(actual, start)
                : isAfter(actual, start);
        final boolean checkUpperBoundaryPeriod = inclusiveEnd ? isBeforeOrEqualTo(actual, end) : isBefore(actual, end);
        final boolean isBetweenGivenPeriod = checkLowerBoundaryPeriod && checkUpperBoundaryPeriod;
        return isBetweenGivenPeriod;
    }

    /**
     * Verifies that the actual {@code FDate} is not in <i>start:end</i> period..<br>
     * start date belongs to the period if inclusiveStart is true.<br>
     * end date belongs to the period if inclusiveEnd is true.<br>
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param start
     *            the period start, expected not to be null.
     * @param end
     *            the period end, expected not to be null.
     * @param inclusiveStart
     *            wether to include start date in period.
     * @param inclusiveEnd
     *            wether to include end date in period.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if start {@code FDate} is {@code null}.
     * @throws NullPointerException
     *             if end {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is in <i>start:end</i> period.
     */
    public void assertIsNotBetween(final AssertionInfo info, final FDate actual, final FDate start, final FDate end,
            final boolean inclusiveStart, final boolean inclusiveEnd) {
        if (!actualIsBetweenGivenPeriod(info, actual, start, end, inclusiveStart, inclusiveEnd)) {
            return;
        }
        throw failures.failure(info, shouldNotBeBetween(FDates.toDate(actual), FDates.toDate(start), FDates.toDate(end),
                inclusiveStart, inclusiveEnd, comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is strictly in the past.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in the past.
     */
    public void assertIsInThePast(final AssertionInfo info, final FDate actual) {
        assertNotNull(info, actual);
        if (isBefore(actual, FDate.valueOf(DateUtil.now()))) {
            return;
        }
        throw failures.failure(info, shouldBeInThePast(FDates.toDate(actual), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is today, by comparing only year, month and day of actual to today (ie. we
     * don't check hours).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not today.
     */
    public void assertIsToday(final AssertionInfo info, final FDate actual) {
        assertNotNull(info, actual);
        final FDate todayWithoutTime = FDate.valueOf(DateUtil.truncateTime(DateUtil.now()));
        final FDate actualWithoutTime = FDate.valueOf(DateUtil.truncateTime(FDates.toDate(actual)));
        if (areEqual(actualWithoutTime, todayWithoutTime)) {
            return;
        }
        throw failures.failure(info, shouldBeToday(FDates.toDate(actual), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is strictly in the future.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} is not in the future.
     */
    public void assertIsInTheFuture(final AssertionInfo info, final FDate actual) {
        assertNotNull(info, actual);
        if (isAfter(actual, FDate.valueOf(DateUtil.now()))) {
            return;
        }
        throw failures.failure(info, shouldBeInTheFuture(FDates.toDate(actual), comparisonStrategy));
    }

    /**
     * Verifies that the actual {@code FDate} is strictly before the given year.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param year
     *            the year to compare actual year to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is after or equal to the given year.
     */
    public void assertIsBeforeYear(final AssertionInfo info, final FDate actual, final int year) {
        assertNotNull(info, actual);
        if (DateUtil.yearOf(FDates.toDate(actual)) < year) {
            return;
        }
        throw failures.failure(info, shouldBeBeforeYear(FDates.toDate(actual), year));
    }

    /**
     * Verifies that the actual {@code FDate} is strictly after the given year.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param year
     *            the year to compare actual year to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is before or equal to the given year.
     */
    public void assertIsAfterYear(final AssertionInfo info, final FDate actual, final int year) {
        assertNotNull(info, actual);
        if (DateUtil.yearOf(FDates.toDate(actual)) > year) {
            return;
        }
        throw failures.failure(info, shouldBeAfterYear(FDates.toDate(actual), year));
    }

    /**
     * Verifies that the actual {@code FDate} year is equal to the given year.
     * 
     * @param year
     *            the year to compare actual year to
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} year is not equal to the given year.
     */
    public void assertIsWithinYear(final AssertionInfo info, final FDate actual, final int year) {
        assertNotNull(info, actual);
        if (DateUtil.yearOf(FDates.toDate(actual)) == year) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "year", year));
    }

    /**
     * Verifies that the actual {@code FDate} month is equal to the given month, <b>month value starting at 1</b>
     * (January=1, February=2, ...).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param month
     *            the month to compare actual month to, see {@link Calendar#MONTH} for valid values
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} month is not equal to the given month.
     */
    public void assertIsWithinMonth(final AssertionInfo info, final FDate actual, final int month) {
        assertNotNull(info, actual);
        if (DateUtil.monthOf(FDates.toDate(actual)) == month) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "month", month));
    }

    /**
     * Verifies that the actual {@code FDate} day of month is equal to the given day of month.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param dayOfMonth
     *            the day of month to compare actual day of month to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} month is not equal to the given day of month.
     */
    public void assertIsWithinDayOfMonth(final AssertionInfo info, final FDate actual, final int dayOfMonth) {
        assertNotNull(info, actual);
        if (DateUtil.dayOfMonthOf(FDates.toDate(actual)) == dayOfMonth) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "day of month", dayOfMonth));
    }

    /**
     * Verifies that the actual {@code FDate} day of week is equal to the given day of week.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param dayOfWeek
     *            the day of week to compare actual day of week to, see {@link Calendar#DAY_OF_WEEK} for valid values
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} week is not equal to the given day of week.
     */
    public void assertIsWithinDayOfWeek(final AssertionInfo info, final FDate actual, final int dayOfWeek) {
        assertNotNull(info, actual);
        if (DateUtil.dayOfWeekOf(FDates.toDate(actual)) == dayOfWeek) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "day of week", dayOfWeek));
    }

    /**
     * Verifies that the actual {@code FDate} hour od day is equal to the given hour of day (24-hour clock).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param hourOfDay
     *            the hour of day to compare actual hour of day to (24-hour clock)
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} hour is not equal to the given hour.
     */
    public void assertIsWithinHourOfDay(final AssertionInfo info, final FDate actual, final int hourOfDay) {
        assertNotNull(info, actual);
        if (DateUtil.hourOfDayOf(FDates.toDate(actual)) == hourOfDay) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "hour", hourOfDay));
    }

    /**
     * Verifies that the actual {@code FDate} minute is equal to the given minute.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param minute
     *            the minute to compare actual minute to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} minute is not equal to the given minute.
     */
    public void assertIsWithinMinute(final AssertionInfo info, final FDate actual, final int minute) {
        assertNotNull(info, actual);
        if (DateUtil.minuteOf(FDates.toDate(actual)) == minute) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "minute", minute));
    }

    /**
     * Verifies that the actual {@code FDate} second is equal to the given second.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param second
     *            the second to compare actual second to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} second is not equal to the given second.
     */
    public void assertIsWithinSecond(final AssertionInfo info, final FDate actual, final int second) {
        assertNotNull(info, actual);
        if (DateUtil.secondOf(FDates.toDate(actual)) == second) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "second", second));
    }

    /**
     * Verifies that the actual {@code FDate} millisecond is equal to the given millisecond.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param millisecond
     *            the millisecond to compare actual millisecond to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} millisecond is not equal to the given millisecond.
     */
    public void assertIsWithinMillisecond(final AssertionInfo info, final FDate actual, final int millisecond) {
        assertNotNull(info, actual);
        if (DateUtil.millisecondOf(FDates.toDate(actual)) == millisecond) {
            return;
        }
        throw failures.failure(info, ShouldBeWithin.shouldBeWithin(FDates.toDate(actual), "millisecond", millisecond));
    }

    /**
     * Verifies that actual and given {@code FDate} are in the same year.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not in the same year.
     */
    public void assertIsInSameYearAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameYear(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameYear(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, false otherwise
     */
    private static boolean areInSameYear(final FDate actual, final FDate other) {
        return DateUtil.yearOf(FDates.toDate(actual)) == DateUtil.yearOf(FDates.toDate(other));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same month (and thus in the same year).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same month.
     */
    public void assertIsInSameMonthAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameMonth(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameMonth(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year and month, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year and month, false otherwise
     */
    private static boolean areInSameMonth(final FDate actual, final FDate other) {
        return areInSameYear(actual, other)
                && DateUtil.monthOf(FDates.toDate(actual)) == DateUtil.monthOf(FDates.toDate(other));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same day of month (and thus in the same
     * month and year).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same day of month.
     */
    public void assertIsInSameDayAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameDayOfMonth(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameDay(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year, month and day of month, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month and day of month, false otherwise
     */
    private static boolean areInSameDayOfMonth(final FDate actual, final FDate other) {
        return areInSameMonth(actual, other)
                && DateUtil.dayOfMonthOf(FDates.toDate(actual)) == DateUtil.dayOfMonthOf(FDates.toDate(other));
    }

    /**
     * Verifies that actual and given {@code FDate} are in the same hour (and thus in the same day of month, month and
     * year).
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same hour.
     */
    public void assertIsInSameHourAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameHour(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameHour(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same hour, day of month, month and year.
     *
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same hour.
     */
    public void assertIsInSameHourWindowAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameHourWindow(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameHourWindow(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     */
    private static boolean areInSameHourWindow(final FDate actual, final FDate other) {
        return DateUtil.timeDifference(FDates.toDate(actual), FDates.toDate(other)) < TimeUnit.HOURS.toMillis(1);
    }

    /**
     * Returns true if both date are in the same year, month, day of month and hour, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month, day of month and hour, false otherwise.
     */
    private static boolean areInSameHour(final FDate actual, final FDate other) {
        return areInSameDayOfMonth(actual, other)
                && DateUtil.hourOfDayOf(FDates.toDate(actual)) == DateUtil.hourOfDayOf(FDates.toDate(other));
    }

    /**
     * Verifies that actual and given {@code FDate} are in the same minute, hour, day of month, month and year.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same minute.
     */
    public void assertIsInSameMinuteAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameMinute(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameMinute(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same minute.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same minute.
     */
    public void assertIsInSameMinuteWindowAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameMinuteWindow(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameMinuteWindow(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year, month, day of month, hour and minute, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month, day of month, hour and minute, false otherwise.
     */
    private static boolean areInSameMinute(final FDate actual, final FDate other) {
        return areInSameHour(actual, other)
                && DateUtil.minuteOf(FDates.toDate(actual)) == DateUtil.minuteOf(FDates.toDate(other));
    }

    private static boolean areInSameMinuteWindow(final FDate actual, final FDate other) {
        return DateUtil.timeDifference(FDates.toDate(actual), FDates.toDate(other)) < TimeUnit.MINUTES.toMillis(1);
    }

    /**
     * Verifies that actual and given {@code FDate} are in the same second, minute, hour, day of month, month and year.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same second.
     */
    public void assertIsInSameSecondAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameSecond(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameSecond(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Verifies that actual and given {@code FDate} are chronologically in the same second.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if actual and given {@code FDate} are not chronologically speaking in the same second.
     */
    public void assertIsInSameSecondWindowAs(final AssertionInfo info, final FDate actual, final FDate other) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        if (areInSameSecondWindow(actual, other)) {
            return;
        }
        throw failures.failure(info, shouldBeInSameSecondWindow(FDates.toDate(actual), FDates.toDate(other)));
    }

    /**
     * Returns true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     */
    private static boolean areInSameSecondWindow(final FDate actual, final FDate other) {
        return DateUtil.timeDifference(FDates.toDate(actual), FDates.toDate(other)) < TimeUnit.SECONDS.toMillis(1);
    }

    /**
     * Returns true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     * 
     * @param actual
     *            the actual date. expected not be null
     * @param other
     *            the other date. expected not be null
     * @return true if both date are in the same year, month and day of month, hour, minute and second, false otherwise.
     */
    private static boolean areInSameSecond(final FDate actual, final FDate other) {
        return areInSameMinute(actual, other)
                && DateUtil.secondOf(FDates.toDate(actual)) == DateUtil.secondOf(FDates.toDate(other));
    }

    /**
     * Verifies that the actual {@code FDate} is close to the other date by less than delta, if difference is equals to
     * delta it is ok.<br>
     * Note that delta expressed in milliseconds.<br>
     * Use handy TimeUnit to convert a duration in milliseconds, for example you can express a delta of 5 seconds with
     * <code>TimeUnit.SECONDS.toMillis(5)</code>.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param other
     *            the given {@code FDate} to compare actual {@code FDate} to.
     * @param deltaInMilliseconds
     *            the delta used for date comparison, expressed in milliseconds
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws NullPointerException
     *             if other {@code FDate} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} week is not close to the given date by less than delta.
     */
    public void assertIsCloseTo(final AssertionInfo info, final FDate actual, final FDate other,
            final long deltaInMilliseconds) {
        assertNotNull(info, actual);
        dateParameterIsNotNull(other);
        final long difference = Longs.abs(actual.millisValue() - other.millisValue());
        if (difference <= deltaInMilliseconds) {
            return;
        }
        throw failures.failure(info,
                shouldBeCloseTo(FDates.toDate(actual), FDates.toDate(other), deltaInMilliseconds, difference));
    }

    /**
     * Verifies that the actual {@code FDate} time is equal to the given timestamp.
     * 
     * @param info
     *            contains information about the assertion.
     * @param actual
     *            the "actual" {@code FDate}.
     * @param timestamp
     *            the timestamp to compare actual time to
     * @throws AssertionError
     *             if {@code actual} is {@code null}.
     * @throws AssertionError
     *             if the actual {@code FDate} time is not equal to the given timestamp.
     */
    public void assertHasTime(final AssertionInfo info, final FDate actual, final long timestamp) {
        assertNotNull(info, actual);
        if (actual.millisValue() == timestamp) {
            return;
        }
        throw failures.failure(info, shouldHaveTime(FDates.toDate(actual), timestamp));
    }

    /**
     * used to check that the date to compare actual date to is not null, in that case throws a
     * {@link NullPointerException} with an explicit message
     * 
     * @param date
     *            the date to check
     * @throws NullPointerException
     *             with an explicit message if the given date is null
     */
    private static void dateParameterIsNotNull(final FDate date) {
        if (date == null) {
            throw new NullPointerException("The date to compare actual with should not be null");
        }
    }

    /**
     * used to check that the start of period date to compare actual date to is not null, in that case throws a
     * {@link NullPointerException} with an explicit message
     * 
     * @param start
     *            the start date to check
     * @throws NullPointerException
     *             with an explicit message if the given start date is null
     */
    private static void startFDateParameterIsNotNull(final FDate start) {
        if (start == null) {
            throw new NullPointerException("The start date of period to compare actual with should not be null");
        }
    }

    /**
     * used to check that the end of period date to compare actual date to is not null, in that case throws a
     * {@link NullPointerException} with an explicit message
     * 
     * @param end
     *            the end date to check
     * @throws NullPointerException
     *             with an explicit message if the given end date is null
     */
    private static void endFDateParameterIsNotNull(final FDate end) {
        if (end == null) {
            throw new NullPointerException("The end date of period to compare actual with should not be null");
        }
    }

    private void assertNotNull(final AssertionInfo info, final FDate actual) {
        Objects.instance().assertNotNull(info, actual);
    }

    /**
     * Returns <code>true</code> if the actual {@code FDate} is before or equal to the given one according to underlying
     * {@link #comparisonStrategy}, false otherwise.
     * 
     * @param actual
     *            the actual date - must not be null.
     * @param other
     *            the given FDate.
     * @return <code>true</code> if the actual {@code FDate} is before or equal to the given one according to underlying
     *         {@link #comparisonStrategy}, false otherwise.
     * @throws NullPointerException
     *             if {@code actual} is {@code null}.
     */
    private boolean isBeforeOrEqualTo(final FDate actual, final FDate other) {
        return comparisonStrategy.isLessThanOrEqualTo(actual, other);
    }

    /**
     * Returns true if the actual {@code FDate} is equal to the given one according to underlying
     * {@link #comparisonStrategy}, false otherwise.
     * 
     * @param actual
     *            the actual date - must not be null.
     * @param other
     *            the given FDate.
     * @return <code>true</code> if the actual {@code FDate} is equal to the given one according to underlying
     *         {@link #comparisonStrategy}, false otherwise.
     */
    private boolean areEqual(final FDate actual, final FDate other) {
        return comparisonStrategy.areEqual(other, actual);
    }

    /**
     * Returns <code>true</code> if the actual {@code FDate} is after or equal to the given one according to underlying
     * {@link #comparisonStrategy}, false otherwise.
     * 
     * @param actual
     *            the actual date - must not be null.
     * @param other
     *            the given FDate.
     * @return <code>true</code> if the actual {@code FDate} is after or equal to the given one according to underlying
     *         {@link #comparisonStrategy}, false otherwise.
     * @throws NullPointerException
     *             if {@code actual} is {@code null}.
     */
    private boolean isAfterOrEqualTo(final FDate actual, final FDate other) {
        return comparisonStrategy.isGreaterThanOrEqualTo(actual, other);
    }

    /**
     * Returns true if actual is before other according to underlying {@link #comparisonStrategy}, false otherwise.
     * 
     * @param actual
     *            the {@link FDate} to compare to other
     * @param other
     *            the {@link FDate} to compare to actual
     * @return true if actual is before other according to underlying {@link #comparisonStrategy}, false otherwise.
     */
    private boolean isBefore(final FDate actual, final FDate other) {
        return comparisonStrategy.isLessThan(actual, other);
    }

    /**
     * Returns true if actual is after other according to underlying {@link #comparisonStrategy}, false otherwise.
     * 
     * @param actual
     *            the {@link FDate} to compare to other
     * @param other
     *            the {@link FDate} to compare to actual
     * @return true if actual is after other according to underlying {@link #comparisonStrategy}, false otherwise.
     */
    private boolean isAfter(final FDate actual, final FDate other) {
        return comparisonStrategy.isGreaterThan(actual, other);
    }

}
