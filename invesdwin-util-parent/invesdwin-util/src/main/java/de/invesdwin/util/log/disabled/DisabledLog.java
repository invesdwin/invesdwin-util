package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Marker;

import de.invesdwin.util.log.ILog;

@Immutable
public final class DisabledLog implements ILog {

    public static final DisabledLog INSTANCE = new DisabledLog();

    private DisabledLog() {}

    @Override
    public String getName() {
        return DisabledLog.class.getSimpleName();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(final String msg) {}

    @Override
    public void trace(final String format, final Object arg) {}

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void trace(final String format, final Object... arguments) {}

    @Override
    public void trace(final String msg, final Throwable t) {}

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void trace(final Marker marker, final String msg) {}

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {}

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void trace(final Marker marker, final String format, final Object... argArray) {}

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {}

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(final String msg) {}

    @Override
    public void debug(final String format, final Object arg) {}

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void debug(final String format, final Object... arguments) {}

    @Override
    public void debug(final String msg, final Throwable t) {}

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void debug(final Marker marker, final String msg) {}

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {}

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {}

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {}

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(final String msg) {}

    @Override
    public void info(final String format, final Object arg) {}

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void info(final String format, final Object... arguments) {}

    @Override
    public void info(final String msg, final Throwable t) {}

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void info(final Marker marker, final String msg) {}

    @Override
    public void info(final Marker marker, final String format, final Object arg) {}

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {}

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {}

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(final String msg) {}

    @Override
    public void warn(final String format, final Object arg) {}

    @Override
    public void warn(final String format, final Object... arguments) {}

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void warn(final String msg, final Throwable t) {}

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void warn(final Marker marker, final String msg) {}

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {}

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {}

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {}

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(final String msg) {}

    @Override
    public void error(final String format, final Object arg) {}

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void error(final String format, final Object... arguments) {}

    @Override
    public void error(final String msg, final Throwable t) {}

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void error(final Marker marker, final String msg) {}

    @Override
    public void error(final Marker marker, final String format, final Object arg) {}

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {}

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {}

}
