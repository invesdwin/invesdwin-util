package de.invesdwin.util.log.slf4j;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

import de.invesdwin.util.log.ILog;

@ThreadSafe
public class LoggerDelegateLog implements ILog {

    private final org.apache.logging.log4j.Logger delegate;

    public LoggerDelegateLog(final String name) {
        this(LogManager.getLogger(name));
    }

    public LoggerDelegateLog(final Class<?> clazz) {
        this(LogManager.getLogger(clazz));
    }

    public LoggerDelegateLog(final org.apache.logging.log4j.Logger delegate) {
        this.delegate = delegate;
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
    public void trace(final String format, final Object arg1, final Object arg2) {
        delegate.trace(format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        delegate.trace(format, arguments);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        delegate.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        delegate.trace(marker, msg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        delegate.trace(marker, format, arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... arguments) {
        delegate.trace(marker, format, arguments);
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        delegate.trace(marker, msg, t);
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
    public void debug(final String msg, final Throwable t) {
        delegate.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        delegate.debug(marker, msg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        delegate.debug(marker, format, arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        delegate.debug(marker, format, arguments);
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        delegate.debug(marker, msg, t);
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
    public void info(final String msg, final Throwable t) {
        delegate.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(final Marker marker, final String msg) {
        delegate.info(marker, msg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        delegate.info(marker, format, arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        delegate.info(marker, format, arguments);
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        delegate.info(marker, msg, t);
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
    public void warn(final String msg, final Throwable t) {
        delegate.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        delegate.warn(marker, msg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        delegate.warn(marker, format, arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        delegate.warn(marker, format, arguments);
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        delegate.warn(marker, msg, t);
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

    @Override
    public void error(final String msg, final Throwable t) {
        delegate.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(final Marker marker, final String msg) {
        delegate.error(marker, msg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        delegate.error(marker, format, arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        delegate.error(marker, format, arguments);
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        delegate.error(marker, msg, t);
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
    public void fatal(final String format, final Object arg1, final Object arg2) {
        delegate.fatal(format, arg1, arg2);
    }

    @Override
    public void fatal(final String format, final Object... arguments) {
        delegate.fatal(format, arguments);
    }

    @Override
    public void fatal(final String msg, final Throwable t) {
        delegate.fatal(msg, t);
    }

    @Override
    public boolean isFatalEnabled(final Marker marker) {
        return delegate.isFatalEnabled(marker);
    }

    @Override
    public void fatal(final Marker marker, final String msg) {
        delegate.fatal(marker, msg);
    }

    @Override
    public void fatal(final Marker marker, final String format, final Object arg) {
        delegate.fatal(marker, format, arg);
    }

    @Override
    public void fatal(final Marker marker, final String format, final Object arg1, final Object arg2) {
        delegate.fatal(marker, format, arg1, arg2);
    }

    @Override
    public void fatal(final Marker marker, final String format, final Object... arguments) {
        delegate.fatal(marker, format, arguments);
    }

    @Override
    public void fatal(final Marker marker, final String msg, final Throwable t) {
        delegate.fatal(marker, msg, t);
    }

    @Override
    public void catching(final Level level, final Throwable throwable) {}

    @Override
    public void catching(final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final Message message) {}

    @Override
    public void debug(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void debug(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final CharSequence message) {}

    @Override
    public void debug(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final Object message) {}

    @Override
    public void debug(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void debug(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void debug(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void debug(final Message message) {}

    @Override
    public void debug(final Message message, final Throwable throwable) {}

    @Override
    public void debug(final MessageSupplier messageSupplier) {}

    @Override
    public void debug(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void debug(final CharSequence message) {}

    @Override
    public void debug(final CharSequence message, final Throwable throwable) {}

    @Override
    public void debug(final Object message) {}

    @Override
    public void debug(final Object message, final Throwable throwable) {}

    @Override
    public void debug(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void debug(final Supplier<?> messageSupplier) {}

    @Override
    public void debug(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

    @Override
    public void entry() {}

    @Override
    public void entry(final Object... params) {}

    @Override
    public void error(final Marker marker, final Message message) {}

    @Override
    public void error(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void error(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void error(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void error(final Marker marker, final CharSequence message) {}

    @Override
    public void error(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void error(final Marker marker, final Object message) {}

    @Override
    public void error(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void error(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void error(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void error(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void error(final Message message) {}

    @Override
    public void error(final Message message, final Throwable throwable) {}

    @Override
    public void error(final MessageSupplier messageSupplier) {}

    @Override
    public void error(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void error(final CharSequence message) {}

    @Override
    public void error(final CharSequence message, final Throwable throwable) {}

    @Override
    public void error(final Object message) {}

    @Override
    public void error(final Object message, final Throwable throwable) {}

    @Override
    public void error(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void error(final Supplier<?> messageSupplier) {}

    @Override
    public void error(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

    @Override
    public void exit() {}

    @Override
    public <R> R exit(final R result) {
        return null;
    }

    @Override
    public void fatal(final Marker marker, final Message message) {}

    @Override
    public void fatal(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void fatal(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void fatal(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void fatal(final Marker marker, final CharSequence message) {}

    @Override
    public void fatal(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void fatal(final Marker marker, final Object message) {}

    @Override
    public void fatal(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void fatal(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void fatal(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void fatal(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void fatal(final Message message) {}

    @Override
    public void fatal(final Message message, final Throwable throwable) {}

    @Override
    public void fatal(final MessageSupplier messageSupplier) {}

    @Override
    public void fatal(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void fatal(final CharSequence message) {}

    @Override
    public void fatal(final CharSequence message, final Throwable throwable) {}

    @Override
    public void fatal(final Object message) {}

    @Override
    public void fatal(final Object message, final Throwable throwable) {}

    @Override
    public void fatal(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void fatal(final Supplier<?> messageSupplier) {}

    @Override
    public void fatal(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

    @Override
    public Level getLevel() {
        return null;
    }

    @Override
    public <MF extends MessageFactory> MF getMessageFactory() {
        return null;
    }

    @Override
    public FlowMessageFactory getFlowMessageFactory() {
        return null;
    }

    @Override
    public void info(final Marker marker, final Message message) {}

    @Override
    public void info(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void info(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void info(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void info(final Marker marker, final CharSequence message) {}

    @Override
    public void info(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void info(final Marker marker, final Object message) {}

    @Override
    public void info(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void info(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void info(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void info(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void info(final Message message) {}

    @Override
    public void info(final Message message, final Throwable throwable) {}

    @Override
    public void info(final MessageSupplier messageSupplier) {}

    @Override
    public void info(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void info(final CharSequence message) {}

    @Override
    public void info(final CharSequence message, final Throwable throwable) {}

    @Override
    public void info(final Object message) {}

    @Override
    public void info(final Object message, final Throwable throwable) {}

    @Override
    public void info(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void info(final Supplier<?> messageSupplier) {}

    @Override
    public void info(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

    @Override
    public boolean isEnabled(final Level level) {
        return false;
    }

    @Override
    public boolean isEnabled(final Level level, final Marker marker) {
        return false;
    }

    @Override
    public void log(final Level level, final Marker marker, final Message message) {}

    @Override
    public void log(final Level level, final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void log(final Level level, final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final CharSequence message) {}

    @Override
    public void log(final Level level, final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final Object message) {}

    @Override
    public void log(final Level level, final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final String message) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object... params) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Message message) {}

    @Override
    public void log(final Level level, final Message message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final MessageSupplier messageSupplier) {}

    @Override
    public void log(final Level level, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void log(final Level level, final CharSequence message) {}

    @Override
    public void log(final Level level, final CharSequence message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Object message) {}

    @Override
    public void log(final Level level, final Object message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final String message) {}

    @Override
    public void log(final Level level, final String message, final Object... params) {}

    @Override
    public void log(final Level level, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void log(final Level level, final String message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Supplier<?> messageSupplier) {}

    @Override
    public void log(final Level level, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void log(final Level level, final String message, final Object p0) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void printf(final Level level, final Marker marker, final String format, final Object... params) {}

    @Override
    public void printf(final Level level, final String format, final Object... params) {}

    @Override
    public <T extends Throwable> T throwing(final Level level, final T throwable) {
        return null;
    }

    @Override
    public <T extends Throwable> T throwing(final T throwable) {
        return null;
    }

    @Override
    public void trace(final Marker marker, final Message message) {}

    @Override
    public void trace(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void trace(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void trace(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void trace(final Marker marker, final CharSequence message) {}

    @Override
    public void trace(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void trace(final Marker marker, final Object message) {}

    @Override
    public void trace(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void trace(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void trace(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void trace(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void trace(final Message message) {}

    @Override
    public void trace(final Message message, final Throwable throwable) {}

    @Override
    public void trace(final MessageSupplier messageSupplier) {}

    @Override
    public void trace(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void trace(final CharSequence message) {}

    @Override
    public void trace(final CharSequence message, final Throwable throwable) {}

    @Override
    public void trace(final Object message) {}

    @Override
    public void trace(final Object message, final Throwable throwable) {}

    @Override
    public void trace(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void trace(final Supplier<?> messageSupplier) {}

    @Override
    public void trace(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

    @Override
    public EntryMessage traceEntry() {
        return null;
    }

    @Override
    public EntryMessage traceEntry(final String format, final Object... params) {
        return null;
    }

    @Override
    public EntryMessage traceEntry(final Supplier<?>... paramSuppliers) {
        return null;
    }

    @Override
    public EntryMessage traceEntry(final String format, final Supplier<?>... paramSuppliers) {
        return null;
    }

    @Override
    public EntryMessage traceEntry(final Message message) {
        return null;
    }

    @Override
    public void traceExit() {}

    @Override
    public <R> R traceExit(final R result) {
        return null;
    }

    @Override
    public <R> R traceExit(final String format, final R result) {
        return null;
    }

    @Override
    public void traceExit(final EntryMessage message) {}

    @Override
    public <R> R traceExit(final EntryMessage message, final R result) {
        return null;
    }

    @Override
    public <R> R traceExit(final Message message, final R result) {
        return null;
    }

    @Override
    public void warn(final Marker marker, final Message message) {}

    @Override
    public void warn(final Marker marker, final Message message, final Throwable throwable) {}

    @Override
    public void warn(final Marker marker, final MessageSupplier messageSupplier) {}

    @Override
    public void warn(final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void warn(final Marker marker, final CharSequence message) {}

    @Override
    public void warn(final Marker marker, final CharSequence message, final Throwable throwable) {}

    @Override
    public void warn(final Marker marker, final Object message) {}

    @Override
    public void warn(final Marker marker, final Object message, final Throwable throwable) {}

    @Override
    public void warn(final Marker marker, final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void warn(final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void warn(final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void warn(final Message message) {}

    @Override
    public void warn(final Message message, final Throwable throwable) {}

    @Override
    public void warn(final MessageSupplier messageSupplier) {}

    @Override
    public void warn(final MessageSupplier messageSupplier, final Throwable throwable) {}

    @Override
    public void warn(final CharSequence message) {}

    @Override
    public void warn(final CharSequence message, final Throwable throwable) {}

    @Override
    public void warn(final Object message) {}

    @Override
    public void warn(final Object message, final Throwable throwable) {}

    @Override
    public void warn(final String message, final Supplier<?>... paramSuppliers) {}

    @Override
    public void warn(final Supplier<?> messageSupplier) {}

    @Override
    public void warn(final Supplier<?> messageSupplier, final Throwable throwable) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9) {}

}
