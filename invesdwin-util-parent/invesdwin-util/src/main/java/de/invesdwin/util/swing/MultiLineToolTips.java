package de.invesdwin.util.swing;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * https://stackoverflow.com/questions/868651/multi-line-tooltips-in-java?rq=1
 */
@Immutable
public final class MultiLineToolTips {

    /** Default max length of the tool tip when split with {@link #splitToolTip(String)}. */
    private static final int DEFAULT_TOOLTIP_MAX_SIZE = 200;

    /** Private constructor for utility class. */
    private MultiLineToolTips() {}

    /**
     * A function that splits a string into sections of {@value #DEFAULT_TOOLTIP_MAX_SIZE} characters or less. If you
     * want the lines to be shorter or longer call {@link #splitToolTip(String, int)}.
     * 
     * @param toolTip
     *            The tool tip string to be split
     * @return the tool tip string with HTML formatting to break it into sections of the correct length
     */
    public static String splitToolTipText(final String toolTip) {
        return splitToolTipText(toolTip, DEFAULT_TOOLTIP_MAX_SIZE);
    }

    /**
     * An overloaded function that splits a tool tip string into sections of a specified length.
     * 
     * @param toolTip
     *            The tool tip string to be split
     * @param desiredLength
     *            The maximum length of the tool tip per line
     * @return The tool tip string with HTML formatting to break it into sections of the correct length
     */
    public static String splitToolTipText(final String toolTip, final int desiredLength) {
        if (toolTip.length() <= desiredLength) {
            return toolTip;
        }

        final List<String> parts = new ArrayList<>();
        int stringPosition = 0;

        while (stringPosition < toolTip.length()) {
            if (stringPosition + desiredLength < toolTip.length()) {
                final String tipSubstring = toolTip.substring(stringPosition, stringPosition + desiredLength);
                final int lastSpace = tipSubstring.lastIndexOf(' ');
                if (lastSpace == -1 || lastSpace == 0) {
                    parts.add(toolTip.substring(stringPosition, stringPosition + desiredLength));
                    stringPosition += desiredLength;
                } else {
                    parts.add(toolTip.substring(stringPosition, stringPosition + lastSpace));
                    stringPosition += lastSpace;
                }
            } else {
                parts.add(toolTip.substring(stringPosition));
                break;
            }
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size() - 1; i++) {
            sb.append(parts.get(i) + "<br>");
        }
        sb.append(parts.get(parts.size() - 1));
        return sb.toString();
    }
}