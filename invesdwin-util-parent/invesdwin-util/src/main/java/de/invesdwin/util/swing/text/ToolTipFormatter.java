package de.invesdwin.util.swing.text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;

/**
 * https://stackoverflow.com/questions/868651/multi-line-tooltips-in-java?rq=1
 */
@Immutable
public final class ToolTipFormatter extends AValueObject {

    private static final int DEFAULT_MAX_LENGTH = 200;
    private static final String DEFAULT_LINE_BREAK = "<br>";

    private int maxLength = DEFAULT_MAX_LENGTH;
    private String lineBreak = DEFAULT_LINE_BREAK;
    private boolean isDefaultLineBreak;

    public String getLineBreak() {
        return lineBreak;
    }

    public ToolTipFormatter withLineBreak(final String lineBreak) {
        this.lineBreak = lineBreak;
        isDefaultLineBreak = Objects.equals(lineBreak, DEFAULT_LINE_BREAK);
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
        if (maxLength < 1 || str.length() <= maxLength || Strings.isBlank(lineBreak)) {
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
                if (isDefaultLineBreak) {
                    if (!Strings.startsWithIgnoreCase(part, DEFAULT_LINE_BREAK)
                            && !Strings.endsWithIgnoreCase(sb, DEFAULT_LINE_BREAK)) {
                        sb.append(lineBreak);
                    }
                } else {
                    if (!Strings.startsWithAnyIgnoreCase(part, lineBreak, DEFAULT_LINE_BREAK)
                            && !Strings.endsWithAnyIgnoreCase(sb, lineBreak, DEFAULT_LINE_BREAK)) {
                        sb.append(lineBreak);
                    }
                }
            }
            sb.append(part);
        }
    }

    private int findSpace(final String tipSubstring) {
        int lastSpace = Strings.lastIndexOfIgnoreCase(tipSubstring, lineBreak);
        if (lastSpace == -1 && !isDefaultLineBreak) {
            lastSpace = Strings.lastIndexOfIgnoreCase(tipSubstring, DEFAULT_LINE_BREAK);
        }
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