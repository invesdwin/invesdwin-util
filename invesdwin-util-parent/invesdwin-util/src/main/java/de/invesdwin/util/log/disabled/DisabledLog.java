package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

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
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void trace(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void trace(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

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
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void debug(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void debug(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

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
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void info(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void info(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

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
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void warn(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void warn(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

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
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void error(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void error(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public void fatal(final String msg) {}

    @Override
    public void fatal(final String format, final Object arg) {}

    @Override
    public void fatal(final String format, final Object arg1, final Object arg2) {}

    @Override
    public void fatal(final String format, final Object... arguments) {}

    @Override
    public void fatal(final String msg, final Throwable t) {}

    @Override
    public boolean isFatalEnabled(final Marker marker) {
        return false;
    }

    @Override
    public void fatal(final Marker marker, final String msg) {}

    @Override
    public void fatal(final Marker marker, final String format, final Object arg) {}

    @Override
    public void fatal(final Marker marker, final String format, final Object arg1, final Object arg2) {}

    @Override
    public void fatal(final Marker marker, final String format, final Object... arguments) {}

    @Override
    public void fatal(final Marker marker, final String msg, final Throwable t) {}

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
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void fatal(final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void fatal(final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    /////////////////////////////

    @Override
    public void catching(final Level level, final Throwable throwable) {}

    @Override
    public void catching(final Throwable throwable) {}

    @Override
    public void entry() {}

    @Override
    public void entry(final Object... params) {}

    @Override
    public void exit() {}

    @Override
    public <R> R exit(final R result) {
        return null;
    }

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
    public void log(final Level level, final Marker marker, final MessageSupplier messageSupplier,
            final Throwable throwable) {}

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
    public void log(final Level level, final Marker marker, final String message,
            final Supplier<?>... paramSuppliers) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Throwable throwable) {}

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier) {}

    @Override
    public void log(final Level level, final Marker marker, final Supplier<?> messageSupplier,
            final Throwable throwable) {}

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
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8) {}

    @Override
    public void log(final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9) {}

    @Override
    public void log(final Level level, final String message, final Object p0) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void log(final Level level, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

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

}
