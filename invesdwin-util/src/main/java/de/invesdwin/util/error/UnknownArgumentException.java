package de.invesdwin.util.error;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@SuppressWarnings("serial")
@NotThreadSafe
public final class UnknownArgumentException extends IllegalArgumentException {

    private final Class<?> argumentClass;
    private final Object argument;

    private UnknownArgumentException(@Nonnull final Class<?> argumentClass, final Object argument) {
        super("Unknown " + argumentClass.getSimpleName() + ": " + argument);
        this.argumentClass = argumentClass;
        this.argument = argument;
    }

    public Class<?> getArgumentClass() {
        return argumentClass;
    }

    public Object getArgument() {
        return argument;
    }

    public static <T> UnknownArgumentException newInstance(@Nonnull final Class<T> argumentClass, final T argument) {
        return new UnknownArgumentException(argumentClass, argument);
    }

}
