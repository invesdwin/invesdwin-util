package de.invesdwin.util.lang.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.math.NumberUtils;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.norva.beanpath.BeanPathStrings;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.pool.set.ICloseableSet;
import de.invesdwin.util.collections.factory.pool.set.PooledSet;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.string.internal.AStringsStaticFacade;
import de.invesdwin.util.lang.string.internal.CheckedCastStrings;
import de.invesdwin.util.lang.string.internal.CommentRemover;
import de.invesdwin.util.lang.string.internal.DefaultToStringStyle;
import de.invesdwin.util.lang.string.internal.ExtendedReflectionToStringBuilder;
import de.invesdwin.util.lang.string.internal.MultilineToStringStyle;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.lang.string.internal.AStringsStaticFacade", targets = {
        CheckedCastStrings.class, BeanPathStrings.class, org.assertj.core.util.Strings.class,
        org.apache.commons.text.StringEscapeUtils.class, org.apache.commons.text.WordUtils.class, CommentRemover.class,
        com.google.common.base.Strings.class }, filterMethodSignatureExpressions = {
                ".* isNullOrEmpty\\(.*" }, filterSeeMethodSignatures = {
                        "com.google.common.base.Strings#repeat(java.lang.String, int)" })
public final class Strings extends AStringsStaticFacade {

    public static final IComparator<String> COMPARATOR = IComparator.getDefaultInstance();

    public static final String EMPTY = org.apache.commons.lang3.StringUtils.EMPTY;
    public static final String[] EMPTY_ARRAY = new String[0];
    public static final String[][] EMPTY_MATRIX = new String[0][];
    public static final String NULL_TEXT = "null";
    public static final String DEFAULT_MISSING_VALUE = null;

    private static final Map<String, String> SYMBOL_ESCAPEDHTML = ILockCollectionFactory.getInstance(false).newMap();

    private static final String[] AS_STRING_SPACES_SEARCH = new String[] { //
            "|", //
            "[", //
            "]", //
            "{", //
            "}", //
            "->", //
            "=>", //
            "  ", //
    };
    private static final String[] AS_STRING_SPACES_REPLACE = new String[] { //
            " | ", //
            " [ ", //
            " ]", //
            "{ ", //
            " }", //
            " -> ", //
            " => ", //
            " ", //
    };

    private static final String[] AS_STRING_SPACES_HTML_SEARCH = new String[] { //
            "|", //
            "[", //
            "]", //
            "{", //
            "}", //
            "->", //
            "=>", //
            "\n", //
            "\t", //
    };
    private static final String[] AS_STRING_SPACES_HTML_REPLACE = new String[] { //
            " | ", //
            " [ ", //
            " ]", //
            "{ ", //
            " }", //
            " -> ", //
            " => ", //
            "<br>", //
            "&nbsp;&nbsp;&nbsp;&nbsp;", //
    };

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

    public static List<String> putPrefix(final List<String> strs, final String prefix) {
        final List<String> prefixed = new ArrayList<>(strs.size());
        for (int i = 0; i < strs.size(); i++) {
            prefixed.add(putPrefix(strs.get(i), prefix));
        }
        return prefixed;
    }

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

    public static String asString(final List<?> list, final String delimiter) {
        if (list == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Object e : list) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(asStringNotNull(e));
        }
        return sb.toString();
    }

    public static String asString(final List<?> list, final char delimiter) {
        if (list == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Object e : list) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append(asStringNotNull(e));
        }
        return sb.toString();
    }

    /**
     * Calls o.toString(). Returns &lt;null&gt; if o is null.
     */
    public static String asStringNullText(final Object o) {
        if (o == null) {
            return NULL_TEXT;
        } else {
            return asStringNotNull(o);
        }
    }

    public static String asStringEmptyText(final Object o) {
        if (o == null) {
            return "";
        } else {
            return asStringNotNull(o);
        }
    }

    public static String asStringSpaces(final Object o) {
        return replaceEach(asStringNullText(o), AS_STRING_SPACES_SEARCH, AS_STRING_SPACES_REPLACE);
    }

    public static String asStringSpacesHtml(final Object o) {
        return replaceEach(asStringNullText(o).replace(" ", "&nbsp;"), AS_STRING_SPACES_HTML_SEARCH,
                AS_STRING_SPACES_HTML_REPLACE);
    }

    /**
     * Calls ExtendedReflectionToStringBuilder.toString() with DefaultToStringStyle.
     */
    public static String asStringReflective(final Object o) {
        if (o == null) {
            return null;
        } else {
            return ExtendedReflectionToStringBuilder.toString(o, DefaultToStringStyle.INSTANCE);
        }
    }

    /**
     * Calls ExtendedReflectionToStringBuilder.toString() with MultilineToStringStyle.
     */
    public static String asStringReflectiveMultiline(final Object o) {
        if (o == null) {
            return null;
        } else {
            return ExtendedReflectionToStringBuilder.toString(o, MultilineToStringStyle.INSTANCE);
        }
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
        final String[] lines = Strings.splitPreserveAllTokens(s, "\n");
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
        final String[] lines = Strings.splitPreserveAllTokens(s, "\n");
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
        if (s == null) {
            return null;
        }
        return s.replace("\r\n", "\n").replace("\r", "\n");
    }

    public static String removeDuplicateLines(final String s) {
        try (ICloseableSet<String> duplicateFilter = PooledSet.getInstance()) {
            final String[] lines = Strings.splitPreserveAllTokens(s, "\n");
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                final String line = lines[i];
                if (duplicateFilter.add(line)) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(line);
                }
            }
            return sb.toString();
        }
    }

    public static String replaceRange(final String s, final int startInclusive, final int endExclusive,
            final String replaceStr) {
        final String anfang = s.substring(0, startInclusive) + replaceStr;
        if (endExclusive > s.length() - 1) {
            return anfang;
        } else {
            return anfang + s.substring(endExclusive);
        }
    }

    public static String removeRange(final String s, final int startInclusive, final int endExclusive) {
        return replaceRange(s, startInclusive, endExclusive, "");
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

    public static String[] toArray(final List<String> vector) {
        if (vector == null) {
            return null;
        }
        return vector.toArray(EMPTY_ARRAY);
    }

    public static String[] toArrayVector(final List<String> vector) {
        return toArray(vector);
    }

    public static String[][] toArrayMatrix(final List<? extends List<String>> matrix) {
        if (matrix == null) {
            return null;
        }
        final String[][] arrayMatrix = new String[matrix.size()][];
        for (int i = 0; i < matrix.size(); i++) {
            final List<String> vector = matrix.get(i);
            arrayMatrix[i] = toArrayVector(vector);
        }
        return arrayMatrix;
    }

    public static List<String> asList(final String... vector) {
        if (vector == null) {
            return null;
        }
        return Arrays.asList(vector);
    }

    public static List<String> asListVector(final String[] vector) {
        return asList(vector);
    }

    public static List<List<String>> asListMatrix(final String[][] matrix) {
        if (matrix == null) {
            return null;
        }
        final List<List<String>> matrixAsList = new ArrayList<List<String>>(matrix.length);
        for (final String[] vector : matrix) {
            matrixAsList.add(asListVector(vector));
        }
        return matrixAsList;
    }

    public static String[][] fixInconsistentMatrixDimensions(final String[][] matrix) {
        return fixInconsistentMatrixDimensions(matrix, DEFAULT_MISSING_VALUE);
    }

    public static String[][] fixInconsistentMatrixDimensions(final String[][] matrix, final String missingValue) {
        return fixInconsistentMatrixDimensions(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static String[][] fixInconsistentMatrixDimensions(final String[][] matrix, final String missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensions(matrix, missingValue, appendMissingValues);
    }

    public static List<List<String>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends String>> matrix) {
        return fixInconsistentMatrixDimensionsAsList(matrix, DEFAULT_MISSING_VALUE);
    }

    public static List<List<String>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends String>> matrix, final String missingValue) {
        return fixInconsistentMatrixDimensionsAsList(matrix, missingValue, Objects.DEFAULT_APPEND_MISSING_VALUES);
    }

    public static List<List<String>> fixInconsistentMatrixDimensionsAsList(
            final List<? extends List<? extends String>> matrix, final String missingValue,
            final boolean appendMissingValues) {
        return Objects.fixInconsistentMatrixDimensionsAsList(matrix, missingValue, appendMissingValues);
    }

    public static String getPossibleValuesString(final Enum<?>[] values) {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                b.append(" | ");
            }
            b.append(values[i].name());
        }
        return b.toString();
    }

    public static String replaceEachIgnoreCase(final String text, final String[] searchList,
            final String[] replacementList) {
        return replaceEachIgnoreCase(text, searchList, replacementList, false, 0);
    }

    public static String replaceEachIgnoreCaseRepeatedly(final String text, final String[] searchList,
            final String[] replacementList) {
        // timeToLive should be 0 if not used or nothing to replace, else it's
        // the length of the replace array
        final int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEachIgnoreCase(text, searchList, replacementList, true, timeToLive);
    }

    //CHECKSTYLE:OFF
    private static String replaceEachIgnoreCase(final String text, final String[] searchList,
            final String[] replacementList, final boolean repeat, final int timeToLive) {
        //CHECKSTYLE:ON
        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.isEmpty() || searchList == null || searchList.length == 0 || replacementList == null
                || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException(
                    "Aborting to protect against StackOverflowError - " + "output of one loop is the input of another");
        }

        final int searchLength = searchList.length;
        final int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException(
                    "Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }

        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].isEmpty()
                    || replacementList[i] == null) {
                continue;
            }
            tempIndex = indexOfIgnoreCase(text, searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            final int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        final StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].isEmpty()
                        || replacementList[i] == null) {
                    continue;
                }
                tempIndex = indexOfIgnoreCase(text, searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        final String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEachIgnoreCase(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    public static boolean isBlankOrNullText(final String str) {
        return isBlank(str) || NULL_TEXT.equalsIgnoreCase(str.trim());
    }

    public static boolean isEmptyOrNullText(final String str) {
        return isEmpty(str) || NULL_TEXT.equalsIgnoreCase(str);
    }

    public static String removeSpace(final String value) {
        final String space = normalizeSpace(value);
        return replace(space, " ", "");
    }

    public static String substringBetweenLast(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.lastIndexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String uncapitalizeMultipleStart(final String str) {
        StringBuilder sb = null;
        boolean changed = false;
        for (int i = 0; i < str.length(); i++) {
            final char character = str.charAt(i);
            if (Character.isUpperCase(character)) {
                if (sb == null) {
                    sb = new StringBuilder(str);
                }
                sb.setCharAt(i, Character.toLowerCase(character));
                changed = true;
            } else {
                if (sb != null && i > 1 && i < str.length() - 1) {
                    //workaround for ADGRLong -> adgrLong
                    //though still uncapitalize AverageDailyGrowthRate -> averageDailyGrowthRate
                    final int prevIndex = i - 1;
                    final char prevChar = sb.charAt(prevIndex);
                    sb.setCharAt(prevIndex, Character.toUpperCase(prevChar));
                }
                break;
            }
        }
        if (changed) {
            return sb.toString();
        } else {
            return str;
        }
    }

    public static List<String> splitByMaxLength(final String str, final int maxLength) {
        return splitByMaxLength(str, maxLength, false);
    }

    public static List<String> splitByMaxLength(final String str, final int maxLength, final boolean once) {
        if (maxLength <= 0 || str.length() <= maxLength) {
            return Collections.singletonList(str);
        }
        final int whitespaceMaxLength = (int) (maxLength * 1.1D);
        final int hardMaxLength = (int) (whitespaceMaxLength * 1.1D);
        final List<String> chunks = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            sb.append(c);
            if (once && !chunks.isEmpty()) {
                continue;
            }
            if (sb.length() >= hardMaxLength || (sb.length() >= whitespaceMaxLength && Character.isWhitespace(c))
                    || (sb.length() >= maxLength && c == '\n')) {
                chunks.add(sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) {
            chunks.add(sb.toString());
        }
        return chunks;
    }

    public static boolean isDecimal(final String str) {
        return NumberUtils.isParsable(str);
    }

    public static boolean isInteger(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            final char c = cs.charAt(i);
            if (!Character.isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

    public static String blankToNull(final String str) {
        if (isBlank(str)) {
            return null;
        } else {
            return str;
        }
    }

    public static boolean startsWith(final CharSequence str, final CharSequence prefix, final int startIndex) {
        return Strings.indexOf(str, prefix, startIndex) >= 0;
    }

}
