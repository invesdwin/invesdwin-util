package de.invesdwin.util.error;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.exception.ExceptionUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.internal.AThrowablesStaticFacade;
import de.invesdwin.util.lang.Strings;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.error.internal.AThrowablesStaticFacade", targets = {
        com.google.common.base.Throwables.class, org.fest.util.Throwables.class,
        org.fest.reflect.util.Throwables.class })
public final class Throwables extends AThrowablesStaticFacade {

    private static boolean finalizerDebugStackTraceEnabled;

    private Throwables() {}

    public static void setFinalizerDebugStackTraceEnabled(final boolean finalizerDebugStackTraceEnabled) {
        Throwables.finalizerDebugStackTraceEnabled = finalizerDebugStackTraceEnabled;
    }

    public static boolean isFinalizerDebugStackTraceEnabled() {
        return finalizerDebugStackTraceEnabled;
    }

    public static <T extends Throwable> boolean isCausedByType(@Nonnull final Throwable e,
            @Nonnull final Class<T> type) {
        return getCauseByType(e, type) != null;
    }

    @SuppressWarnings("unchecked" /* is safe since typecheck is done */)
    public static <T extends Throwable> T getCauseByType(@Nonnull final Throwable e, @Nonnull final Class<T> type) {
        Assertions.assertThat(e).isNotNull();
        Assertions.assertThat(type).isNotNull();
        Throwable cause = e;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return (T) cause;
            }
            cause = getCause(cause);
        }
        return (T) null;
    }

    /**
     * Handles InvocationTargetException properly
     */
    public static Throwable getCause(final Throwable t) {
        Throwable cause = null;
        if (t instanceof InvocationTargetException) {
            final InvocationTargetException ite = (InvocationTargetException) t;
            cause = ite.getTargetException();
        }
        if (cause == null) {
            cause = t.getCause();
        }
        return cause;
    }

    public static String concatMessages(final Throwable e) {
        final StringBuilder sb = new StringBuilder();
        Throwable cause = e;
        while (cause != null) {
            if (sb.length() > 0) {
                sb.append("\nCaused by ");
            }
            sb.append(cause.toString());
            cause = getCause(cause);
        }
        return sb.toString();
    }

    public static boolean isCausedByMessagePart(final Throwable e, final String messagePart) {
        Throwable cause = e;
        while (cause != null) {
            if (Strings.containsIgnoreCase(cause.getMessage(), messagePart)) {
                return true;
            }
            cause = getCause(cause);
        }
        return false;
    }

    public static String getFullStackTrace(final Throwable t) {
        return ExceptionUtils.getStackTrace(t);
    }

}
