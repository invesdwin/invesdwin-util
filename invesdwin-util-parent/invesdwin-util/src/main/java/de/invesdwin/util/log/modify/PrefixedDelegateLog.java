package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;

@Immutable
public class PrefixedDelegateLog extends AModifiedDelegateLog {

    private final String prefix;

    public PrefixedDelegateLog(final ILog delegate, final String prefix) {
        super(delegate);
        this.prefix = prefix;
    }

    @Override
    protected String modify(final LogLevel level, final String msg) {
        return prefix(msg);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9) {
        return prefix(format);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object... params) {
        return prefix(format);
    }

    private String prefix(final String msg) {
        return prefix + msg;
    }

}
