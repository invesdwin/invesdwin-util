package de.invesdwin.util.log.adaptor;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.LogManager;

import de.invesdwin.util.log.ILog;

@ThreadSafe
public class Log4j2DelegateLog implements ILog {

    private final org.apache.logging.log4j.Logger delegate;

    public Log4j2DelegateLog(final String name) {
        this(LogManager.getLogger(name));
    }

    public Log4j2DelegateLog(final Class<?> clazz) {
        this(LogManager.getLogger(clazz));
    }

    public Log4j2DelegateLog(final org.apache.logging.log4j.Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void catching(final Throwable throwable) {
        delegate.catching(throwable);
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
    public void error(final String format, final Object... params) {
        delegate.error(format, params);
    }

    @Override
    public boolean isFatalEnabled() {
        return delegate.isFatalEnabled();
    }

    @Override
    public void fatal(final String msg) {
        delegate.fatal(msg);
    }

    @Override
    public void fatal(final String format, final Object arg) {
        delegate.fatal(format, arg);
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {
        delegate.fatal(format, p0, p1);
    }

    @Override
    public void fatal(final String format, final Object... params) {
        delegate.fatal(format, params);
    }

}
