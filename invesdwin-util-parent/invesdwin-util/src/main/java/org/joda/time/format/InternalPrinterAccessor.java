package org.joda.time.format;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public class InternalPrinterAccessor {

    private static final MethodHandle MH_DATETIMEFORMATTER_IPRINTER;

    static {
        final Field iPrinterField = Reflections.findField(DateTimeFormatter.class, "iPrinter");
        Reflections.makeAccessible(iPrinterField);
        final Lookup lookup = MethodHandles.lookup();
        try {
            MH_DATETIMEFORMATTER_IPRINTER = lookup.unreflectGetter(iPrinterField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final InternalPrinter printer;

    public InternalPrinterAccessor(final DateTimeFormatter formatter) {
        try {
            this.printer = (InternalPrinter) MH_DATETIMEFORMATTER_IPRINTER.invoke(formatter);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
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
