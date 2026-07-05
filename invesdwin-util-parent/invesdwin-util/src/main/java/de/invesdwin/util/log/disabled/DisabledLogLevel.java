package de.invesdwin.util.log.disabled;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.ILogLevel;

@Immutable
public final class DisabledLogLevel implements ILogLevel {

    public static final DisabledLogLevel INSTANCE = new DisabledLogLevel();

    private DisabledLogLevel() {}

    @Override
    public boolean isEnabled(final ILog logger) {
        return false;
    }

    @Override
    public void log(final ILog logger, final String msg) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {}

    @Override
    public void log(final ILog logger, final String format, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {}

    @Override
    public void log(final ILog logger, final String format, final Object... params) {}

}
