package de.invesdwin.util.time.date.format;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.FormatUtilsAccessor;
import org.joda.time.format.InternalParserAccessor;
import org.joda.time.format.InternalPrinterAccessor;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDatePicos;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;

@ThreadSafe
public final class FDateTimeFormatter {

    private static final char PICOSECOND_CHAR = 'P';
    private static final char NANOSECOND_CHAR = 'N';
    private static final char MICROSECOND_CHAR = 'U';

    private static final int PATTERN_CACHE_SIZE = 500;
    private static final Map<String, FDateTimeFormatter> PATTERN_CACHE = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

    private final String pattern;
    private final DateTimeFormatter jodaFormatter;
    private final InternalPrinterAccessor printer;
    private final InternalParserAccessor parser;
    private final ParseToken[] parseTokens;
    private final ParseToken[] parseTokensByIndex;

    private FDateTimeFormatter(final String pattern) {
        this.pattern = pattern;

        final StringBuilder jodaPattern = new StringBuilder(pattern.length() + 5);
        final List<ParseToken> tokens = new ArrayList<>();

        // We track TWO states:
        // 1. Did the user ask for quotes?
        // 2. Are we currently inside quotes in the Joda builder?
        boolean userInsideQuotes = false;
        boolean jodaInsideQuotes = false;
        int shift = 0;

        for (int i = 0; i < pattern.length(); i++) {
            final char c = pattern.charAt(i);

            if (c == '\'') {
                // Handle Joda's escaped quote '' (literal single quote)
                if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
                    if (!jodaInsideQuotes) {
                        jodaPattern.append('\'');
                        jodaInsideQuotes = true;
                        shift++;
                    }
                    jodaPattern.append("''");
                    shift++; // '' becomes a single ' in the output, so it shifts the index by 1 more
                    i++; // Skip the second quote
                    continue;
                }

                // Standard quote: Just toggle the user's state.
                // We DO NOT append to jodaPattern here.
                // We let the character content drive the Joda quotes!
                userInsideQuotes = !userInsideQuotes;
                continue;
            }

            final boolean isCustomToken = (!userInsideQuotes
                    && (c == MICROSECOND_CHAR || c == NANOSECOND_CHAR || c == PICOSECOND_CHAR));

            // A character NEEDS quotes if the user asked for them, OR if it's our custom token
            final boolean needsQuotes = userInsideQuotes || isCustomToken;

            // Sync the actual Joda pattern quotes with our needs
            if (needsQuotes && !jodaInsideQuotes) {
                jodaPattern.append('\'');
                jodaInsideQuotes = true;
                shift++; // Joda consumes this
            } else if (!needsQuotes && jodaInsideQuotes) {
                jodaPattern.append('\'');
                jodaInsideQuotes = false;
                shift++; // Joda consumes this
            }

            // Now, append the character
            if (isCustomToken) {
                final int start = i;
                while (i < pattern.length() && pattern.charAt(i) == c) {
                    i++;
                }
                final int length = i - start;
                i--; // Adjust because the for-loop will increment

                if (length > 3) {
                    throw new IllegalArgumentException("Precision token '" + c + "' exceeds max length of 3.");
                }

                final int rawIndex = jodaPattern.length();
                for (int k = 0; k < length; k++) {
                    jodaPattern.append(c);
                }

                // The index calculation remains flawless
                tokens.add(new ParseToken(c, length, rawIndex - shift));
            } else {
                jodaPattern.append(c);
            }
        }

        // Close the Joda quote block if the string ends while we are still inside one
        if (jodaInsideQuotes) {
            jodaPattern.append('\'');
            shift++;
        }

        this.parseTokens = tokens.toArray(ParseToken.EMPTY_ARRAY);
        this.parseTokensByIndex = new ParseToken[jodaPattern.length() - shift];
        for (int i = 0; i < parseTokensByIndex.length; i++) {
            for (int t = 0; t < parseTokens.length; t++) {
                final ParseToken token = parseTokens[t];
                if (token.index <= i && i <= token.endIndex) {
                    parseTokensByIndex[i] = token;
                    break;
                }
            }
        }

        this.jodaFormatter = DateTimeFormat.forPattern(jodaPattern.toString());
        this.printer = new InternalPrinterAccessor(jodaFormatter);
        this.parser = new InternalParserAccessor(jodaFormatter);
    }

    @Override
    public String toString() {
        return pattern;
    }

    protected Map<FTimeZone, FDateTimeFormatter> newZonedCache() {
        return ILockCollectionFactory.getInstance(true).newConcurrentMap();
    }

    public static FDateTimeFormatter forPattern(final String pattern) {
        FDateTimeFormatter formatter = PATTERN_CACHE.get(pattern);
        if (formatter == null) {
            formatter = new FDateTimeFormatter(pattern);
            if (PATTERN_CACHE.size() < PATTERN_CACHE_SIZE) {
                // the size check is not locked against concurrent access,
                // but is accepted to be slightly off in contention scenarios.
                final FDateTimeFormatter oldFormatter = PATTERN_CACHE.putIfAbsent(pattern, formatter);
                if (oldFormatter != null) {
                    formatter = oldFormatter;
                }
            }
        }
        return formatter;
    }

    public String print(final long millis, final int picos, final FTimeZone timeZone, final Locale locale) {
        final StringBuilder sb = new StringBuilder(printer.estimatePrintedLength());
        printTo(sb, millis, picos, timeZone, locale);
        return sb.toString();
    }

    public void printTo(final StringBuffer buf, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) {
        try {
            printTo(buf, buf.length(), buf::setCharAt, millis, picos, timeZone, locale);
        } catch (final IOException ex) {
            // StringBuffer does not throw IOException
        }
    }

    public void printTo(final StringBuilder buf, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) {
        try {
            printTo(buf, buf.length(), buf::setCharAt, millis, picos, timeZone, locale);
        } catch (final IOException ex) {
            // StringBuffer does not throw IOException
        }
    }

    public void printTo(final Writer out, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) throws IOException {
        printTo((Appendable) out, millis, picos, timeZone, locale);
    }

    public void printTo(final Appendable appendable, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) throws IOException {
        if (parseTokens.length == 0) {
            jodaPrintTo(appendable, millis, timeZone, locale);
            return;
        }

        // 1. Get the base string from Joda-Time
        // We use a StringBuilder to allow us to modify the buffer in-place
        final StringBuilder sb = new StringBuilder(jodaFormatter.getPrinter().estimatePrintedLength());
        jodaPrintTo(sb, millis, timeZone, locale);

        // 2. Modify the characters directly in the StringBuilder
        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = token.extractor.applyAsInt(picos);

            for (int i = token.length - 1; i >= 0; i--) {
                // Setting a char at a specific index in StringBuilder is an O(1) operation
                sb.setCharAt(token.index + i, (char) ('0' + (value % 10)));
                value /= 10;
            }
        }

        // 3. Append to the final target
        appendable.append(sb);
    }

    private void printTo(final Appendable appendable, final int sizeBefore, final ISetCharAtFunction setCharAtF,
            final long millis, final int picos, final FTimeZone timeZone, final Locale locale) throws IOException {

        // 1. Get the base string from Joda-Time
        // We use a StringBuilder to allow us to modify the buffer in-place
        jodaPrintTo(appendable, millis, timeZone, locale);

        if (parseTokens.length == 0) {
            return;
        }

        // 2. Modify the characters directly in the StringBuilder
        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = token.extractor.applyAsInt(picos);

            for (int i = token.endIndex; i >= token.index; i--) {
                // Setting a char at a specific index in StringBuilder is an O(1) operation
                setCharAtF.setCharAt(sizeBefore + i, (char) ('0' + (value % 10)));
                value /= 10;
            }
        }
    }

    private void jodaPrintTo(final Appendable appendable, final long millis, final FTimeZone timeZone,
            final Locale locale) {
        //System.out.println("TODO: verify this converts the timestamp correctly");
        try {
            final Chronology chrono = timeZone.getChronology();
            // Shift instant into local time (UTC) to avoid excessive offset
            // calculations when printing multiple fields in a composite printer.
            DateTimeZone zone = chrono.getZone();
            int offset = zone.getOffset(millis);
            long adjustedInstant = millis + offset;
            if ((millis ^ adjustedInstant) < 0 && (millis ^ offset) >= 0) {
                // Time zone offset overflow, so revert to UTC.
                zone = DateTimeZone.UTC;
                offset = 0;
                adjustedInstant = millis;
            }
            printer.printTo(appendable, adjustedInstant, chrono.withUTC(), offset, zone, locale);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FDate parse(final String text, final FTimeZone timeZone, final Locale locale) {
        // Joda correctly skips quoted placeholders, so no replacement needed.
        final long millis = jodaParseMillis(text, timeZone, locale);

        int picos = 0;
        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = 0;
            // Direct char-to-int conversion without substring or parseInt
            for (int i = 0; i < token.length; i++) {
                final char c = text.charAt(token.index + i);
                value = (value * 10) + (c - '0');
            }

            // Apply the multiplier (e.g., 1_000_000 for micros, 1_000 for nanos, 1 for picos)
            picos += token.multiplier.applyAsInt(value);
        }

        return new FDate(millis, picos);
    }

    private long jodaParseMillis(final String text, final FTimeZone timeZone, final Locale locale) {
        final Chronology chrono = timeZone.getChronology();
        final DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, locale, jodaFormatter.getPivotYear(),
                jodaFormatter.getDefaultYear());
        return doParseMillis(bucket, text);
    }

    public long doParseMillis(final DateTimeParserBucket bucket, final CharSequence text) {
        int curPos = 0;
        while (curPos < text.length()) {
            int newPos = parser.parseInto(bucket, text, curPos);
            if (newPos >= 0) {
                if (newPos >= text.length()) {
                    return bucket.computeMillis(true, text);
                }
            } else {
                newPos = -newPos;
                if (newPos >= text.length()) {
                    return bucket.computeMillis(true, text);
                }
                ParseToken parseTokenAtNewPos = parseTokensByIndex[newPos];
                boolean found = false;
                while (parseTokenAtNewPos != null) {
                    found = true;
                    // We found a custom token at the position where Joda failed to parse
                    // This means we need to skip over this token and try parsing again
                    newPos = parseTokenAtNewPos.endIndex + 1; // Move past the entire token
                    if (newPos < parseTokensByIndex.length) {
                        parseTokenAtNewPos = parseTokensByIndex[newPos];
                    } else {
                        break;
                    }
                }
                if (!found) {
                    newPos++;
                }
                curPos = newPos;
            }
        }
        if (curPos == text.length()) {
            return bucket.computeMillis(true, text);
        }
        throw new IllegalArgumentException(FormatUtilsAccessor.createErrorMessage(text.toString(), curPos));
    }

    private static class ParseToken {
        public static final ParseToken[] EMPTY_ARRAY = new ParseToken[0];
        private final int index;
        private final int length;
        private final int endIndex;
        // Pre-calculate the value extraction function
        private final IntUnaryOperator extractor;
        private final Int2IntFunction multiplier;

        ParseToken(final char type, final int length, final int index) {
            this.index = index;
            this.length = length;
            this.endIndex = index + length - 1;
            switch (type) {
            case MICROSECOND_CHAR:
                this.extractor = FDatePicos::getMicrosecond;
                this.multiplier = v -> v * FTimeUnit.PICOSECONDS_IN_MICROSECOND;
                break;
            case NANOSECOND_CHAR:
                this.extractor = FDatePicos::getNanosecond;
                this.multiplier = v -> v * FTimeUnit.PICOSECONDS_IN_NANOSECOND;
                break;
            case PICOSECOND_CHAR:
                this.extractor = FDatePicos::getPicosecond;
                this.multiplier = v -> v;
                break;
            default:
                throw UnknownArgumentException.newInstance(char.class, type);
            }
        }
    }

    @FunctionalInterface
    private interface ISetCharAtFunction {
        void setCharAt(int index, char ch);
    }

}