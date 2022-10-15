package de.invesdwin.util.assertions.type.internal.junit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.string.description.TextDescription;

@Immutable
public final class JUnit5CheckEquals {

    private static final class ComparisonFailure extends org.opentest4j.AssertionFailedError {
        private final String abbreviatedMessage;

        private ComparisonFailure(final String message, final org.opentest4j.ValueWrapper expected,
                final org.opentest4j.ValueWrapper actual) {
            super(message, expected, actual);
            this.abbreviatedMessage = message;
        }

        @Override
        public String getMessage() {
            return abbreviatedMessage;
        }
    }

    private static final int COMPARISON_FAILURE_MESSAGE_LIMIT = Assertions.COMPARISON_FAILURE_MESSAGE_LIMIT;

    private JUnit5CheckEquals() {
    }

    public static void checkEqualsJunit(final String expected, final String actual, final String message,
            final Object... args) {
        try {
            org.junit.jupiter.api.Assertions.assertEquals(TextDescription.format(message, args), expected, actual);
        } catch (final org.opentest4j.AssertionFailedError e) {
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual());
        }
    }

    public static void checkEqualsJunit(final String expected, final String actual) {
        try {
            org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
        } catch (final org.opentest4j.AssertionFailedError e) {
            //limit message length or else eclipse freezes in junit dialog
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual());
        }
    }

}
