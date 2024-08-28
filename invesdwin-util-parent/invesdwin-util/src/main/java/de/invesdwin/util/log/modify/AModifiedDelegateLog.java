package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Marker;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;

@Immutable
public abstract class AModifiedDelegateLog implements ILog {

    private final ILog delegate;

    public AModifiedDelegateLog(final ILog delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    protected final String modifyIfEnabled(final LogLevel level, final String msg) {
        if (!level.isEnabled(delegate)) {
            return msg;
        }
        return modify(level, msg);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String msg, final Throwable t) {
        if (!level.isEnabled(delegate)) {
            return msg;
        }
        return modify(level, msg, t);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object arg) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, arg);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object arg1,
            final Object arg2) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, arg1, arg2);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object... arguments) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, arguments);
    }

    protected abstract String modify(LogLevel level, String msg);

    protected abstract String modify(LogLevel level, String msg, Throwable t);

    protected abstract String modify(LogLevel level, String format, Object arg);

    protected abstract String modify(LogLevel level, String format, Object arg1, Object arg2);

    protected abstract String modify(LogLevel level, String format, Object... arguments);

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, msg));
    }

    @Override
    public void trace(final String format, final Object arg) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, arg), arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, arguments), arguments);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, msg, t), t);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        delegate.trace(marker, modifyIfEnabled(LogLevel.TRACE, msg));
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        delegate.trace(marker, modifyIfEnabled(LogLevel.TRACE, format, arg), arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.trace(marker, modifyIfEnabled(LogLevel.TRACE, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... arguments) {
        delegate.trace(marker, modifyIfEnabled(LogLevel.TRACE, format, arguments), arguments);
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        delegate.trace(marker, modifyIfEnabled(LogLevel.TRACE, msg, t), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, msg));
    }

    @Override
    public void debug(final String format, final Object arg) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, arg), arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, arguments), arguments);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, msg, t), t);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        delegate.debug(marker, modifyIfEnabled(LogLevel.DEBUG, msg));
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        delegate.debug(marker, modifyIfEnabled(LogLevel.DEBUG, format, arg), arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.debug(marker, modifyIfEnabled(LogLevel.DEBUG, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        delegate.debug(marker, modifyIfEnabled(LogLevel.DEBUG, format, arguments), arguments);
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        delegate.debug(marker, modifyIfEnabled(LogLevel.DEBUG, msg, t), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, msg));
    }

    @Override
    public void info(final String format, final Object arg) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, arg), arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void info(final String format, final Object... arguments) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, arguments), arguments);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, msg, t), t);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(final Marker marker, final String msg) {
        delegate.info(marker, modifyIfEnabled(LogLevel.INFO, msg));
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        delegate.info(marker, modifyIfEnabled(LogLevel.INFO, format, arg), arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.info(marker, modifyIfEnabled(LogLevel.INFO, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        delegate.info(marker, modifyIfEnabled(LogLevel.INFO, format, arguments), arguments);
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        delegate.info(marker, modifyIfEnabled(LogLevel.INFO, msg, t), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, msg));
    }

    @Override
    public void warn(final String format, final Object arg) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, arg), arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, arguments), arguments);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, msg, t), t);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        delegate.warn(marker, modifyIfEnabled(LogLevel.WARN, msg));
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        delegate.warn(marker, modifyIfEnabled(LogLevel.WARN, format, arg), arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.warn(marker, modifyIfEnabled(LogLevel.WARN, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        delegate.warn(marker, modifyIfEnabled(LogLevel.WARN, format, arguments), arguments);
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        delegate.warn(marker, modifyIfEnabled(LogLevel.WARN, msg, t), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, msg));
    }

    @Override
    public void error(final String format, final Object arg) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, arg), arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void error(final String format, final Object... arguments) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, arguments), arguments);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, msg, t), t);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(final Marker marker, final String msg) {
        delegate.error(marker, modifyIfEnabled(LogLevel.ERROR, msg));
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        delegate.error(marker, modifyIfEnabled(LogLevel.ERROR, format, arg), arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.error(marker, modifyIfEnabled(LogLevel.ERROR, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        delegate.error(marker, modifyIfEnabled(LogLevel.ERROR, format, arguments), arguments);
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        delegate.error(marker, modifyIfEnabled(LogLevel.ERROR, msg, t), t);
    }

}
