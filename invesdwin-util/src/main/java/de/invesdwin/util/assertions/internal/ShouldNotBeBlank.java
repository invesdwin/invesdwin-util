package de.invesdwin.util.assertions.internal;

import javax.annotation.concurrent.Immutable;

import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;

@Immutable
public final class ShouldNotBeBlank extends BasicErrorMessageFactory {

    private static final ShouldNotBeBlank INSTANCE = new ShouldNotBeBlank();

    private ShouldNotBeBlank() {
        super("\nExpecting actual not to be empty");
    }

    public static ErrorMessageFactory shouldNotBeBlank() {
        return INSTANCE;
    }

}
