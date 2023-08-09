package de.invesdwin.util.html;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class HtmlStringUtils {
    private HtmlStringUtils() {}

    public static void appendTrString(final StringBuilder sb, final String... columnContent) {
        sb.append("<tr>");
        for (final String content : columnContent) {
            appendTdString(sb, content);
        }
        sb.append("</tr>");
    }

    private static void appendTdString(final StringBuilder sb, final String content) {
        sb.append("<td>");
        sb.append(content);
        sb.append("</td>");
    }

    public static void appendFontSizeString(final StringBuilder sb, final String content, final int fontSize) {
        sb.append("<font size=\"" + fontSize + "\">" + content + "</font>");
    }

    public static String getBoldString(final String content) {
        return "<b>" + content + "</b>";
    }
}
