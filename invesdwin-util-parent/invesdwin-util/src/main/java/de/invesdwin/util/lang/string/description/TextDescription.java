package de.invesdwin.util.lang.string.description;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.description.Description;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.string.description.internal.TextDescriptionFormatter;

@NotThreadSafe
public class TextDescription extends Description {
    private final String value;
    private final Object[] args;
    private String formattedMessage;

    public TextDescription(final String value, final Object... args) {
        this.value = value;
        this.args = args;
    }

    @Override
    public String value() {
        if (formattedMessage == null) {
            formattedMessage = TextDescription.format(value, args);
        }
        return formattedMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getClass(), value, args);
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
        return Objects.equals(value, other.value) && Objects.equals(args, other.args);
    }

    /**
     * WARNING: Did you forget to add args?
     */
    @Deprecated
    public static String format(final String messagePattern) {
        return messagePattern;
    }

    public static String format(final String messagePattern, final Object arg) {
        return TextDescriptionFormatter.format(messagePattern, arg);
    }

    public static String format(final String messagePattern, final Object arg1, final Object arg2) {
        return TextDescriptionFormatter.format(messagePattern, arg1, arg2);
    }

    public static String format(final String messagePattern, final Object... args) {
        return TextDescriptionFormatter.format(messagePattern, args);
    }
}
