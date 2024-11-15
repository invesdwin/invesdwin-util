package de.invesdwin.util.log;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Marker;

@Immutable
public enum LogLevel implements ILogLevel {
    TRACE {
        @Override
        public boolean isEnabled(final org.slf4j.Logger logger) {
            return logger.isTraceEnabled();
        }

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg) {
            logger.trace(msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg) {
            logger.trace(format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {
            logger.trace(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object... args) {
            logger.trace(format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {
            logger.trace(msg, t);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {
            logger.trace(marker, msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {
            logger.trace(marker, format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
                final Object arg2) {
            logger.trace(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {
            logger.trace(marker, format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {
            logger.trace(marker, msg, t);
        }

    },
    DEBUG {
        @Override
        public boolean isEnabled(final org.slf4j.Logger logger) {
            return logger.isDebugEnabled();
        }

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg) {
            logger.debug(msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg) {
            logger.debug(format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {
            logger.debug(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object... args) {
            logger.debug(format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {
            logger.debug(msg, t);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {
            logger.debug(marker, msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {
            logger.debug(marker, format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
                final Object arg2) {
            logger.debug(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {
            logger.debug(marker, format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {
            logger.debug(marker, msg, t);
        }
    },
    INFO {

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger) {
            return logger.isInfoEnabled();
        }

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
            return logger.isInfoEnabled(marker);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg) {
            logger.info(msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg) {
            logger.info(format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {
            logger.info(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object... args) {
            logger.info(format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {
            logger.info(msg, t);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {
            logger.info(marker, msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {
            logger.info(marker, format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
                final Object arg2) {
            logger.info(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {
            logger.info(marker, format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {
            logger.info(marker, msg, t);
        }
    },
    WARN {

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger) {
            return logger.isWarnEnabled();
        }

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg) {
            logger.warn(msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg) {
            logger.warn(format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {
            logger.warn(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object... args) {
            logger.warn(format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {
            logger.warn(marker, msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {
            logger.warn(marker, format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
                final Object arg2) {
            logger.warn(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {
            logger.warn(marker, format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {
            logger.warn(marker, msg, t);
        }
    },
    ERROR {
        @Override
        public boolean isEnabled(final org.slf4j.Logger logger) {
            return logger.isErrorEnabled();
        }

        @Override
        public boolean isEnabled(final org.slf4j.Logger logger, final Marker marker) {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg) {
            logger.error(msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg) {
            logger.error(format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object arg1, final Object arg2) {
            logger.error(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String format, final Object... args) {
            logger.error(format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final String msg, final Throwable t) {
            logger.error(msg, t);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg) {
            logger.error(marker, msg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg) {
            logger.error(marker, format, arg);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object arg1,
                final Object arg2) {
            logger.error(format, arg1, arg2);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String format, final Object... args) {
            logger.error(marker, format, args);
        }

        @Override
        public void log(final org.slf4j.Logger logger, final Marker marker, final String msg, final Throwable t) {
            logger.error(marker, msg, t);
        }
    };

}
