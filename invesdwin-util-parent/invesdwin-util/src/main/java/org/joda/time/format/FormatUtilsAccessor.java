package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class FormatUtilsAccessor {

    private FormatUtilsAccessor() {}

    public static String createErrorMessage(final String text, final int errorPos) {
        //        return FormatUtils.createErrorMessage(text, errorPos);
        //just implement the whole function to avoid errors when java module system is enabled
        final int sampleLen = errorPos + 32;
        final String sampleText;
        if (text.length() <= sampleLen + 3) {
            sampleText = text;
        } else {
            sampleText = text.substring(0, sampleLen).concat("...");
        }

        if (errorPos <= 0) {
            return "Invalid format: \"" + sampleText + '"';
        }

        if (errorPos >= text.length()) {
            return "Invalid format: \"" + sampleText + "\" is too short";
        }

        return "Invalid format: \"" + sampleText + "\" is malformed at \"" + sampleText.substring(errorPos) + '"';
    }

}
