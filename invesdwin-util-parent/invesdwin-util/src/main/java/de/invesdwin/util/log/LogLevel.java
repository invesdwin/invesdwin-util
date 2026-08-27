package de.invesdwin.util.log;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum LogLevel implements ILogLevel {
    TRACE {
        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isTraceEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.trace(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.trace(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.trace(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.trace(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.trace(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.trace(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.trace(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.trace(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.trace(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.trace(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.trace(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.trace(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.TRACE;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.TRACE;
        }

    },
    DEBUG {
        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isDebugEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.debug(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.debug(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.debug(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.debug(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.debug(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.debug(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.debug(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.debug(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.debug(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.debug(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.debug(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.debug(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.DEBUG;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.DEBUG;
        }

    },
    INFO {

        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isInfoEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.info(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.info(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.info(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.info(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.info(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.info(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.info(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.info(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.info(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.info(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.info(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.info(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.INFO;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.INFO;
        }

    },
    WARN {

        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isWarnEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.warn(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.warn(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.warn(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.warn(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.warn(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.warn(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.warn(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.warn(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.warn(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.warn(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.warn(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.warn(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.WARN;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.WARN;
        }

    },
    ERROR {
        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isErrorEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.error(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.error(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.error(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.error(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.error(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.error(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.error(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.error(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.error(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.error(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.error(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.ERROR;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.ERROR;
        }

    },
    FATAL {
        @Override
        public boolean isEnabled(final ILog logger) {
            return logger.isFatalEnabled();
        }

        @Override
        public void log(final ILog logger, final String msg) {
            logger.fatal(msg);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0) {
            logger.fatal(format, p0);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1) {
            logger.fatal(format, p0, p1);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {
            logger.fatal(format, p0, p1, p2);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3) {
            logger.fatal(format, p0, p1, p2, p3);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4) {
            logger.fatal(format, p0, p1, p2, p3, p4);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5) {
            logger.fatal(format, p0, p1, p2, p3, p4, p5);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6) {
            logger.fatal(format, p0, p1, p2, p3, p4, p5, p6);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
            logger.fatal(format, p0, p1, p2, p3, p4, p5, p6, p7);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
            logger.fatal(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
        }

        @Override
        public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
                final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
                final Object p9) {
            logger.fatal(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }

        @Override
        public void log(final ILog logger, final String format, final Object... params) {
            logger.fatal(format, params);
        }

        @Override
        public org.apache.logging.log4j.Level asLog4j2Level() {
            return org.apache.logging.log4j.Level.FATAL;
        }

        @Override
        public org.slf4j.ext.XLogger.Level asSlf4jLevel() {
            return org.slf4j.ext.XLogger.Level.ERROR;
        }

    };

    public abstract org.apache.logging.log4j.Level asLog4j2Level();

    public abstract org.slf4j.ext.XLogger.Level asSlf4jLevel();

}
