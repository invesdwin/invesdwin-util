package de.invesdwin.util.assertions.type.internal;

import javax.annotation.concurrent.Immutable;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

@Immutable
public final class ShouldBeNullOrBlank extends BasicErrorMessageFactory {

    private ShouldBeNullOrBlank(final Object actual) {
        super("\nExpecting null or blank but was:<%s>", actual);
    }

    public static ErrorMessageFactory shouldBeNullOrBlank(final Object actual) {
        return new ShouldBeNullOrBlank(actual);
    }

}
