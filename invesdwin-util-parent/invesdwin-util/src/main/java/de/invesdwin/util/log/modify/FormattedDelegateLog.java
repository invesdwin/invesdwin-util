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
    protected String modify(final LogLevel level, final String msg, final Throwable t) {
        return msg;
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object arg) {
        return format(format, arg);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object arg1, final Object arg2) {
        return format(format, arg1, arg2);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object... arguments) {
        return format(format, arguments);
    }

    public static String format(final String messagePattern, final Object arg) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { arg });
    }

    public static String format(final String messagePattern, final Object arg1, final Object arg2) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { arg1, arg2 });
    }

    public static String format(final String messagePattern, final Object[] argArray) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                argArray);
    }

}
