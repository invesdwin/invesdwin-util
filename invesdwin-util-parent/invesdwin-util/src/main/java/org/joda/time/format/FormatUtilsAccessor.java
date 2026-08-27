package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class FormatUtilsAccessor {

    private static boolean parserFallbackEnabled = false;
    private static boolean printerFallbackEnabled = false;

    private FormatUtilsAccessor() {}

    public static String createErrorMessage(final String text, final int errorPos) {
        //        return FormatUtils.createErrorMessage(text, errorPos);
        //just implement the whole function to avoid errors when java module system is enabled
        final int sampleLen = errorPos + 32;
        final String sampleText;
        if (text.length() <= sampleLen + 3) {
            sampleText = text;
        } else {
            sampleText = text.substring(0, sampleLen).concat("...");
        }

        if (errorPos <= 0) {
            return "Invalid format: \"" + sampleText + '"';
        }

        if (errorPos >= text.length()) {
            return "Invalid format: \"" + sampleText + "\" is too short";
        }

        return "Invalid format: \"" + sampleText + "\" is malformed at \"" + sampleText.substring(errorPos) + '"';
    }

    public static IInternalParserAccessor newParser(final DateTimeFormatter formatter) {
        if (parserFallbackEnabled) {
            return DateTimeParserInternalParserFallback.of(formatter.getParser());
        }
        try {
            return new DirectInternalParserAccessor(formatter);
        } catch (final Throwable t) {
            //must be a restricted environment, fallback to a slighly less efficient variant that has to use CharSequence.toString() to parseInto
            parserFallbackEnabled = true;
            return DateTimeParserInternalParserFallback.of(formatter.getParser());
        }
    }

    public static IInternalPrinterAccessor newPrinter(final DateTimeFormatter formatter) {
        if (printerFallbackEnabled) {
            return DateTimePrinterInternalPrinterFallback.of(formatter.getPrinter());
        }
        try {
            return new DirectInternalPrinterAccessor(formatter);
        } catch (final Throwable t) {
            //must be a restricted environment, fallback to a wrapper
            printerFallbackEnabled = true;
            return DateTimePrinterInternalPrinterFallback.of(formatter.getPrinter());
        }
    }

}
