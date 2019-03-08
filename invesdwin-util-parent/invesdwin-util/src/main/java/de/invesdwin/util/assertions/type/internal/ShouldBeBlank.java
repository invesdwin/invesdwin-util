package de.invesdwin.util.assertions.type.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

@NotThreadSafe
public final class ShouldBeBlank extends BasicErrorMessageFactory {

    private ShouldBeBlank(final Object actual) {
        super("\nExpecting blank but was:<%s>", actual);
    }

    public static ErrorMessageFactory shouldBeBlank(final Object actual) {
        return new ShouldBeBlank(actual);
    }

}
