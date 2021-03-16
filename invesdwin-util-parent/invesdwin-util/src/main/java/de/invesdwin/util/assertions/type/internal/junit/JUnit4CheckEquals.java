package de.invesdwin.util.assertions.type.internal.junit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescription;

@Immutable
public final class JUnit4CheckEquals {

    private static final class ComparisonFailure extends org.junit.ComparisonFailure {
        private final String abbreviatedMessage;

        private ComparisonFailure(final String message, final String expected, final String actual) {
            super(message, expected, actual);
            this.abbreviatedMessage = message;
        }

        @Override
        public String getMessage() {
            return abbreviatedMessage;
        }
    }

    private static final int COMPARISON_FAILURE_MESSAGE_LIMIT = Assertions.COMPARISON_FAILURE_MESSAGE_LIMIT;

    private JUnit4CheckEquals() {
    }

    public static void checkEqualsJunit(final String expected, final String actual, final String message,
            final Object... args) {
        try {
            org.junit.Assert.assertEquals(TextDescription.format(message, args), expected, actual);
        } catch (final org.junit.ComparisonFailure e) {
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual());
        }
    }

    public static void checkEqualsJunit(final String expected, final String actual) {
        try {
            org.junit.Assert.assertEquals(expected, actual);
        } catch (final org.junit.ComparisonFailure e) {
            //limit message length or else eclipse freezes in junit dialog
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual());
        }
    }

}
