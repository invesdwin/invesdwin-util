package org.joda.time.format;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public class InternalParserAccessor {

    private static final MethodHandle MH_DATETIMEFORMATTER_IPARSER;

    static {
        final Field iParserField = Reflections.findField(DateTimeFormatter.class, "iParser");
        Reflections.makeAccessible(iParserField);
        final Lookup lookup = MethodHandles.lookup();
        try {
            MH_DATETIMEFORMATTER_IPARSER = lookup.unreflectGetter(iParserField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final InternalParser parser;

    public InternalParserAccessor(final DateTimeFormatter formatter) {
        try {
            this.parser = (InternalParser) MH_DATETIMEFORMATTER_IPARSER.invoke(formatter);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public InternalParserAccessor(final InternalParser parser) {
        this.parser = parser;
    }

    public InternalParser getParser() {
        return parser;
    }

    public int estimateParsedLength() {
        return parser.estimateParsedLength();
    }

    public int parseInto(final DateTimeParserBucket bucket, final CharSequence text, final int position) {
        return parser.parseInto(bucket, text, position);
    }

    public long doParseMillis(final DateTimeParserBucket bucket, final CharSequence text) {
        return bucket.doParseMillis(parser, text);
    }

}
