package de.invesdwin.util.marshallers.jaxb;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.concurrent.Immutable;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DatatypeConverter {

    public static final DatatypeFactory FACTORY;

    static {
        try {
            FACTORY = DatatypeFactory.newInstance();
        } catch (final DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private DatatypeConverter() {}

    private static FDate toFDate(final String lex) {
        return FDate.valueOf(FACTORY.newXMLGregorianCalendar(lex).toGregorianCalendar());
    }

    private static String toXML(final FDate date, final GTypes type) {
        final Calendar cal = toCalendar(date);
        return type.stripToValid(FACTORY.newXMLGregorianCalendar((GregorianCalendar) cal)).toXMLFormat();
    }

    private static Calendar toCalendar(final FDate date) {
        final Calendar cal;
        if (date == null) {
            cal = null;
        } else {
            cal = date.calendarValue();
        }
        return cal;
    }

    public static String printGDay(final FDate cal) {
        return toXML(cal, GTypes.GDay);
    }

    public static FDate parseGDay(final String lex) {
        return toFDate(lex);
    }

    public static String printGMonth(final FDate cal) {
        return toXML(cal, GTypes.GMonth);
    }

    public static FDate parseGMonth(final String lex) {
        return toFDate(lex);
    }

    public static String printGMonthDay(final FDate cal) {
        return toXML(cal, GTypes.GMonthDay);
    }

    public static FDate parseGMonthDay(final String lex) {
        return toFDate(lex);
    }

    public static String printGYear(final FDate cal) {
        return toXML(cal, GTypes.GYear);
    }

    public static FDate parseGYear(final String lex) {
        return toFDate(lex);
    }

    public static String printGYearMonth(final FDate cal) {
        return toXML(cal, GTypes.GYearMonth);
    }

    public static FDate parseGYearMonth(final String lex) {
        return toFDate(lex);
    }

    public static String printDecimal(final Decimal decimal) {
        return decimal.toString();
    }

    public static Decimal parseDecimal(final String lex) {
        return new Decimal(lex);
    }

    public static FDate parseTime(final String lex) {
        final Calendar cal = javax.xml.bind.DatatypeConverter.parseTime(lex);
        return FDate.valueOf(cal);
    }

    public static String printTime(final FDate date) {
        if (date == null) {
            return null;
        }
        final Calendar cal = toCalendar(date);
        return javax.xml.bind.DatatypeConverter.printTime(cal);
    }

    public static FDate parseDate(final String lex) {
        final Calendar cal = javax.xml.bind.DatatypeConverter.parseDate(lex);
        return FDate.valueOf(cal);
    }

    public static String printDate(final FDate date) {
        if (date == null) {
            return null;
        }
        final Calendar cal = toCalendar(date);
        return javax.xml.bind.DatatypeConverter.printDate(cal);
    }

    public static FDate parseDateTime(final String lex) {
        final Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(lex);
        return FDate.valueOf(cal);
    }

    public static String printDateTime(final FDate date) {
        if (date == null) {
            return null;
        }
        final Calendar cal = toCalendar(date);
        return javax.xml.bind.DatatypeConverter.printDateTime(cal);
    }

}
