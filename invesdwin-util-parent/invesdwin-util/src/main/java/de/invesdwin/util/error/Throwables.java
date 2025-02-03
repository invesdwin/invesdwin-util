package de.invesdwin.util.error;

import java.nio.channels.ClosedByInterruptException;

import javax.annotation.concurrent.Immutable;

import org.agrona.LangUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;
import de.invesdwin.util.concurrent.handler.UncaughtExecutorExceptionHandler;
import de.invesdwin.util.error.internal.AThrowablesStaticFacade;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Integers;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.error.internal.AThrowablesStaticFacade", targets = {
        org.fest.util.Throwables.class,
        org.fest.reflect.util.Throwables.class }, filterMethodSignatureExpressions = { ".* throwCause\\(.*" })
public final class Throwables extends AThrowablesStaticFacade {

    private static boolean debugStackTraceEnabled = false;

    private static IExecutorExceptionHandler defaultExecutorExceptionHandler = newDefaultExecutorUncaughtExceptionHandler();

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

    public static IExecutorExceptionHandler getDefaultExecutorExceptionHandler() {
        return defaultExecutorExceptionHandler;
    }

    public static void setDefaultExecutorExceptionHandler(
            final IExecutorExceptionHandler defaultExecutorExceptionHandler) {
        if (defaultExecutorExceptionHandler == null) {
            Throwables.defaultExecutorExceptionHandler = newDefaultExecutorUncaughtExceptionHandler();
        } else {
            Throwables.defaultExecutorExceptionHandler = defaultExecutorExceptionHandler;
        }
    }

    public static UncaughtExecutorExceptionHandler newDefaultExecutorUncaughtExceptionHandler() {
        return UncaughtExecutorExceptionHandler.INSTANCE;
    }

    public static <T> boolean isCausedByType(final Throwable e, final Class<T> type) {
        return getCauseByType(e, type) != null;
    }

    @SafeVarargs
    public static boolean isCausedByAnyType(final Throwable e, final Class<?>... types) {
        Throwable cause = e;
        while (cause != null) {
            for (int i = 0; i < types.length; i++) {
                final Class<?> type = types[i];
                if (type.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    @SuppressWarnings("unchecked" /* is safe since typecheck is done */)
    public static <T> T getCauseByType(final Throwable e, final Class<T> type) {
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

    public static String concatMessagesShort(final Throwable e) {
        final StringBuilder sb = new StringBuilder();
        Throwable cause = e;
        while (cause != null) {
            if (sb.length() > 0) {
                sb.append("\nCaused by ");
            }
            sb.append(cause.getClass().getSimpleName());
            sb.append(": ");
            sb.append(cause.getMessage());
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
     * Prints the first X stack trace elements with the exception info
     */
    public static String getShortStackTrace(final Throwable e, final int maxStacks) {
        final StringBuilder sb = new StringBuilder(e.toString());
        final StackTraceElement[] stackTrace = e.getStackTrace();
        for (int i = 0; i < maxStacks && i < stackTrace.length; i++) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            sb.append(" -> ");
            sb.append(stackTraceElement);
        }
        return sb.toString();
    }

    /**
     * Prints the first X stack trace elements with the exception info up to the stop condition
     */
    public static String getShortStackTrace(final Throwable e, final int maxStacksAround,
            final Object2BooleanFunction<StackTraceElement> stopCondition) {
        final StringBuilder sb = new StringBuilder(e.toString());
        final StackTraceElement[] stackTrace = e.getStackTrace();
        int ignoredStacks = 0;
        for (int i = 0; i < stackTrace.length; i++) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            if (i < maxStacksAround) {
                sb.append(" -> ");
                sb.append(stackTraceElement);
                if (stopCondition.getBoolean(stackTraceElement)) {
                    break;
                }
            } else if (stopCondition.getBoolean(stackTraceElement)) {
                if (ignoredStacks > 0) {
                    final int countSuffix = Integers.min(ignoredStacks, maxStacksAround);
                    ignoredStacks -= countSuffix;
                    if (ignoredStacks > 0) {
                        sb.append(" -> ... ");
                        sb.append(ignoredStacks);
                        sb.append(" more ... ");
                    }
                    for (int s = countSuffix; s > 0; s--) {
                        final StackTraceElement suffixStackTraceElement = stackTrace[i - s];
                        sb.append(" -> ");
                        sb.append(suffixStackTraceElement);
                    }
                }
                sb.append(" -> ");
                sb.append(stackTraceElement);
                break;
            } else {
                ignoredStacks++;
            }
        }
        return sb.toString();
    }

    /**
     * Prints the first X stack trace elements with the exception info filtered by base packages
     */
    public static String getShortStackTrace(final Throwable e, final int maxBasePackageStacks,
            final String[] basePackages) {
        final StringBuilder sb = new StringBuilder(e.toString());
        final StackTraceElement[] stackTrace = e.getStackTrace();
        int basePackageStacks = 0;
        for (int i = 0; basePackageStacks <= maxBasePackageStacks && i < stackTrace.length; i++) {
            final String stackTraceElement = stackTrace[i].toString();
            sb.append(" -> ");
            sb.append(stackTraceElement);
            if (Strings.startsWithAny(stackTraceElement, basePackages)) {
                basePackageStacks++;
            }
        }
        return sb.toString();
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

    public static String asStringOrExceptionMessage(final Object obj) {
        try {
            return obj.toString();
        } catch (final Throwable t) {
            return obj.getClass().getSimpleName() + ".toString[" + t.toString() + "]";
        }
    }

    public static boolean isCausedByInterrupt(final Throwable t) {
        return Threads.isInterrupted() || Throwables.isCausedByAnyType(t, InterruptedException.class,
                ClosedByInterruptException.class, InterruptedRuntimeException.class);
    }

    /**
     * Not throwing a RuntimeException, instead propagating the actual throwable
     */
    public static RuntimeException propagate(final Throwable t) {
        LangUtil.rethrowUnchecked(t);
        throw (RuntimeException) t;
    }

    public static IndexOutOfBoundsException newIndexOutOfBoundsException(final int index) {
        return new IndexOutOfBoundsException("Index out of range: " + index);
    }

}
