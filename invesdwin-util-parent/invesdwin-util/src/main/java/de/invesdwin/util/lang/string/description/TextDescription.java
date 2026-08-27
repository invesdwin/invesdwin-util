package de.invesdwin.util.lang.string.description;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.assertj.core.description.Description;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter;

@NotThreadSafe
public class TextDescription extends Description implements Message, StringBuilderFormattable {

    private final String messagePattern;
    private final transient Object[] params;
    private String formattedMessage;

    public TextDescription(final String messagePattern, final Object... params) {
        this.messagePattern = messagePattern;
        this.params = params;
    }

    @Override
    public void formatTo(final StringBuilder buffer) {
        TextDescriptionFormatter.formatTo(buffer, messagePattern, params);
    }

    @Override
    public String getFormattedMessage() {
        if (formattedMessage == null) {
            formattedMessage = TextDescriptionFormatter.format(messagePattern, params);
        }
        return formattedMessage;
    }

    @Override
    public String value() {
        return getFormattedMessage();
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }

    @Override
    public String getFormat() {
        return messagePattern;
    }

    @Override
    public Object[] getParameters() {
        return params;
    }

    @Override
    public Throwable getThrowable() {
        // Optional: Extract throwable from argArray if it's the last element, similar to SLF4J
        if (params != null && params.length > 0 && params[params.length - 1] instanceof Throwable) {
            return (Throwable) params[params.length - 1];
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), messagePattern, params);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TextDescription other = (TextDescription) obj;
        return Objects.equals(messagePattern, other.messagePattern) && Objects.equals(params, other.params);
    }

    /**
     * WARNING: Did you forget to add args?
     */
    @Deprecated
    public static String format(final String messagePattern) {
        return messagePattern;
    }

    public static String format(final String messagePattern, final Object p0) {
        return TextDescriptionFormatter.format(messagePattern, p0);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4, p5);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4, p5, p6);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public static String format(final String messagePattern, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9) {
        return TextDescriptionFormatter.format(messagePattern, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    public static String format(final String messagePattern, final Object... params) {
        return TextDescriptionFormatter.format(messagePattern, params);
    }

}