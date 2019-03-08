package de.invesdwin.util.error;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;

@SuppressWarnings("serial")
@NotThreadSafe
public final class UnknownArgumentException extends IllegalArgumentException {

    private final Class<?> argumentClass;
    private final Object argument;

    private UnknownArgumentException(@Nonnull final Class<?> argumentClass, final Object argument) {
        super(newMessage(argumentClass, argument));
        this.argumentClass = argumentClass;
        this.argument = argument;
    }

    private static String newMessage(final Class<?> argumentClass, final Object argument) {
        final StringBuilder sb = new StringBuilder("Unknown ");
        sb.append(argumentClass.getSimpleName());
        sb.append(": ");
        sb.append(argument);
        if (argument != null) {
            sb.append(" (");
            sb.append(argument.getClass().getSimpleName());
            if (argument.getClass().isArray()) {
                sb.append("=");
                sb.append(Strings.checkedCast(argument));
            }
            sb.append(")");
        }
        return sb.toString();
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
