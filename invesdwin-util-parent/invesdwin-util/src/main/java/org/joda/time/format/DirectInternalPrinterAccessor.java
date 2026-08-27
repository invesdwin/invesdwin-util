package org.joda.time.format;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;

@Immutable
final class DirectInternalPrinterAccessor implements IInternalPrinterAccessor {

    private final InternalPrinter printer;

    DirectInternalPrinterAccessor(final DateTimeFormatter formatter) {
        this(formatter.getPrinter0());
    }

    DirectInternalPrinterAccessor(final InternalPrinter printer) {
        this.printer = printer;
    }

    @Override
    public int estimatePrintedLength() {
        return printer.estimatePrintedLength();
    }

    @Override
    public void printTo(final Appendable appendable, final long instant, final Chronology chrono,
            final int displayOffset, final DateTimeZone displayZone, final Locale locale) throws IOException {
        printer.printTo(appendable, instant, chrono, displayOffset, displayZone, locale);
    }

    @Override
    public void printTo(final Appendable appendable, final ReadablePartial partial, final Locale locale)
            throws IOException {
        printer.printTo(appendable, partial, locale);
    }

}
