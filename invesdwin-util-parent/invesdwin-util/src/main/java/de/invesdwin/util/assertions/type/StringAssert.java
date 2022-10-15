package de.invesdwin.util.assertions.type;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertionInfo;

import de.invesdwin.util.assertions.type.internal.ShouldBeBlank;
import de.invesdwin.util.assertions.type.internal.ShouldBeNullOrBlank;
import de.invesdwin.util.assertions.type.internal.ShouldNotBeBlank;
import de.invesdwin.util.lang.string.Strings;

@NotThreadSafe
public class StringAssert extends AbstractStringAssert<StringAssert> {

    private final org.assertj.core.internal.Failures failures = org.assertj.core.internal.Failures.instance();

    public StringAssert(final String actual) {
        super(actual, StringAssert.class);
    }

    @Override
    public StringAssert isBlank() {
        assertBlank(info, actual);
        return myself;
    }

    @Override
    public StringAssert isNotBlank() {
        assertNotBlank(info, actual);
        return myself;
    }

    public StringAssert isNullOrBlank() {
        assertNullOrBlank(info, actual);
        return myself;
    }

    private void assertNullOrBlank(final AssertionInfo info, final CharSequence actual) {
        if (actual == null || Strings.isBlank(actual)) {
            return;
        }
        throw failures.failure(info, ShouldBeNullOrBlank.shouldBeNullOrBlank(actual));
    }

    private void assertBlank(final AssertionInfo info, final CharSequence actual) {
        assertNotNull(info, actual);
        if (Strings.isBlank(actual)) {
            return;
        }
        throw failures.failure(info, ShouldBeBlank.shouldBeBlank(actual));
    }

    private void assertNotBlank(final AssertionInfo info, final CharSequence actual) {
        assertNotNull(info, actual);
        if (Strings.isNotBlank(actual)) {
            return;
        }
        throw failures.failure(info, ShouldNotBeBlank.shouldNotBeBlank());
    }

    private void assertNotNull(final AssertionInfo info, final CharSequence actual) {
        org.assertj.core.internal.Objects.instance().assertNotNull(info, actual);
    }

}
