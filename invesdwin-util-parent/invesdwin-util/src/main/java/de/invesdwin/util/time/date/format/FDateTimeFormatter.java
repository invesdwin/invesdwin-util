package de.invesdwin.util.time.date.format;

import java.io.IOException;
import java.io.Writer;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import javax.annotation.concurrent.ThreadSafe;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.FormatUtilsAccessor;
import org.joda.time.format.InternalParserAccessor;
import org.joda.time.format.InternalPrinterAccessor;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.ToStringHelper;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.date.millis.FDatePicos;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;

@ThreadSafe
public final class FDateTimeFormatter extends java.text.Format {

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
    private final FTimeZone timeZone;
    private final Locale locale;

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
        this.timeZone = null;
        this.locale = null;
    }

    private FDateTimeFormatter(final FDateTimeFormatter copyOf, final FTimeZone defaultTimeZone,
            final Locale defaultLocale) {
        this.pattern = copyOf.pattern;
        this.jodaFormatter = copyOf.jodaFormatter;
        this.printer = copyOf.printer;
        this.parser = copyOf.parser;
        this.parseTokens = copyOf.parseTokens;
        this.parseTokensByIndex = copyOf.parseTokensByIndex;
        this.hasCustomFractions = copyOf.hasCustomFractions;
        this.timeZone = defaultTimeZone;
        this.locale = defaultLocale;
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
        final ToStringHelper tsh = Objects.toStringHelper(this).add("pattern", pattern);
        if (timeZone != null) {
            tsh.add("defaultTimeZone", timeZone);
        }
        if (locale != null) {
            tsh.add("defaultLocale", locale);
        }
        return tsh.toString();
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

    public String print(final Object obj) {
        return print(obj, null);
    }

    public String print(final Object obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    //CHECKSTYLE:OFF
    public String print(final Object obj, final FTimeZone timeZone, final Locale locale) {
        //CHECKSTYLE:ON
        if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof Date) {
            final Date cObj = (Date) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof Calendar) {
            final Calendar cObj = (Calendar) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof ReadableDateTime) {
            final ReadableDateTime cObj = (ReadableDateTime) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof LocalDateTime) {
            final LocalDateTime cObj = (LocalDateTime) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof ReadablePartial) {
            final ReadablePartial cObj = (ReadablePartial) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof LocalDate) {
            final LocalDate cObj = (LocalDate) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof java.time.ZonedDateTime) {
            final java.time.ZonedDateTime cObj = (java.time.ZonedDateTime) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof java.time.LocalDateTime) {
            final java.time.LocalDateTime cObj = (java.time.LocalDateTime) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj instanceof java.time.LocalDate) {
            final java.time.LocalDate cObj = (java.time.LocalDate) obj;
            return print(cObj, timeZone, locale);
        }
        if (obj == null) {
            throw new NullPointerException("Cannot format null object");
        }
        throw UnknownArgumentException.newInstance(Class.class, obj.getClass());
    }

    public String print(final FDate obj) {
        return print(obj, null);
    }

    public String print(final FDate obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final FDate obj, final FTimeZone timeZone, final Locale locale) {
        return print(obj.millisValue(), obj.picosValue(), timeZone, locale);
    }

    public String print(final Date obj) {
        return print(obj, null);
    }

    public String print(final Date obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final Date obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final Calendar obj) {
        return print(obj, null);
    }

    public String print(final Calendar obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final Calendar obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final ReadableDateTime obj) {
        return print(obj, null);
    }

    public String print(final ReadableDateTime obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final ReadableDateTime obj, final FTimeZone timeZone, final Locale locale) {
        return print(obj.getMillis(), 0, timeZone, locale);
    }

    public String print(final LocalDateTime obj) {
        return print(obj, null);
    }

    public String print(final LocalDateTime obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final LocalDateTime obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final ReadablePartial obj) {
        return print(obj, null);
    }

    public String print(final ReadablePartial obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final ReadablePartial obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final LocalDate obj) {
        return print(obj, null);
    }

    public String print(final LocalDate obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final LocalDate obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final java.time.ZonedDateTime obj) {
        return print(obj, null);
    }

    public String print(final java.time.ZonedDateTime obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final java.time.ZonedDateTime obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final java.time.LocalDateTime obj) {
        return print(obj, null);
    }

    public String print(final java.time.LocalDateTime obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final java.time.LocalDateTime obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final java.time.LocalDate obj) {
        return print(obj, null);
    }

    public String print(final java.time.LocalDate obj, final FTimeZone timeZone) {
        return print(obj, timeZone, null);
    }

    public String print(final java.time.LocalDate obj, final FTimeZone timeZone, final Locale locale) {
        return print(FDateMillis.valueOfNotNullSafe(obj), 0, timeZone, locale);
    }

    public String print(final long millis, final int picos) {
        return print(millis, picos, null);
    }

    public String print(final long millis, final int picos, final FTimeZone timeZone) {
        return print(millis, picos, timeZone, null);
    }

    public String print(final long millis, final int picos, final FTimeZone timeZone, final Locale locale) {
        final StringBuilder sb = new StringBuilder(printer.estimatePrintedLength());
        printTo(sb, millis, picos, timeZone, locale);
        return sb.toString();
    }

    public void printTo(final StringBuffer buf, final long millis, final int picos) {
        printTo(buf, millis, picos, null);
    }

    public void printTo(final StringBuffer buf, final long millis, final int picos, final FTimeZone timeZone) {
        printTo(buf, millis, picos, timeZone, null);
    }

    public void printTo(final StringBuffer buf, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) {
        try {
            printTo(buf, buf.length(), buf::setCharAt, millis, picos, timeZone, locale);
        } catch (final IOException ex) {
            // StringBuffer does not throw IOException
        }
    }

    public void printTo(final StringBuilder buf, final long millis, final int picos) {
        printTo(buf, millis, picos, null);
    }

    public void printTo(final StringBuilder buf, final long millis, final int picos, final FTimeZone timeZone) {
        printTo(buf, millis, picos, timeZone, null);
    }

    public void printTo(final StringBuilder buf, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) {
        try {
            printTo(buf, buf.length(), buf::setCharAt, millis, picos, timeZone, locale);
        } catch (final IOException ex) {
            // StringBuilder does not throw IOException
        }
    }

    public void printTo(final Writer out, final long millis, final int picos) throws IOException {
        printTo(out, millis, picos, null);
    }

    public void printTo(final Writer out, final long millis, final int picos, final FTimeZone timeZone)
            throws IOException {
        printTo(out, millis, picos, timeZone, null);
    }

    public void printTo(final Writer out, final long millis, final int picos, final FTimeZone timeZone,
            final Locale locale) throws IOException {
        printTo((Appendable) out, millis, picos, timeZone, locale);
    }

    public void printTo(final Appendable appendable, final long millis, final int picos) throws IOException {
        printTo(appendable, millis, picos, null);
    }

    public void printTo(final Appendable appendable, final long millis, final int picos, final FTimeZone timeZone)
            throws IOException {
        printTo(appendable, millis, picos, timeZone, null);
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
            final FTimeZone usedTimeZone = determineTimeZone(timeZone);
            final Chronology chrono = usedTimeZone.getChronology();
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

    private FTimeZone determineTimeZone(final FTimeZone timeZone) {
        if (timeZone != null) {
            return timeZone;
        } else if (timeZone != null) {
            return timeZone;
        } else {
            return FDates.getDefaultTimeZone();
        }
    }

    private Locale determineLocale(final Locale locale) {
        if (locale != null) {
            return locale;
        }
        return locale;
    }

    public FDate parse(final String text) {
        return parse(text, null);
    }

    public FDate parse(final String text, final FTimeZone timeZone) {
        return parse(text, timeZone, null);
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
        final FTimeZone usedTimeZone = determineTimeZone(timeZone);
        final Locale usedLocale = determineLocale(locale);
        final Chronology chrono = usedTimeZone.getChronology();
        final DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, usedLocale,
                jodaFormatter.getPivotYear(), jodaFormatter.getDefaultYear());
        //return parser.doParseMillis(bucket, text);
        int newPos = parser.parseInto(bucket, text, 0);
        if (newPos >= 0) {
            if (newPos >= text.length()) {
                return bucket.computeMillis(false, text);
            }
        } else {
            newPos = ~newPos;
        }
        throw new IllegalArgumentException(FormatUtilsAccessor.createErrorMessage(text.toString(), newPos));
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

    //CHECKSTYLE:OFF
    @Deprecated
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        //CHECKSTYLE:ON
        if (obj instanceof FDate) {
            final FDate cObj = (FDate) obj;
            // Ignore FieldPosition (pos); we just append the formatted date to the buffer.
            // Using null for timezone and locale to trigger your global fallback defaults.
            printTo(toAppendTo, cObj.millisValue(), cObj.picosValue());
            return toAppendTo;
        }
        if (obj instanceof Date) {
            final Date cObj = (Date) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof Calendar) {
            final Calendar cObj = (Calendar) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof ReadableDateTime) {
            final ReadableDateTime cObj = (ReadableDateTime) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof LocalDateTime) {
            final LocalDateTime cObj = (LocalDateTime) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof ReadablePartial) {
            final ReadablePartial cObj = (ReadablePartial) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof LocalDate) {
            final LocalDate cObj = (LocalDate) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof java.time.ZonedDateTime) {
            final java.time.ZonedDateTime cObj = (java.time.ZonedDateTime) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof java.time.LocalDateTime) {
            final java.time.LocalDateTime cObj = (java.time.LocalDateTime) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj instanceof java.time.LocalDate) {
            final java.time.LocalDate cObj = (java.time.LocalDate) obj;
            printTo(toAppendTo, FDateMillis.valueOfNotNullSafe(cObj), 0);
            return toAppendTo;
        }
        if (obj == null) {
            throw new NullPointerException("Cannot format null object");
        }
        throw UnknownArgumentException.newInstance(Class.class, obj.getClass());
    }

    @Deprecated
    @Override
    public FDate parseObject(final String source) throws ParseException {
        final int startIndex = 0;
        // 1. Shift the virtual mask backward by startIndex so Joda's absolute
        // string queries align perfectly with our 0-indexed token array.
        final CharSequence jodaInput = hasCustomFractions
                ? new VirtualJodaCharSequence(source, parseTokensByIndex, -startIndex)
                : source;

        // 2. Setup the parser bucket using the default un-zoned chronology
        // to match your Absolute UTC Paradigm
        final Chronology chrono = FDates.getDefaultTimeZone().getChronology();
        final DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, null, jodaFormatter.getPivotYear(),
                jodaFormatter.getDefaultYear());

        // 3. Let Joda-Time parse starting directly at the specified index
        final int newPos = parser.parseInto(bucket, jodaInput, startIndex);

        if (newPos < 0) {
            // Joda-Time returns the bitwise complement (~newPos) of the index where it failed
            throw new ParseException("Failed to parse date: " + source, ~newPos);
        }

        final long millis;
        try {
            // Compute milliseconds based on the successful extraction bounds
            millis = bucket.computeMillis(false, jodaInput);
        } catch (final IllegalArgumentException ex) {
            // Catch invalid calendar dates (e.g., February 29th on a non-leap year)
            throw new ParseException("Failed to parse date: " + source, startIndex);
        }

        int picos = 0;
        if (hasCustomFractions) {
            for (int t = 0; t < parseTokens.length; t++) {
                final ParseToken token = parseTokens[t];
                int value = 0;
                for (int i = 0; i < token.length; i++) {
                    // Extract the raw digits directly from the source string
                    // shifted forward by the starting position
                    final char c = source.charAt(startIndex + token.index + i);
                    value = (value * 10) + (c - '0');
                }
                picos += token.multiplier.applyAsInt(value);
            }
        }

        return new FDate(millis, picos);
    }

    @Deprecated
    @Override
    public FDate parseObject(final String source, final ParsePosition pos) {
        final int startIndex = pos.getIndex();

        // 1. Shift the virtual mask backward by startIndex so Joda's absolute
        // string queries align perfectly with our 0-indexed token array.
        final CharSequence jodaInput = hasCustomFractions
                ? new VirtualJodaCharSequence(source, parseTokensByIndex, -startIndex)
                : source;

        // 2. Setup the parser bucket using the default un-zoned chronology
        // to match your Absolute UTC Paradigm
        final Chronology chrono = FDates.getDefaultTimeZone().getChronology();
        final DateTimeParserBucket bucket = new DateTimeParserBucket(0, chrono, null, jodaFormatter.getPivotYear(),
                jodaFormatter.getDefaultYear());

        // 3. Let Joda-Time parse starting directly at the specified index
        final int newPos = parser.parseInto(bucket, jodaInput, startIndex);

        if (newPos < 0) {
            // Joda-Time returns the bitwise complement (~newPos) of the index where it failed
            pos.setErrorIndex(~newPos);
            return null;
        }

        final long millis;
        try {
            // Compute milliseconds based on the successful extraction bounds
            millis = bucket.computeMillis(false, jodaInput);
        } catch (final IllegalArgumentException ex) {
            // Catch invalid calendar dates (e.g., February 29th on a non-leap year)
            pos.setErrorIndex(startIndex);
            return null;
        }

        int picos = 0;
        if (hasCustomFractions) {
            for (int t = 0; t < parseTokens.length; t++) {
                final ParseToken token = parseTokens[t];
                int value = 0;
                for (int i = 0; i < token.length; i++) {
                    // Extract the raw digits directly from the source string
                    // shifted forward by the starting position
                    final char c = source.charAt(startIndex + token.index + i);
                    value = (value * 10) + (c - '0');
                }
                picos += token.multiplier.applyAsInt(value);
            }
        }

        // 4. Update the ParsePosition to signal how much text was consumed
        pos.setIndex(newPos);
        return new FDate(millis, picos);
    }

    public <T> T parse(final Class<T> type, final String value) {
        return parse(type, value, null);
    }

    public <T> T parse(final Class<T> type, final String value, final FTimeZone timeZone) {
        return parse(type, value, timeZone, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(final Class<T> type, final String value, final FTimeZone timeZone, final Locale locale) {
        final FDate date = parse(value, timeZone, locale);
        if (type == FDate.class) {
            return (T) date;
        }
        if (type == Date.class) {
            return (T) date.dateValue();
        }
        if (type == Calendar.class) {
            return (T) date.calendarValue();
        }
        if (type == LocalDateTime.class) {
            return (T) date.jodaTimeValue();
        }
        if (type == ZonedDateTime.class || type == ReadableDateTime.class) {
            return (T) date.jodaTimeValueZoned();
        }
        if (type == LocalDate.class || type == ReadablePartial.class) {
            return (T) date.jodaDateValue();
        }
        if (type == java.time.LocalDateTime.class) {
            return (T) date.javaTimeValue();
        }
        if (type == java.time.ZonedDateTime.class) {
            return (T) date.javaTimeValueZoned();
        }
        if (type == java.time.LocalDate.class) {
            return (T) date.javaDateValue();
        }
        throw UnknownArgumentException.newInstance(Class.class, type);
    }

    //CHECKSTYLE:OFF
    public FDateTimeFormatter withTimeZoneId(final String timeZoneId) {
        //CHECKSTYLE:OF
        if (timeZoneId == null) {
            return withTimeZone(null);
        } else if (timeZone != null && Objects.equals(this.timeZone.getId(), timeZoneId)) {
            return this;
        } else {
            return withTimeZone(FTimeZone.valueOf(timeZoneId));
        }
    }

    //CHECKSTYLE:OFF
    public FDateTimeFormatter withTimeZone(final FTimeZone timeZone) {
        //CHECKSTYLE:OF
        if (Objects.equals(this.timeZone, timeZone)) {
            return this;
        }
        return new FDateTimeFormatter(this, timeZone, locale);
    }

    //CHECKSTYLE:OFF
    public FDateTimeFormatter withLocale(final Locale locale) {
        //CHECKSTYLE:OF
        if (Objects.equals(this.locale, locale)) {
            return this;
        }
        return new FDateTimeFormatter(this, timeZone, locale);
    }

    public String getPattern() {
        return pattern;
    }

    public FTimeZone getTimeZone() {
        return timeZone;
    }

    public Locale getLocale() {
        return locale;
    }

}