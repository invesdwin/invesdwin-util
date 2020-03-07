package de.invesdwin.util.swing.text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Strings;

/**
 * https://stackoverflow.com/questions/868651/multi-line-tooltips-in-java?rq=1
 */
@Immutable
public final class ToolTipFormatter extends AValueObject {

    private static final String[] DEFAULT_LINE_BREAKS = new String[] { "<br>", "<li>", "<p>", "<pre>" };
    private static final int DEFAULT_MAX_LENGTH = 200;

    private int maxLength = DEFAULT_MAX_LENGTH;
    private String[] lineBreaks = DEFAULT_LINE_BREAKS.clone();

    public String[] getLineBreaks() {
        return lineBreaks;
    }

    /**
     * lineBreaks[0] will be used as the inserted line break
     */
    public ToolTipFormatter withLineBreaks(final String[] lineBreaks) {
        this.lineBreaks = lineBreaks;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public ToolTipFormatter withMaxLength(final int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String format(final String str) {
        if (maxLength < 1 || str.length() <= maxLength || lineBreaks == null || lineBreaks.length == 0) {
            return str;
        }

        final List<String> parts = new ArrayList<>();
        int stringPosition = 0;

        while (stringPosition < str.length()) {
            if (stringPosition + maxLength < str.length()) {
                final String tipSubstring = str.substring(stringPosition, stringPosition + maxLength);
                final int lastSpace = findSpace(tipSubstring);
                if (lastSpace == -1 || lastSpace == 0) {
                    parts.add(str.substring(stringPosition, stringPosition + maxLength));
                    stringPosition += maxLength;
                } else {
                    parts.add(str.substring(stringPosition, stringPosition + lastSpace));
                    stringPosition += lastSpace;
                }
            } else {
                parts.add(str.substring(stringPosition));
                break;
            }
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            final String part = parts.get(i);
            appendPart(sb, part);
        }
        return sb.toString();
    }

    private void appendPart(final StringBuilder sb, final String part) {
        if (part.length() > 0) {
            if (sb.length() > 0) {
                if (!Strings.startsWithAnyIgnoreCase(part, lineBreaks)
                        && !Strings.endsWithAnyIgnoreCase(sb, lineBreaks)) {
                    sb.append(lineBreaks[0]);
                }
            }
            sb.append(part);
        }
    }

    private int findSpace(final String tipSubstring) {
        int lastSpace = Strings.lastIndexOfAnyIgnoreCase(tipSubstring, lineBreaks);
        if (lastSpace == -1) {
            lastSpace = tipSubstring.lastIndexOf(' ');
        }
        return lastSpace;
    }

    @Override
    public ToolTipFormatter clone() {
        return (ToolTipFormatter) super.clone();
    }
}