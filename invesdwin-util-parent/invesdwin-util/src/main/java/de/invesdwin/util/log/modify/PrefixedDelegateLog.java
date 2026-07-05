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
    protected String modify(final LogLevel level, final String format, final Object... params) {
        return prefix(format);
    }

    private String prefix(final String msg) {
        return prefix + msg;
    }

}
