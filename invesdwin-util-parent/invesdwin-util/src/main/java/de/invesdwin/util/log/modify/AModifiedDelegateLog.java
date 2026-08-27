package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

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

    protected abstract String modify(LogLevel level, String msg);

    protected abstract String modify(LogLevel level, String format, Object p0);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8);

    protected abstract String modify(LogLevel level, String format, Object p0, Object p1, Object p2, Object p3,
            Object p4, Object p5, Object p6, Object p7, Object p8, Object p9);

    protected abstract String modify(LogLevel level, String format, Object... params);

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, msg));
        }
    }

    @Override
    public void trace(final String format, final Object p0) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0), p0);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8);
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4,
                    p5, p6, p7, p8, p9);
        }
    }

    @Override
    public void trace(final String format, final Object... params) {
        if (delegate.isTraceEnabled()) {
            delegate.trace(modify(LogLevel.TRACE, format, params), params);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, msg));
        }
    }

    @Override
    public void debug(final String format, final Object p0) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0), p0);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8);
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4,
                    p5, p6, p7, p8, p9);
        }
    }

    @Override
    public void debug(final String format, final Object... params) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(modify(LogLevel.DEBUG, format, params), params);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, msg));
        }
    }

    @Override
    public void info(final String format, final Object p0) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0), p0);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5, p6,
                    p7, p8);
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8, p9);
        }
    }

    @Override
    public void info(final String format, final Object... params) {
        if (delegate.isInfoEnabled()) {
            delegate.info(modify(LogLevel.INFO, format, params), params);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, msg));
        }
    }

    @Override
    public void warn(final String format, final Object p0) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0), p0);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5, p6,
                    p7, p8);
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8, p9);
        }
    }

    @Override
    public void warn(final String format, final Object... params) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(modify(LogLevel.WARN, format, params), params);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, msg));
        }
    }

    @Override
    public void error(final String format, final Object p0) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0), p0);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8);
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4,
                    p5, p6, p7, p8, p9);
        }
    }

    @Override
    public void error(final String format, final Object... params) {
        if (delegate.isErrorEnabled()) {
            delegate.error(modify(LogLevel.ERROR, format, params), params);
        }
    }

    @Override
    public boolean isFatalEnabled() {
        return delegate.isFatalEnabled();
    }

    @Override
    public void fatal(final String msg) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, msg));
        }
    }

    @Override
    public void fatal(final String format, final Object p0) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0), p0);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1), p0, p1);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2), p0, p1, p2);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3), p0, p1, p2, p3);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5, p6,
                    p7);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4, p5,
                    p6, p7, p8);
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3, p4,
                    p5, p6, p7, p8, p9);
        }
    }

    @Override
    public void fatal(final String format, final Object... params) {
        if (delegate.isFatalEnabled()) {
            delegate.fatal(modify(LogLevel.FATAL, format, params), params);
        }
    }

    @Override
    public void catching(final Throwable throwable) {
        delegate.catching(throwable);
    }

    @Override
    public void catching(final LogLevel level, final Throwable throwable) {
        delegate.catching(level, throwable);
    }

}
