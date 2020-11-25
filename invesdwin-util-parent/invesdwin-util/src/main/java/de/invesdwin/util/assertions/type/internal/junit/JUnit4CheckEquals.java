package de.invesdwin.util.assertions.type.internal.junit;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescriptionFormatter;

@Immutable
public final class JUnit4CheckEquals {

    private static final int COMPARISON_FAILURE_MESSAGE_LIMIT = Assertions.COMPARISON_FAILURE_MESSAGE_LIMIT;

    public JUnit4CheckEquals() {
    }

    public void checkEqualsJunit(final String expected, final String actual, final String message,
            final Object... args) {
        try {
            org.junit.Assert.assertEquals(TextDescriptionFormatter.format(message, args), expected, actual);
        } catch (final org.junit.ComparisonFailure e) {
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new org.junit.ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual()) {
                @Override
                public String getMessage() {
                    return abbreviatedMessage;
                }
            };
        }
    }

    public void checkEqualsJunit(final String expected, final String actual) {
        try {
            org.junit.Assert.assertEquals(expected, actual);
        } catch (final org.junit.ComparisonFailure e) {
            //limit message length or else eclipse freezes in junit dialog
            final String abbreviatedMessage = Strings.abbreviate(e.getMessage(), COMPARISON_FAILURE_MESSAGE_LIMIT);
            throw new org.junit.ComparisonFailure(abbreviatedMessage, e.getExpected(), e.getActual()) {
                @Override
                public String getMessage() {
                    return abbreviatedMessage;
                }
            };
        }
    }

}
