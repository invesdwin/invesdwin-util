package de.invesdwin.util.lang.string;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Locales {

    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale SPANISH = new Locale("es", "ES");

    private Locales() {
    }

    public static boolean isSameLanguage(final Locale locale1, final Locale locale2) {
        return isSameLanguage(locale1, locale2.getLanguage());
    }

    public static boolean isSameLanguage(final Locale locale1, final String languageCode2) {
        return isSameLanguage(locale1.getLanguage(), languageCode2);
    }

    public static boolean isSameLanguage(final String languageCode1, final String languageCode2) {
        return Strings.equalsIgnoreCase(languageCode1, languageCode2);
    }

}
