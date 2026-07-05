package de.invesdwin.util.log;

public interface ILog {

    String getName();

    void catching(Throwable throwable);

    default boolean isEnabled(final LogLevel level) {
        return level.isEnabled(this);
    }

    default void log(final LogLevel level, final String msg) {
        level.log(this, msg);
    }

    default void log(final LogLevel level, final String format, final Object p0) {
        level.log(this, format, p0);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1) {
        level.log(this, format, p0, p1);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2) {
        level.log(this, format, p0, p1, p2);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        level.log(this, format, p0, p1, p2, p3);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        level.log(this, format, p0, p1, p2, p3, p4);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        level.log(this, format, p0, p1, p2, p3, p4, p5);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        level.log(this, format, p0, p1, p2, p3, p4, p5, p6);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        level.log(this, format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        level.log(this, format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    default void log(final LogLevel level, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {
        level.log(this, format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    default void log(final LogLevel level, final String format, final Object... params) {
        level.log(this, format, params);
    }

    ///////////////// TRACE

    boolean isTraceEnabled();

    void trace(String msg);

    void trace(String format, Object p0);

    void trace(String format, Object p0, Object p1);

    void trace(String format, Object p0, Object p1, Object p2);

    void trace(String format, Object p0, Object p1, Object p2, Object p3);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void trace(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void trace(String format, Object... params);

    ///////////////// DEBUG

    boolean isDebugEnabled();

    void debug(String msg);

    void debug(String format, Object p0);

    void debug(String format, Object p0, Object p1);

    void debug(String format, Object p0, Object p1, Object p2);

    void debug(String format, Object p0, Object p1, Object p2, Object p3);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void debug(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void debug(String format, Object... params);

    ///////////////// INFO

    boolean isInfoEnabled();

    void info(String msg);

    void info(String format, Object p0);

    void info(String format, Object p0, Object p1);

    void info(String format, Object p0, Object p1, Object p2);

    void info(String format, Object p0, Object p1, Object p2, Object p3);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void info(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void info(String format, Object... params);

    ///////////////// WARN

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String format, Object p0);

    void warn(String format, Object p0, Object p1);

    void warn(String format, Object p0, Object p1, Object p2);

    void warn(String format, Object p0, Object p1, Object p2, Object p3);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void warn(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void warn(String format, Object... params);

    ///////////////// ERROR

    boolean isErrorEnabled();

    void error(String msg);

    void error(String format, Object p0);

    void error(String format, Object p0, Object p1);

    void error(String format, Object p0, Object p1, Object p2);

    void error(String format, Object p0, Object p1, Object p2, Object p3);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void error(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void error(String format, Object... params);

    ///////////////// FATAL

    boolean isFatalEnabled();

    void fatal(String msg);

    void fatal(String format, Object p0);

    void fatal(String format, Object p0, Object p1);

    void fatal(String format, Object p0, Object p1, Object p2);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8);

    void fatal(String format, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7,
            Object p8, Object p9);

    void fatal(String format, Object... params);

}
