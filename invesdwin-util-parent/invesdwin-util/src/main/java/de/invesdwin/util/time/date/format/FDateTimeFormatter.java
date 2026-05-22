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
    private final boolean hasCustomFractions;

    private FDateTimeFormatter(final String pattern) {
        this.pattern = pattern;

        final StringBuilder jodaPattern = new StringBuilder(pattern.length() + 5);
        final List<ParseToken> tokens = new ArrayList<>();
        final int shift = parsePattern(pattern, jodaPattern, tokens);
        this.parseTokens = tokens.toArray(ParseToken.EMPTY_ARRAY);
        this.parseTokensByIndex = new ParseToken[jodaPattern.length() - shift];
        this.hasCustomFractions = parseTokens.length > 0;

        if (hasCustomFractions) {
            for (int i = 0; i < parseTokensByIndex.length; i++) {
                for (int t = 0; t < parseTokens.length; t++) {
                    final ParseToken token = parseTokens[t];
                    if (token.index <= i && i <= token.endIndex) {
                        parseTokensByIndex[i] = token;
                        break;
                    }
                }
            }
        }

        this.jodaFormatter = DateTimeFormat.forPattern(jodaPattern.toString());
        this.printer = new InternalPrinterAccessor(jodaFormatter);
        this.parser = new InternalParserAccessor(jodaFormatter);
    }

    private int parsePattern(final String pattern, final StringBuilder jodaPattern, final List<ParseToken> tokens) {
        boolean userInsideQuotes = false;
        boolean jodaInsideQuotes = false;
        int shift = 0;

        for (int i = 0; i < pattern.length(); i++) {
            final char c = pattern.charAt(i);

            if (c == '\'') {
                if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
                    if (!jodaInsideQuotes) {
                        jodaPattern.append('\'');
                        jodaInsideQuotes = true;
                        shift++;
                    }
                    jodaPattern.append("''");
                    shift++;
                    i++;
                    continue;
                }
                userInsideQuotes = !userInsideQuotes;
                continue;
            }

            final boolean isCustomToken = (!userInsideQuotes
                    && (c == MICROSECOND_CHAR || c == NANOSECOND_CHAR || c == PICOSECOND_CHAR));
            final boolean needsQuotes = userInsideQuotes || isCustomToken;

            if (needsQuotes && !jodaInsideQuotes) {
                jodaPattern.append('\'');
                jodaInsideQuotes = true;
                shift++;
            } else if (!needsQuotes && jodaInsideQuotes) {
                jodaPattern.append('\'');
                jodaInsideQuotes = false;
                shift++;
            }

            if (isCustomToken) {
                final int start = i;
                while (i < pattern.length() && pattern.charAt(i) == c) {
                    i++;
                }
                final int length = i - start;
                i--;

                if (length > 3) {
                    throw new IllegalArgumentException("Precision token '" + c + "' exceeds max length of 3.");
                }

                final int rawIndex = jodaPattern.length();
                for (int k = 0; k < length; k++) {
                    jodaPattern.append(c);
                }

                tokens.add(new ParseToken(c, length, rawIndex - shift));
            } else {
                jodaPattern.append(c);
            }
        }

        if (jodaInsideQuotes) {
            jodaPattern.append('\'');
            shift++;
        }
        return shift;
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
            // StringBuilder does not throw IOException
        }
    }

    public void printTo(final Writer out, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) throws IOException {
        printTo((Appendable) out, millis, picos, timeZone, locale);
    }

    public void printTo(final Appendable appendable, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) throws IOException {

        // Smart delegation: Bypass buffer creation if Appendable is secretly a StringBuilder/Buffer
        if (appendable instanceof StringBuilder) {
            printTo((StringBuilder) appendable, millis, picos, timeZone, locale);
            return;
        }
        if (appendable instanceof StringBuffer) {
            printTo((StringBuffer) appendable, millis, picos, timeZone, locale);
            return;
        }

        if (!hasCustomFractions) {
            jodaPrintTo(appendable, millis, timeZone, locale);
            return;
        }

        // 1. Get the base string from Joda-Time into a temporary modifiable buffer
        final StringBuilder sb = new StringBuilder(printer.estimatePrintedLength());
        jodaPrintTo(sb, millis, timeZone, locale);

        // 2. Modify the characters directly in the StringBuilder
        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = token.extractor.applyAsInt(picos);

            for (int i = token.endIndex; i >= token.index; i--) {
                sb.setCharAt(i, (char) ('0' + (value % 10)));
                value /= 10;
            }
        }

        // 3. Append the finalized string to the write-only target
        appendable.append(sb);
    }

    private void printTo(final Appendable appendable, final int sizeBefore, final ISetCharAtFunction setCharAtF,
            final long millis, final int picos, final FTimeZone timeZone, final Locale locale) throws IOException {

        jodaPrintTo(appendable, millis, timeZone, locale);

        if (!hasCustomFractions) {
            return;
        }

        if (!FDatePicos.isValidPicos(picos)) {
            throw new IllegalArgumentException(
                    "Picos value out of range [0 to " + FDatePicos.END_OF_DAY_PICOS + "]: " + picos);
        }

        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = token.extractor.applyAsInt(picos);

            for (int i = token.endIndex; i >= token.index; i--) {
                setCharAtF.setCharAt(sizeBefore + i, (char) ('0' + (value % 10)));
                value /= 10;
            }
        }
    }

    private void jodaPrintTo(final Appendable appendable, final long millis, final FTimeZone timeZone,
            final Locale locale) {
        try {
            final Chronology chrono = timeZone.getChronology();
            DateTimeZone zone = chrono.getZone();
            int offset = zone.getOffset(millis);
            long adjustedInstant = millis + offset;
            if ((millis ^ adjustedInstant) < 0 && (millis ^ offset) >= 0) {
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
        final CharSequence jodaInput = hasCustomFractions ? new VirtualJodaCharSequence(text, parseTokensByIndex)
                : text;
        final long millis = jodaParseMillis(jodaInput, timeZone, locale);

        int picos = 0;
        for (int t = 0; t < parseTokens.length; t++) {
            final ParseToken token = parseTokens[t];
            int value = 0;
            for (int i = 0; i < token.length; i++) {
                final char c = text.charAt(token.index + i);
                value = (value * 10) + (c - '0');
            }
            picos += token.multiplier.applyAsInt(value);
        }

        return new FDate(millis, picos);
    }

    private long jodaParseMillis(final CharSequence text, final FTimeZone timeZone, final Locale locale) {
        final Chronology chrono = timeZone.getChronology();
        final DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, locale, jodaFormatter.getPivotYear(),
                jodaFormatter.getDefaultYear());
        return parser.doParseMillis(bucket, text);
    }

    private static class ParseToken {
        public static final ParseToken[] EMPTY_ARRAY = new ParseToken[0];
        private final char type;
        private final int index;
        private final int length;
        private final int endIndex;
        private final IntUnaryOperator extractor;
        private final Int2IntFunction multiplier;

        ParseToken(final char type, final int length, final int index) {
            this.type = type;
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

    private static final class VirtualJodaCharSequence implements CharSequence {
        private final String delegate;
        private final ParseToken[] parseTokensByIndex;
        private final int offset;

        VirtualJodaCharSequence(final String delegate, final ParseToken[] parseTokensByIndex) {
            this(delegate, parseTokensByIndex, 0);
        }

        private VirtualJodaCharSequence(final String delegate, final ParseToken[] parseTokensByIndex,
                final int offset) {
            this.delegate = delegate;
            this.parseTokensByIndex = parseTokensByIndex;
            this.offset = offset;
        }

        @Override
        public int length() {
            return delegate.length();
        }

        @Override
        public char charAt(final int index) {
            final int absoluteIndex = offset + index;
            if (absoluteIndex >= 0 && absoluteIndex < parseTokensByIndex.length) {
                final ParseToken token = parseTokensByIndex[absoluteIndex];
                if (token != null) {
                    return token.type;
                }
            }
            return delegate.charAt(index);
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new VirtualJodaCharSequence(delegate.substring(start, end), parseTokensByIndex, offset + start);
        }

        @Override
        public String toString() {
            return delegate;
        }
    }
}