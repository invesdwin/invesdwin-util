package org.joda.time.format;

/**
 * Internal interface for parsing textual representations of datetimes.
 * <p>
 * This has been separated from {@link DateTimeParser} to change to using {@code CharSequence}.
 *
 * @author Stephen Colebourne
 * @since 2.4
 */
public interface IInternalParserAccessor {

    /**
     * Returns the expected maximum number of characters consumed. The actual amount should rarely exceed this estimate.
     * 
     * @return the estimated length
     */
    int estimateParsedLength();

    /**
     * Parse an element from the given text, saving any fields into the given DateTimeParserBucket. If the parse
     * succeeds, the return value is the new text position. Note that the parse may succeed without fully reading the
     * text.
     * <p>
     * If it fails, the return value is negative. To determine the position where the parse failed, apply the one's
     * complement operator (~) on the return value.
     *
     * @param bucket
     *            field are saved into this, not null
     * @param text
     *            the text to parse, not null
     * @param position
     *            position to start parsing from
     * @return new position, negative value means parse failed - apply complement operator (~) to get position of
     *         failure
     * @throws IllegalArgumentException
     *             if any field is out of range
     */
    int parseInto(DateTimeParserBucket bucket, CharSequence text, int position);

    static IInternalParserAccessor of(final DateTimeFormatter formatter) {
        try {
            return new DirectInternalParserAccessor(formatter);
        } catch (final Throwable t) {
            //must be a restricted environment, fallback to a slighly less efficient variant that has to use CharSequence.toString() to parseInto
            return DateTimeParserInternalParserFallback.of(formatter.getParser());
        }
    }

}
