package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

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
    public void catching(final Throwable throwable) {}

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(final String msg) {}

    @Override
    public void trace(final String format, final Object p0) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void trace(final String format, final Object... params) {}

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(final String msg) {}

    @Override
    public void debug(final String format, final Object p0) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void debug(final String format, final Object... params) {}

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(final String msg) {}

    @Override
    public void info(final String format, final Object p0) {}

    @Override
    public void info(final String format, final Object p0, final Object p1) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void info(final String format, final Object... params) {}

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(final String msg) {}

    @Override
    public void warn(final String format, final Object p0) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void warn(final String format, final Object... params) {}

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(final String msg) {}

    @Override
    public void error(final String format, final Object p0) {}

    @Override
    public void error(final String format, final Object p0, final Object p1) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void error(final String format, final Object... params) {}

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public void fatal(final String msg) {}

    @Override
    public void fatal(final String format, final Object p0) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {}

    @Override
    public void fatal(final String format, final Object... params) {}

}
