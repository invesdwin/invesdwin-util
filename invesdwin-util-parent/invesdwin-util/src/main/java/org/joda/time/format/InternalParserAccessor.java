package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

@Immutable
public class InternalParserAccessor {

    private final InternalParser parser;

    public InternalParserAccessor(final DateTimeFormatter formatter) {
        this.parser = formatter.getParser0();
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
