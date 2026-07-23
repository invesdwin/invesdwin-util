package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

/**
 * Adapted from org.joda.time.format.DateTimeParserInternalParser as a workaround when the java module system is enabled
 * in restricted environments.
 */
@Immutable
final class DateTimeParserInternalParserFallback implements IInternalParserAccessor {

    private final DateTimeParser underlying;

    private DateTimeParserInternalParserFallback(final DateTimeParser underlying) {
        this.underlying = underlying;
    }

    static IInternalParserAccessor of(final DateTimeParser underlying) {
        if (underlying == null) {
            return null;
        }
        return new DateTimeParserInternalParserFallback(underlying);
    }

    //-----------------------------------------------------------------------
    DateTimeParser getUnderlying() {
        return underlying;
    }

    @Override
    public int estimateParsedLength() {
        return underlying.estimateParsedLength();
    }

    @Override
    public int parseInto(final DateTimeParserBucket bucket, final CharSequence text, final int position) {
        return underlying.parseInto(bucket, text.toString(), position);
    }

}
