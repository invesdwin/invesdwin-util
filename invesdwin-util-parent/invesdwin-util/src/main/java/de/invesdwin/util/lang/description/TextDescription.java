package de.invesdwin.util.lang.description;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.description.Description;

import de.invesdwin.util.lang.Objects;

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
            formattedMessage = TextDescriptionFormatter.format(value, args);
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

    public static String format(final String value, final Object... args) {
        return TextDescriptionFormatter.format(value, args);
    }
}
