package de.invesdwin.util.internal.jaxb;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.datatype.XMLGregorianCalendar;

@ThreadSafe
public enum GTypes {

    GDay {
        @Override
        public XMLGregorianCalendar stripToValid(final XMLGregorianCalendar xmlCal) {
            final XMLGregorianCalendar stripped = DatatypeConverter.FACTORY.newXMLGregorianCalendar();
            stripped.setDay(xmlCal.getDay());
            return stripped;
        }
    },
    GMonth {
        @Override
        public XMLGregorianCalendar stripToValid(final XMLGregorianCalendar xmlCal) {
            final XMLGregorianCalendar stripped = DatatypeConverter.FACTORY.newXMLGregorianCalendar();
            stripped.setMonth(xmlCal.getMonth());
            return stripped;
        }
    },
    GMonthDay {
        @Override
        public XMLGregorianCalendar stripToValid(final XMLGregorianCalendar xmlCal) {
            final XMLGregorianCalendar stripped = GMonth.stripToValid(xmlCal);
            stripped.setDay(xmlCal.getDay());
            return stripped;
        }
    },
    GYear {
        @Override
        public XMLGregorianCalendar stripToValid(final XMLGregorianCalendar xmlCal) {
            final XMLGregorianCalendar stripped = DatatypeConverter.FACTORY.newXMLGregorianCalendar();
            stripped.setYear(xmlCal.getYear());
            return stripped;
        }
    },
    GYearMonth {
        @Override
        public XMLGregorianCalendar stripToValid(final XMLGregorianCalendar xmlCal) {
            final XMLGregorianCalendar stripped = GYear.stripToValid(xmlCal);
            stripped.setMonth(xmlCal.getMonth());
            return stripped;
        }
    };

    public abstract XMLGregorianCalendar stripToValid(XMLGregorianCalendar xmlCal);

}
