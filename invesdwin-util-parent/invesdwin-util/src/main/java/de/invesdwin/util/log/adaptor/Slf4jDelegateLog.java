package de.invesdwin.util.log.adaptor;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;

@ThreadSafe
public class Slf4jDelegateLog implements ILog {

    private final org.slf4j.Logger delegate;
    private final org.slf4j.ext.XLogger xDelegate;

    public Slf4jDelegateLog(final String name) {
        this(org.slf4j.ext.XLoggerFactory.getXLogger(name));
    }

    public Slf4jDelegateLog(final Class<?> clazz) {
        this(org.slf4j.ext.XLoggerFactory.getXLogger(clazz));
    }

    public Slf4jDelegateLog(final org.slf4j.Logger delegate) {
        this.delegate = delegate;
        if (delegate instanceof org.slf4j.ext.XLogger) {
            this.xDelegate = (org.slf4j.ext.XLogger) delegate;
        } else {
            this.xDelegate = new org.slf4j.ext.XLogger(delegate);
        }
    }

    public Slf4jDelegateLog(final org.slf4j.ext.XLogger delegate) {
        this.delegate = delegate;
        this.xDelegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        delegate.trace(msg);
    }

    @Override
    public void trace(final String format, final Object arg) {
        delegate.trace(format, arg);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1) {
        delegate.trace(format, p0, p1);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.trace(format, p0, p1, p2);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.trace(format, p0, p1, p2, p3);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.trace(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.trace(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.trace(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.trace(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.trace(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.trace(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void trace(final String format, final Object... params) {
        delegate.trace(format, params);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        delegate.debug(msg);
    }

    @Override
    public void debug(final String format, final Object arg) {
        delegate.debug(format, arg);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1) {
        delegate.debug(format, p0, p1);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.debug(format, p0, p1, p2);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.debug(format, p0, p1, p2, p3);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.debug(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.debug(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.debug(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.debug(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.debug(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.debug(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void debug(final String format, final Object... params) {
        delegate.debug(format, params);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(final String format, final Object arg) {
        delegate.info(format, arg);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1) {
        delegate.info(format, p0, p1);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.info(format, p0, p1, p2);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.info(format, p0, p1, p2, p3);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.info(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.info(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.info(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.info(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.info(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.info(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void info(final String format, final Object... params) {
        delegate.info(format, params);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        delegate.warn(msg);
    }

    @Override
    public void warn(final String format, final Object arg) {
        delegate.warn(format, arg);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1) {
        delegate.warn(format, p0, p1);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.warn(format, p0, p1, p2);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.warn(format, p0, p1, p2, p3);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.warn(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.warn(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.warn(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.warn(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.warn(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.warn(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void warn(final String format, final Object... params) {
        delegate.warn(format, params);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(final String format, final Object arg) {
        delegate.error(format, arg);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1) {
        delegate.error(format, p0, p1);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.error(format, p0, p1, p2);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.error(format, p0, p1, p2, p3);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.error(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.error(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void error(final String format, final Object... params) {
        delegate.error(format, params);
    }

    @Override
    public boolean isFatalEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void fatal(final String msg) {
        delegate.error(msg);
    }

    @Override
    public void fatal(final String format, final Object arg) {
        delegate.error(format, arg);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {
        delegate.error(format, p0, p1);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2) {
        delegate.error(format, p0, p1, p2);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        delegate.error(format, p0, p1, p2, p3);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        delegate.error(format, p0, p1, p2, p3, p4);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        delegate.error(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        delegate.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Override
    public void fatal(final String format, final Object... params) {
        delegate.error(format, params);
    }

    @Override
    public void catching(final Throwable throwable) {
        xDelegate.catching(throwable);
    }

    @Override
    public void catching(final LogLevel level, final Throwable throwable) {
        xDelegate.catching(level.asSlf4jLevel(), throwable);
    }

}
