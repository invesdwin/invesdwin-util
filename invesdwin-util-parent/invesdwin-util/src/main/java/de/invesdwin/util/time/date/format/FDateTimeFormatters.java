package de.invesdwin.util.time.date.format;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import io.netty.util.concurrent.FastThreadLocal;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@ThreadSafe
public final class FDateTimeFormatters {

    private static final FastThreadLocal<FDateMillisToStringCached> CACHED_TO_STRING_HOLDER = new FastThreadLocal<FDateMillisToStringCached>() {
        @Override
        protected FDateMillisToStringCached initialValue() throws Exception {
            return new FDateMillisToStringCached();
        };
    };

    private static final class FDateMillisToStringCached {

        private static final int MAX_SIZE = 10;
        private final Long2ObjectMap<FDatePicosToStringCached> millis_picos_toString = new Long2ObjectOpenHashMap<>(
                MAX_SIZE);
        private final Long2ObjectMap<String> millis_toString = new Long2ObjectOpenHashMap<>(MAX_SIZE);

        public String toString(final long millis, final int picos) {
            if (millis_picos_toString.size() >= MAX_SIZE) {
                millis_picos_toString.clear();
            }
            final FDatePicosToStringCached picosCached = millis_picos_toString.computeIfAbsent(millis,
                    m -> new FDatePicosToStringCached(m));
            return picosCached.toString(picos);
        }

        public String toString(final long millis) {
            if (millis_toString.size() >= MAX_SIZE) {
                millis_toString.clear();
            }
            if (millis_toString.size() >= MAX_SIZE) {
                millis_toString.clear();
            }
            return millis_toString.computeIfAbsent(millis, (Long2ObjectFunction<String>) key -> FDateTimeFormatters
                    .toString(key, FDate.FORMAT_ISO_DATE_TIME_MS));
        }

    }

    private static final class FDatePicosToStringCached {
        private final long millis;
        private final Int2ObjectMap<String> picos_toString = new Int2ObjectOpenHashMap<>(
                FDateMillisToStringCached.MAX_SIZE);
        private String zeroPicosString;

        private FDatePicosToStringCached(final long millis) {
            this.millis = millis;
        }

        public String toString(final int picos) {
            if (picos == 0) {
                if (zeroPicosString == null) {
                    zeroPicosString = FDateTimeFormatters.toString(millis, 0, FDate.FORMAT_ISO_DATE_TIME_PS);
                }
                return zeroPicosString;
            }

            if (picos_toString.size() >= FDateMillisToStringCached.MAX_SIZE) {
                picos_toString.clear();
            }
            return picos_toString.computeIfAbsent(picos, (Int2ObjectFunction<String>) key -> FDateTimeFormatters
                    .toString(millis, key, FDate.FORMAT_ISO_DATE_TIME_PS));
        }
    }

    private FDateTimeFormatters() {}

    public static String toString(final long millis) {
        final FDateMillisToStringCached cachedToString = CACHED_TO_STRING_HOLDER.get();
        return cachedToString.toString(millis);
    }

    public static String toString(final long millis, final FTimeZone timeZone) {
        return toString(millis, FDate.FORMAT_ISO_DATE_TIME_MS, timeZone);
    }

    public static String toString(final long millis, final String format) {
        return toString(millis, format, null);
    }

    public static String toString(final long millis, final String format, final FTimeZone timeZone) {
        final FDateTimeFormatter df = FDateTimeFormatter.forPattern(format);
        if (timeZone != null) {
            return df.print(millis, 0, timeZone, null);
        } else {
            return df.print(millis, 0, FDates.getDefaultTimeZone(), null);
        }
    }

    public static String toString(final long millis, final int picos) {
        final FDateMillisToStringCached cachedToString = CACHED_TO_STRING_HOLDER.get();
        return cachedToString.toString(millis, picos);
    }

    public static String toString(final long millis, final int picos, final FTimeZone timeZone) {
        return toString(millis, picos, FDate.FORMAT_ISO_DATE_TIME_PS, timeZone);
    }

    public static String toString(final long millis, final int picos, final String format) {
        return toString(millis, picos, format, null);
    }

    public static String toString(final long millis, final int picos, final String format, final FTimeZone timeZone) {
        final FDateTimeFormatter df = FDateTimeFormatter.forPattern(format);
        if (timeZone != null) {
            return df.print(millis, picos, timeZone, null);
        } else {
            return df.print(millis, picos, FDates.getDefaultTimeZone(), null);
        }
    }

}
