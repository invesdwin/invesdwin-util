package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

@Immutable
final class DirectInternalParserAccessor implements IInternalParserAccessor {

    private final InternalParser parser;

    DirectInternalParserAccessor(final DateTimeFormatter formatter) {
        this(formatter.getParser0());
    }

    DirectInternalParserAccessor(final InternalParser parser) {
        this.parser = parser;
    }

    public InternalParser getParser() {
        return parser;
    }

    @Override
    public int estimateParsedLength() {
        return parser.estimateParsedLength();
    }

    @Override
    public int parseInto(final DateTimeParserBucket bucket, final CharSequence text, final int position) {
        return parser.parseInto(bucket, text, position);
    }

    public long doParseMillis(final DateTimeParserBucket bucket, final CharSequence text) {
        return bucket.doParseMillis(parser, text);
    }

}
