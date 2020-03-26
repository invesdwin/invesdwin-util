package de.invesdwin.util.lang;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Optionals {

    private Optionals() {
    }

    public static <T> Optional<T> maybeWrapNull(final Optional<T> optional) {
        if (optional == null) {
            return Optional.empty();
        } else {
            return optional;
        }
    }

    public static <T> T orElse(final Optional<T> optional, final T other) {
        if (optional == null) {
            return null;
        } else {
            return optional.orElse(other);
        }
    }

    public static <T> T orNull(final Optional<T> optional) {
        return orElse(optional, null);
    }

}
