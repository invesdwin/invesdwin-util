package de.invesdwin.util.log;

import org.slf4j.Marker;

public interface ILog extends org.slf4j.Logger {

    default void log(final LogLevel level, final String msg) {
        level.log(this, msg);
    }

    default void log(final LogLevel level, final String format, final Object arg) {
        level.log(this, format, arg);
    }

    default void log(final LogLevel level, final String format, final Object arg1, final Object arg2) {
        level.log(this, format, arg1, arg2);
    }

    default void log(final LogLevel level, final String format, final Object... args) {
        level.log(this, format, args);
    }

    default void log(final LogLevel level, final String msg, final Throwable t) {
        level.log(this, msg, t);
    }

    default void log(final LogLevel level, final Marker marker, final String msg) {
        level.log(this, marker, msg);
    }

    default void log(final LogLevel level, final Marker marker, final String format, final Object arg) {
        level.log(this, marker, format, arg);
    }

    default void log(final LogLevel level, final Marker marker, final String format, final Object arg1,
            final Object arg2) {
        level.log(this, marker, format, arg1, arg2);
    }

    default void log(final LogLevel level, final Marker marker, final String format, final Object... args) {
        level.log(this, marker, format, args);
    }

    default void log(final LogLevel level, final Marker marker, final String msg, final Throwable t) {
        level.log(this, marker, msg, t);
    }

}
