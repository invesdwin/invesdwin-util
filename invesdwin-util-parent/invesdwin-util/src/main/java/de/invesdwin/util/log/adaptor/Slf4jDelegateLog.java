package de.invesdwin.util.log.adaptor;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.log.ILog;

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
    public void catching(final Throwable throwable) {
        xDelegate.catching(throwable);
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
    public void trace(final String format, final Object arg1, final Object arg2) {
        delegate.trace(format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        delegate.trace(format, arguments);
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
    public void debug(final String format, final Object arg1, final Object arg2) {
        delegate.debug(format, arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        delegate.debug(format, arguments);
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
    public void info(final String format, final Object arg1, final Object arg2) {
        delegate.info(format, arg1, arg2);
    }

    @Override
    public void info(final String format, final Object... arguments) {
        delegate.info(format, arguments);
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
    public void warn(final String format, final Object arg1, final Object arg2) {
        delegate.warn(format, arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        delegate.warn(format, arguments);
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
    public void error(final String format, final Object arg1, final Object arg2) {
        delegate.error(format, arg1, arg2);
    }

    @Override
    public void error(final String format, final Object... arguments) {
        delegate.error(format, arguments);
    }

}
