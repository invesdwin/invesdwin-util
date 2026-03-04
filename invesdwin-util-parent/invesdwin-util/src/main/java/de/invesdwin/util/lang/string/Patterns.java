package de.invesdwin.util.lang.string;

import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Patterns {

    private Patterns() {}

    public static Pattern tryCompile(final String regex) {
        try {
            return Pattern.compile(regex);
        } catch (final Exception e) {
            return Pattern.compile("\\Q" + regex + "\\E");
        }
    }

}
