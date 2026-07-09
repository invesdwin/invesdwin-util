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
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2) {
        return format(format, p0, p1, p2);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3) {
        return format(format, p0, p1, p2, p3);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4) {
        return format(format, p0, p1, p2, p3, p4);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5) {
        return format(format, p0, p1, p2, p3, p4, p5);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return format(format, p0, p1, p2, p3, p4, p5, p6);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return format(format, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8) {
        return format(format, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Override
    protected String modify(final LogLevel level, final String format, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9) {
        return format(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
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

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4, p5 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4, p5, p6 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4, p5, p6, p7 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8 });
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 });
    }

    public static String format(final String messagePattern, final Object[] params) {
        return de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter.format(messagePattern,
                params);
    }

}
