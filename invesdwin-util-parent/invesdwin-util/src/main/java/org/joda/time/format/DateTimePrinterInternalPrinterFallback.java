package org.joda.time.format;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;

/**
 * Adapted from org.joda.time.format.DateTimePrinterInternalPrinter as a workaround when the java module system is
 * enabled in restricted environments.
 */
@Immutable
final class DateTimePrinterInternalPrinterFallback implements IInternalPrinterAccessor {

    private final DateTimePrinter underlying;

    private DateTimePrinterInternalPrinterFallback(final DateTimePrinter underlying) {
        this.underlying = underlying;
    }

    static IInternalPrinterAccessor of(final DateTimePrinter underlying) {
        if (underlying == null) {
            return null;
        }
        return new DateTimePrinterInternalPrinterFallback(underlying);
    }

    //-----------------------------------------------------------------------
    DateTimePrinter getUnderlying() {
        return underlying;
    }

    //-----------------------------------------------------------------------
    @Override
    public int estimatePrintedLength() {
        return underlying.estimatePrintedLength();
    }

    @Override
    public void printTo(final Appendable appendable, final long instant, final Chronology chrono,
            final int displayOffset, final DateTimeZone displayZone, final Locale locale) throws IOException {
        if (appendable instanceof StringBuffer) {
            final StringBuffer buf = (StringBuffer) appendable;
            underlying.printTo(buf, instant, chrono, displayOffset, displayZone, locale);
        } else if (appendable instanceof Writer) {
            final Writer out = (Writer) appendable;
            underlying.printTo(out, instant, chrono, displayOffset, displayZone, locale);
        } else {
            final StringBuffer buf = new StringBuffer(estimatePrintedLength());
            underlying.printTo(buf, instant, chrono, displayOffset, displayZone, locale);
            appendable.append(buf);
        }
    }

    @Override
    public void printTo(final Appendable appendable, final ReadablePartial partial, final Locale locale)
            throws IOException {
        if (appendable instanceof StringBuffer) {
            final StringBuffer buf = (StringBuffer) appendable;
            underlying.printTo(buf, partial, locale);
        } else if (appendable instanceof Writer) {
            final Writer out = (Writer) appendable;
            underlying.printTo(out, partial, locale);
        } else {
            final StringBuffer buf = new StringBuffer(estimatePrintedLength());
            underlying.printTo(buf, partial, locale);
            appendable.append(buf);
        }
    }

}
