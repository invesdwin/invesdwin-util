package de.invesdwin.util.error;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.description.TextDescription;

/**
 * This exceptions can be used to interrupt (in a lightweight way) a calling stack to do some maintenance before
 * returning back deeper into the call stack.
 * 
 * Though better provide an adequate reason where and why it was thrown so that debugging is a bit easier.
 * 
 * Anyhow, stacktraces can be enabled for this exception via Throwables.setDebugStackTraceEnabled(true).
 * 
 */
@NotThreadSafe
public final class MaintenanceIntervalException extends RuntimeException {

    private static final MaintenanceIntervalException INSTANCE = new MaintenanceIntervalException(
            "maintenance interval reached");

    private static final long serialVersionUID = 1L;

    /**
     * We always want a message here with some interesting information about the origin, since the stacktrace is
     * disabled. At least we can then search the code for the string.
     */
    private MaintenanceIntervalException(final String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        } else {
            return this; // no stack trace for performance
        }
    }

    public static MaintenanceIntervalException getInstance(final String message) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new MaintenanceIntervalException(message);
        } else {
            return INSTANCE;
        }
    }

    public static MaintenanceIntervalException getInstance(final String message, final Object arg) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new MaintenanceIntervalException(TextDescription.format(message, arg));
        } else {
            return INSTANCE;
        }
    }

    public static MaintenanceIntervalException getInstance(final String message, final Object arg1,
            final Object arg2) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new MaintenanceIntervalException(TextDescription.format(message, arg1, arg2));
        } else {
            return INSTANCE;
        }
    }

    public static MaintenanceIntervalException getInstance(final String message, final Object... args) {
        if (Throwables.isDebugStackTraceEnabled()) {
            return new MaintenanceIntervalException(TextDescription.format(message, args));
        } else {
            return INSTANCE;
        }
    }

    public static MaintenanceIntervalException getInstance(final Throwable cause) {
        return getInstance(cause.getMessage(), cause);
    }

    public static MaintenanceIntervalException getInstance(final String message, final Throwable cause) {
        if (Throwables.isDebugStackTraceEnabled()) {
            final MaintenanceIntervalException eof = new MaintenanceIntervalException(message);
            if (cause != null) {
                eof.initCause(cause);
            }
            return eof;
        } else {
            return INSTANCE;
        }
    }

}
