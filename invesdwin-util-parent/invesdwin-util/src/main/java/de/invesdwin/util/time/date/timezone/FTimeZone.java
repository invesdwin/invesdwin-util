package de.invesdwin.util.time.date.timezone;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationField;
import org.joda.time.chrono.ISOChronology;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDateField;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public class FTimeZone implements IFTimeZoneProvider {

    public static final FTimeZone UTC = new FTimeZone(TimeZones.UTC);
    public static final FTimeZone EUROPE_BERLIN = new FTimeZone(TimeZones.EUROPE_BERLIN);
    public static final FTimeZone EET = new FTimeZone(TimeZones.EET);
    public static final FTimeZone CET = new FTimeZone(TimeZones.CET);
    public static final FTimeZone AMERICA_NEWYORK = new FTimeZone(TimeZones.AMERICA_NEWYORK);

    private final Calendar templateCalendar;
    private final TimeZone timeZone;
    private final boolean isUTC;
    private final DateTimeZone dateTimeZone;
    private final ZoneId zoneId;
    private final Chronology chronology;

    private final DateTimeField dateTimeFieldYear;
    private final DateTimeField dateTimeFieldMonth;
    private final DateTimeField dateTimeFieldDay;
    private final DateTimeField dateTimeFieldWeekday;
    private final DateTimeField dateTimeFieldHour;
    private final DateTimeField dateTimeFieldMinute;
    private final DateTimeField dateTimeFieldSecond;
    private final DateTimeField dateTimeFieldMillisecond;
    private final DateTimeField dateTimeFieldWeekNumberOfYear;

    private final DurationField durationFieldYears;
    private final DurationField durationFieldMonths;
    private final DurationField durationFieldWeeks;
    private final DurationField durationFieldDays;
    private final DurationField durationFieldHours;
    private final DurationField durationFieldMinutes;
    private final DurationField durationFieldSeconds;
    private final DurationField durationFieldMilliseconds;

    public FTimeZone(final ZoneId zoneId) {
        this.zoneId = zoneId;
        //CHECKSTYLE:OFF
        this.timeZone = TimeZone.getTimeZone(zoneId);
        //CHECKSTYLE:ON
        this.isUTC = TimeZones.UTC.equals(timeZone);
        this.dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        //CHECKSTYLE:OFF
        final Calendar cal = Calendar.getInstance();
        //CHECKSTYLE:ON
        cal.clear();
        cal.setTimeZone(timeZone);
        this.templateCalendar = cal;
        this.chronology = ISOChronology.getInstance(dateTimeZone);
        this.dateTimeFieldYear = FDateField.Year.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMonth = FDateField.Month.jodaTimeValue().getField(chronology);
        this.dateTimeFieldDay = FDateField.Day.jodaTimeValue().getField(chronology);
        this.dateTimeFieldWeekday = FDateField.Weekday.jodaTimeValue().getField(chronology);
        this.dateTimeFieldHour = FDateField.Hour.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMinute = FDateField.Minute.jodaTimeValue().getField(chronology);
        this.dateTimeFieldSecond = FDateField.Second.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMillisecond = FDateField.Millisecond.jodaTimeValue().getField(chronology);
        this.dateTimeFieldWeekNumberOfYear = chronology.weekOfWeekyear();

        this.durationFieldYears = FTimeUnit.YEARS.jodaTimeValue().getField(chronology);
        this.durationFieldMonths = FTimeUnit.MONTHS.jodaTimeValue().getField(chronology);
        this.durationFieldWeeks = FTimeUnit.WEEKS.jodaTimeValue().getField(chronology);
        this.durationFieldDays = FTimeUnit.DAYS.jodaTimeValue().getField(chronology);
        this.durationFieldHours = FTimeUnit.HOURS.jodaTimeValue().getField(chronology);
        this.durationFieldMinutes = FTimeUnit.MINUTES.jodaTimeValue().getField(chronology);
        this.durationFieldSeconds = FTimeUnit.SECONDS.jodaTimeValue().getField(chronology);
        this.durationFieldMilliseconds = FTimeUnit.MILLISECONDS.jodaTimeValue().getField(chronology);
    }

    public FTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
        this.isUTC = TimeZones.UTC.equals(timeZone);
        this.dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        this.zoneId = timeZone.toZoneId();
        //CHECKSTYLE:OFF
        final Calendar cal = Calendar.getInstance();
        //CHECKSTYLE:ON
        cal.clear();
        cal.setTimeZone(timeZone);
        this.templateCalendar = cal;
        this.chronology = ISOChronology.getInstance(dateTimeZone);
        this.dateTimeFieldYear = FDateField.Year.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMonth = FDateField.Month.jodaTimeValue().getField(chronology);
        this.dateTimeFieldDay = FDateField.Day.jodaTimeValue().getField(chronology);
        this.dateTimeFieldWeekday = FDateField.Weekday.jodaTimeValue().getField(chronology);
        this.dateTimeFieldHour = FDateField.Hour.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMinute = FDateField.Minute.jodaTimeValue().getField(chronology);
        this.dateTimeFieldSecond = FDateField.Second.jodaTimeValue().getField(chronology);
        this.dateTimeFieldMillisecond = FDateField.Millisecond.jodaTimeValue().getField(chronology);
        this.dateTimeFieldWeekNumberOfYear = chronology.weekOfWeekyear();

        this.durationFieldYears = FTimeUnit.YEARS.jodaTimeValue().getField(chronology);
        this.durationFieldMonths = FTimeUnit.MONTHS.jodaTimeValue().getField(chronology);
        this.durationFieldWeeks = FTimeUnit.WEEKS.jodaTimeValue().getField(chronology);
        this.durationFieldDays = FTimeUnit.DAYS.jodaTimeValue().getField(chronology);
        this.durationFieldHours = FTimeUnit.HOURS.jodaTimeValue().getField(chronology);
        this.durationFieldMinutes = FTimeUnit.MINUTES.jodaTimeValue().getField(chronology);
        this.durationFieldSeconds = FTimeUnit.SECONDS.jodaTimeValue().getField(chronology);
        this.durationFieldMilliseconds = FTimeUnit.MILLISECONDS.jodaTimeValue().getField(chronology);
    }

    public Calendar newCalendar() {
        return (Calendar) templateCalendar.clone();
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public boolean isUTC() {
        return isUTC;
    }

    public DateTimeZone getDateTimeZone() {
        return dateTimeZone;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public Chronology getChronology() {
        return chronology;
    }

    public DateTimeField getDateTimeFieldYear() {
        return dateTimeFieldYear;
    }

    public DateTimeField getDateTimeFieldMonth() {
        return dateTimeFieldMonth;
    }

    public DateTimeField getDateTimeFieldDay() {
        return dateTimeFieldDay;
    }

    public DateTimeField getDateTimeFieldWeekday() {
        return dateTimeFieldWeekday;
    }

    public DateTimeField getDateTimeFieldHour() {
        return dateTimeFieldHour;
    }

    public DateTimeField getDateTimeFieldMinute() {
        return dateTimeFieldMinute;
    }

    public DateTimeField getDateTimeFieldSecond() {
        return dateTimeFieldSecond;
    }

    public DateTimeField getDateTimeFieldMillisecond() {
        return dateTimeFieldMillisecond;
    }

    public DateTimeField getDateTimeFieldWeekNumberOfYear() {
        return dateTimeFieldWeekNumberOfYear;
    }

    public DurationField getDurationFieldYears() {
        return durationFieldYears;
    }

    public DurationField getDurationFieldMonths() {
        return durationFieldMonths;
    }

    public DurationField getDurationFieldWeeks() {
        return durationFieldWeeks;
    }

    public DurationField getDurationFieldDays() {
        return durationFieldDays;
    }

    public DurationField getDurationFieldHours() {
        return durationFieldHours;
    }

    public DurationField getDurationFieldMinutes() {
        return durationFieldMinutes;
    }

    public DurationField getDurationFieldSeconds() {
        return durationFieldSeconds;
    }

    public DurationField getDurationFieldMilliseconds() {
        return durationFieldMilliseconds;
    }

    public long getOffsetMilliseconds(final long millis) {
        return dateTimeZone.getOffset(millis);
    }

    public boolean isFixed() {
        return dateTimeZone.isFixed();
    }

    public boolean isDST(final long millis) {
        return !dateTimeZone.isStandardOffset(millis);
    }

    public String getId() {
        return zoneId.getId();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FTimeZone) {
            final FTimeZone cObj = (FTimeZone) obj;
            return Objects.equals(getId(), cObj.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(FTimeZone.class, getId());
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public FTimeZone asFTimeZone() {
        return this;
    }

    public static FTimeZone valueOf(final String id) {
        return new FTimeZone(TimeZones.getZoneId(id));
    }

}
