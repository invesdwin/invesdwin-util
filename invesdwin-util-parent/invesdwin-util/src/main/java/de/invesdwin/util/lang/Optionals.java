package de.invesdwin.util.lang;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Optionals {

    private Optionals() {}

    public static <T> Optional<T> maybeWrapNull(final Optional<T> optional) {
        if (optional == null) {
            return Optional.empty();
        } else {
            return optional;
        }
    }

}
