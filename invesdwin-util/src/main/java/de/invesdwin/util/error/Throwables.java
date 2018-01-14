package de.invesdwin.util.error;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.exception.ExceptionUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.internal.AThrowablesStaticFacade;
import de.invesdwin.util.lang.Strings;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.error.internal.AThrowablesStaticFacade", targets = {
        com.google.common.base.Throwables.class, org.fest.util.Throwables.class,
        org.fest.reflect.util.Throwables.class }, filterMethodSignatureExpressions = { ".* throwCause\\(.*" })
public final class Throwables extends AThrowablesStaticFacade {

    private static boolean debugStackTraceEnabled;

    private Throwables() {}

    public static void setDebugStackTraceEnabled(final boolean debugStackTraceEnabled) {
        Throwables.debugStackTraceEnabled = debugStackTraceEnabled;
    }

    /**
     * This tells whether stack traces that are normally skipped for performance reasons are enabled (e.g. finalizers of
     * unclosed ACloseableIterators or exceptions that are normally handled for control logic and normally do not
     * require stack traces)
     */
    public static boolean isDebugStackTraceEnabled() {
        return debugStackTraceEnabled;
    }

    public static <T extends Throwable> boolean isCausedByType(final Throwable e, final Class<T> type) {
        return getCauseByType(e, type) != null;
    }

    @SuppressWarnings("unchecked" /* is safe since typecheck is done */)
    public static <T extends Throwable> T getCauseByType(final Throwable e, final Class<T> type) {
        Assertions.assertThat(e).isNotNull();
        Assertions.assertThat(type).isNotNull();
        Throwable cause = e;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return (T) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }

    public static String concatMessages(final Throwable e) {
        final StringBuilder sb = new StringBuilder();
        Throwable cause = e;
        while (cause != null) {
            if (sb.length() > 0) {
                sb.append("\nCaused by ");
            }
            sb.append(cause.toString());
            cause = cause.getCause();
        }
        return sb.toString();
    }

    public static boolean isCausedByMessagePart(final Throwable e, final String messagePart) {
        Throwable cause = e;
        while (cause != null) {
            if (Strings.containsIgnoreCase(cause.getMessage(), messagePart)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    public static String getFullStackTrace(final Throwable t) {
        return ExceptionUtils.getStackTrace(t);
    }

    @SafeVarargs
    public static Throwable ignoreType(final Throwable e, final Class<? extends Throwable>... ignoredTypes) {
        Throwable validCause = e;
        boolean ignoredSomething;
        do {
            ignoredSomething = false;
            for (final Class<? extends Throwable> ignoredType : ignoredTypes) {
                if (ignoredType.isInstance(validCause)) {
                    validCause = validCause.getCause();
                    ignoredSomething = true;
                    break;
                }
            }
        } while (ignoredSomething);
        return validCause;
    }

    /**
     * Prints the first stack trace element with the exception info
     */
    public static String getShortStackTrace(final Throwable e) {
        final StackTraceElement stackTraceElement = e.getStackTrace()[0];
        return e.toString() + " -> " + stackTraceElement;
    }

    public static RuntimeException newUnexpectedHereException(final Class<?> location, final Throwable e) {
        return new RuntimeException("This exception should never be thrown up to here: " + location.getSimpleName(), e);
    }

    public static String getMessage(final Throwable throwable) {
        if (throwable != null) {
            return throwable.getMessage();
        } else {
            return null;
        }
    }

}
