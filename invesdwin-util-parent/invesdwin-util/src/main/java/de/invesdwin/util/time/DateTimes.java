package de.invesdwin.util.time;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import de.invesdwin.util.time.date.FDate;

@Immutable
public final class DateTimes {

    private DateTimes() {}

    public static DateTime fromDate(final Date date) {
        if (date == null) {
            return null;
        } else {
            return new FDate(date).jodaTimeValue().toDateTime();
        }
    }

    public static Date toDate(final DateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.toDate();
        }
    }

}
