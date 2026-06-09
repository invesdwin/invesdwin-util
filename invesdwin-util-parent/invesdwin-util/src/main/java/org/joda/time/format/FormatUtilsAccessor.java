package org.joda.time.format;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class FormatUtilsAccessor {

    private FormatUtilsAccessor() {}

    public static String createErrorMessage(final String text, final int errorPos) {
        return FormatUtils.createErrorMessage(text, errorPos);
    }

}
