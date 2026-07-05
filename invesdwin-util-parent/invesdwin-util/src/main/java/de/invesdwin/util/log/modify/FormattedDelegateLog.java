package de.invesdwin.util.log.modify;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;

@Immutable
public class FormattedDelegateLog extends AModifiedDelegateLog {

    public FormattedDelegateLog(final ILog delegate) {
        super(delegate);
    }

    @Override
    protected String modify(final LogLevel level, final String msg) {
        return msg;
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0) {
        return format(format, p0);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1) {
        return format(format, p0, p1);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object... params) {
        return format(format, params);
    }

    public static String format(final String messagePattern, final Object p0) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1 });
    }

    public static String format(final String messagePattern, final Object[] params) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                params);
    }

}
