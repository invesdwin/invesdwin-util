package org.joda.time.format;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;

@Immutable
public class InternalPrinterAccessor {

    private final InternalPrinter printer;

    public InternalPrinterAccessor(final DateTimeFormatter formatter) {
        this.printer = formatter.getPrinter0();
    }

    public InternalPrinterAccessor(final InternalPrinter printer) {
        this.printer = printer;
    }

    public int estimatePrintedLength() {
        return printer.estimatePrintedLength();
    }

    public void printTo(final Appendable appendable, final long instant, final Chronology chrono,
            final int displayOffset, final DateTimeZone displayZone, final Locale locale) throws IOException {
        printer.printTo(appendable, instant, chrono, displayOffset, displayZone, locale);
    }

    public void printTo(final Appendable appendable, final ReadablePartial partial, final Locale locale)
            throws IOException {
        printer.printTo(appendable, partial, locale);
    }

}
