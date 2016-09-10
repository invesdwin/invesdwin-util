package de.invesdwin.util.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.norva.beanpath.BeanPathStrings;
import de.invesdwin.util.lang.internal.AStringsStaticFacade;
import de.invesdwin.util.lang.internal.DefaultToStringStyle;
import de.invesdwin.util.lang.internal.MultilineToStringStyle;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.lang.internal.AStringsStaticFacade", targets = {
        BeanPathStrings.class })
public final class Strings extends AStringsStaticFacade {

    public static final ADelegateComparator<String> COMPARATOR = new ADelegateComparator<String>() {
        @Override
        protected Comparable<?> getCompareCriteria(final String e) {
            return e;
        }
    };

    public static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;

    private static final Map<String, String> SYMBOL_ESCAPEDHTML = new HashMap<String, String>();

    static {
        SYMBOL_ESCAPEDHTML.put("ä", "&auml;");
        SYMBOL_ESCAPEDHTML.put("Ä", "&Auml;");
        SYMBOL_ESCAPEDHTML.put("ö", "&ouml;");
        SYMBOL_ESCAPEDHTML.put("Ö", "&Ouml;");
        SYMBOL_ESCAPEDHTML.put("ü", "&uuml;");
        SYMBOL_ESCAPEDHTML.put("Ü", "&Uuml;");
        SYMBOL_ESCAPEDHTML.put("ß", "&szlig;");
        SYMBOL_ESCAPEDHTML.put("€", "&euro;");
        SYMBOL_ESCAPEDHTML.put("©", "&copy;");
        SYMBOL_ESCAPEDHTML.put("•", "&bull;");
        SYMBOL_ESCAPEDHTML.put("™", "&trade;");
        SYMBOL_ESCAPEDHTML.put("®", "&reg;");
        SYMBOL_ESCAPEDHTML.put("§", "&sect;");
    }

    private Strings() {}

    public static String escapeHtmlSymbolsWithoutDestroyingMarkup(final String str) {
        String escapedValue = str;
        if (escapedValue != null) {
            for (final Entry<String, String> entry : SYMBOL_ESCAPEDHTML.entrySet()) {
                escapedValue = escapedValue.replace(entry.getKey(), entry.getValue());
            }
        }
        return escapedValue;
    }

    public static String removeLeading(final String s, final String leading) {
        String removed = s;
        while (removed.startsWith(leading)) {
            removed = removeStart(removed, leading.length());
        }
        return removed;
    }

    public static String removeTrailing(final String s, final String trailing) {
        String removed = s;
        while (removed.endsWith(trailing)) {
            removed = removeEnd(removed, trailing.length());
        }
        return removed;
    }

    public static String removeStart(final String s, final int countCharacters) {
        return s.substring(countCharacters, s.length());
    }

    public static String removeAnyEnd(final String s, final String... ends) {
        for (final String end : ends) {
            if (s.endsWith(end)) {
                return removeEnd(s, end);
            }
        }
        return s;
    }

    public static boolean isEmpty(final StringBuilder s) {
        return s == null || s.length() == 0;
    }

    /**
     * Removes all non ASCII characters from the string.
     * 
     * @see <a href=
     *      "http://stackoverflow.com/questions/123336/how-can-you-strip-non-ascii-characters-from-a-string-in-c">
     *      stackoverflow.com</a>
     */
    public static String stripNonAscii(final String s) {
        if (Strings.isBlank(s)) {
            return s;
        } else {
            return s.replaceAll("[^\u0000-\u007F]", "");
        }
    }

    /**
     * 
     * @see <a href=
     *      "http://stackoverflow.com/questions/1176904/php-how-to-remove-all-non-printable-characters-in-a-string">
     *      stackoverflow.com</a>
     */
    public static String stripNonPrintableAscii(final String s) {
        if (Strings.isBlank(s)) {
            return s;
        } else {
            return s.replaceAll("[^\\x0A\\x20-\\x7E]", "");
        }
    }

    public static String stripNonAlphanumeric(final String s) {
        if (Strings.isEmpty(s)) {
            return s;
        } else {
            return s.replaceAll("[^A-Za-z0-9]", "");
        }
    }

    public static String asString(final List<String> list, final String delimiter) {
        if (list == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final String e : list) {
            sb.append(e);
            sb.append(delimiter);
        }
        Strings.removeEnd(sb, delimiter);
        return sb.toString();
    }

    /**
     * Calls o.toString(). Returns &lt;null&gt; if o is null.
     */
    public static String asStringNullText(final Object o) {
        if (o == null) {
            return DefaultToStringStyle.INSTANCE.getNullText();
        } else {
            return o.toString();
        }
    }

    public static String asStringEmptyText(final Object o) {
        if (o == null) {
            return "";
        } else {
            return o.toString();
        }
    }

    /**
     * Calls ReflectionToStringBuilder.toString() with DefaultToStringStyle.
     */
    public static String asStringReflective(final Object o) {
        return ReflectionToStringBuilder.toString(o, DefaultToStringStyle.INSTANCE);
    }

    /**
     * Calls ReflectionToStringBuilder.toString() with MultilineToStringStyle.
     */
    public static String asStringReflectiveMultiline(final Object o) {
        return ReflectionToStringBuilder.toString(o, MultilineToStringStyle.INSTANCE);
    }

    public static String asStringIdentity(final Object o) {
        if (o == null) {
            return null;
        } else {
            return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
        }
    }

    public static String stripBlankLines(final String s) {
        return s.replaceAll("(?m)^\\s*", "").trim();
    }

    public static int countLines(final String s) {
        //There always exists 1 line, thus +1
        return countMatches(s, "\n") + 1;
    }

    /**
     * Counts characters in the longest line available in the string.
     */
    public static int countMaxLineLength(final String s) {
        if (s == null) {
            return 0;
        }
        final String[] lines = Strings.split(s, "\n");
        int maxLength = 0;
        for (int i = 0; i < lines.length; i++) {
            maxLength = Math.max(maxLength, lines[i].length());
        }
        return maxLength;
    }

    public static String extractLongestLine(final String s) {
        if (s == null) {
            return null;
        }
        final String[] lines = Strings.split(s, "\n");
        String longestLine = "";
        for (int i = 0; i < lines.length; i++) {
            if (longestLine.length() < lines[i].length()) {
                longestLine = lines[i];
            }
        }
        return longestLine;
    }

    public static String rightPad(final Object obj, final int size) {
        return rightPad(asString(obj), size);
    }

    public static String rightPad(final Object obj, final int size, final char padChar) {
        return rightPad(asString(obj), size, padChar);
    }

    public static String rightPad(final Object obj, final int size, final String padStr) {
        return rightPad(asString(obj), size, padStr);
    }

    public static String leftPad(final Object obj, final int size) {
        return leftPad(asString(obj), size);
    }

    public static String leftPad(final Object obj, final int size, final char padChar) {
        return leftPad(asString(obj), size, padChar);
    }

    public static String leftPad(final Object obj, final int size, final String padStr) {
        return leftPad(asString(obj), size, padStr);
    }

    /**
     * @see <a href="http://code.activestate.com/recipes/435882-normalizing-newlines-between-windowsunixmacs/">Source
     *      </a>
     */
    public static String normalizeNewlines(final String s) {
        return s.replace("\r\n", "\n").replace("\r", "\n");
    }

    public static String replaceRange(final String s, final int start, final int end, final String replaceStr) {
        final String anfang = s.substring(0, start) + replaceStr;
        if (end + 1 > s.length() - 1) {
            return anfang;
        } else {
            return anfang + s.substring(end + 1);
        }
    }

    public static String removeRange(final String s, final int start, final int end) {
        return replaceRange(s, start, end, "");
    }

    public static String replaceNewlines(final String s, final String newlineReplacement) {
        final String normalized = normalizeNewlines(s);
        return normalized.replace("\n", newlineReplacement);
    }

    public static boolean matchesAny(final String string, final String... searchRegexes) {
        for (final String regex : searchRegexes) {
            if (string.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAny(final String string, final String... searchStrings) {
        if (string == null) {
            return false;
        }
        for (final String search : searchStrings) {
            if (string.contains(search)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAnyIgnoreCase(final String string, final String... searchStrings) {
        if (string == null) {
            return false;
        }
        for (final String search : searchStrings) {
            if (containsIgnoreCase(string, search)) {
                return true;
            }
        }
        return false;
    }

    /**
     * "a/b/c.txt" with suffix "_1" results in "a/b/c_1.txt"
     */
    public static String addSuffixToFileName(final String fileName, final String suffix) {
        if (fileName.contains(".")) {
            return Strings.substringBeforeLast(fileName, ".") + suffix + "."
                    + Strings.substringAfterLast(fileName, ".");
        } else {
            return fileName + suffix;
        }
    }

}
