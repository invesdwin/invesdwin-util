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

    protected final String modifyIfEnabled(final LogLevel level, final String msg) {
        if (!level.isEnabled(delegate)) {
            return msg;
        }
        return modify(level, msg);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0,
            final Object p1) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4, p5);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4, p5, p6);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    protected final String modifyIfEnabled(final LogLevel level, final String format, final Object... params) {
        if (!level.isEnabled(delegate)) {
            return format;
        }
        return modify(level, format, params);
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
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, msg));
    }

    @Override
    public void trace(final String format, final Object p0) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0), p0);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1), p0, p1);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void trace(final String format, final Object... params) {
        delegate.trace(modifyIfEnabled(LogLevel.TRACE, format, params), params);
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
    public void debug(final String format, final Object p0) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0), p0);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1), p0, p1);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void debug(final String format, final Object... params) {
        delegate.debug(modifyIfEnabled(LogLevel.DEBUG, format, params), params);
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
    public void info(final String format, final Object p0) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0), p0);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1), p0, p1);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void info(final String format, final Object... params) {
        delegate.info(modifyIfEnabled(LogLevel.INFO, format, params), params);
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
    public void warn(final String format, final Object p0) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0), p0);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1), p0, p1);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void warn(final String format, final Object... params) {
        delegate.warn(modifyIfEnabled(LogLevel.WARN, format, params), params);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isFatalEnabled();
    }

    @Override
    public void error(final String msg) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, msg));
    }

    @Override
    public void error(final String format, final Object p0) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0), p0);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1), p0, p1);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void error(final String format, final Object... params) {
        delegate.error(modifyIfEnabled(LogLevel.ERROR, format, params), params);
    }

    @Override
    public boolean isFatalEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void fatal(final String msg) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, msg));
    }

    @Override
    public void fatal(final String format, final Object p0) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0), p0);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1), p0, p1);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2), p0, p1, p2);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3), p0, p1, p2, p3);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4), p0, p1, p2, p3, p4);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5), p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6), p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7), p0, p1, p2, p3, p4, p5,
                p6, p7);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7, p8), p0, p1, p2, p3, p4,
                p5, p6, p7, p8);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9), p0, p1, p2, p3,
                p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void fatal(final String format, final Object... params) {
        delegate.fatal(modifyIfEnabled(LogLevel.FATAL, format, params), params);
    }

}
