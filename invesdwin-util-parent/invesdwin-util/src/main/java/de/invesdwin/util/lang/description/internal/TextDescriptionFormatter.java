package de.invesdwin.util.lang.description.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.slf4j.helpers.Util;

import de.invesdwin.util.lang.Strings;

/**
 * Adapted from SLF4J to use %s instead of {}
 * 
 * @author subes
 *
 */
@Immutable
public final class TextDescriptionFormatter {
    private static final String DELIM_START = "%";
    private static final String DELIM_STR = DELIM_START + "s";
    private static final char ESCAPE_CHAR = '\\';

    private TextDescriptionFormatter() {
    }

    public static String format(final String messagePattern, final Object arg) {
        return format(messagePattern, new Object[] { arg });
    }

    public static String format(final String messagePattern, final Object arg1, final Object arg2) {
        return format(messagePattern, new Object[] { arg1, arg2 });
    }

    public static String format(final String messagePattern, final Object[] argArray) {

        if (Strings.isBlank(messagePattern)) {
            return Arrays.toString(argArray);
        }

        if (argArray == null || argArray.length == 0) {
            return messagePattern;
        }

        int messagePatternIdx = 0;
        int delimiterStartIdx;
        // use string builder for better multicore performance
        final StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);

        int argIdx;
        for (argIdx = 0; argIdx < argArray.length; argIdx++) {

            delimiterStartIdx = messagePattern.indexOf(DELIM_STR, messagePatternIdx);

            if (delimiterStartIdx == -1) {
                // no more variables
                if (messagePatternIdx == 0) { // this is a simple string
                    sbuf.append(messagePattern);
                    sbuf.append(" ");
                    appendArgs(sbuf, argArray, 0);
                    return sbuf.toString();
                } else { // add the tail string which contains no variables and return
                    // the result.
                    sbuf.append(messagePattern, messagePatternIdx, messagePattern.length());
                    sbuf.append(" ");
                    appendArgs(sbuf, argArray, argIdx);
                    return sbuf.toString();
                }
            } else {
                if (isEscapedDelimeter(messagePattern, delimiterStartIdx)) {
                    if (!isDoubleEscaped(messagePattern, delimiterStartIdx)) {
                        argIdx--; // DELIM_START was escaped, thus should not be incremented
                        sbuf.append(messagePattern, messagePatternIdx, delimiterStartIdx - 1);
                        sbuf.append(DELIM_START);
                        messagePatternIdx = delimiterStartIdx + 1;
                    } else {
                        // The escape character preceding the delimiter start is
                        // itself escaped: "abc x:\\%s"
                        // we have to consume one backward slash
                        sbuf.append(messagePattern, messagePatternIdx, delimiterStartIdx - 1);
                        deeplyAppendParameter(sbuf, argArray[argIdx], new HashMap<Object[], Object>());
                        messagePatternIdx = delimiterStartIdx + 2;
                    }
                } else {
                    // normal case
                    sbuf.append(messagePattern, messagePatternIdx, delimiterStartIdx);
                    deeplyAppendParameter(sbuf, argArray[argIdx], new HashMap<Object[], Object>());
                    messagePatternIdx = delimiterStartIdx + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        sbuf.append(messagePattern, messagePatternIdx, messagePattern.length());
        return sbuf.toString();
    }

    private static String appendArgs(final StringBuilder sbuf, final Object[] a, final int startIndex) {
        final int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        sbuf.append('[');
        for (int i = startIndex;; i++) {
            sbuf.append(String.valueOf(a[i]));
            if (i == iMax) {
                return sbuf.append(']').toString();
            }
            sbuf.append(", ");
        }
    }

    private static boolean isEscapedDelimeter(final String messagePattern, final int delimeterStartIndex) {

        if (delimeterStartIndex == 0) {
            return false;
        }
        final char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == ESCAPE_CHAR;
    }

    private static boolean isDoubleEscaped(final String messagePattern, final int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
    }

    // special treatment of array values was suggested by 'lizongbo'
    private static void deeplyAppendParameter(final StringBuilder sbuf, final Object o,
            final Map<Object[], Object> seenMap) {
        if (o == null) {
            sbuf.append("null");
            return;
        }
        if (!o.getClass().isArray()) {
            safeObjectAppend(sbuf, o);
        } else {
            // check for primitive array types because they
            // unfortunately cannot be cast to Object[]
            if (o instanceof boolean[]) {
                booleanArrayAppend(sbuf, (boolean[]) o);
            } else if (o instanceof byte[]) {
                byteArrayAppend(sbuf, (byte[]) o);
            } else if (o instanceof char[]) {
                charArrayAppend(sbuf, (char[]) o);
            } else if (o instanceof short[]) {
                shortArrayAppend(sbuf, (short[]) o);
            } else if (o instanceof int[]) {
                intArrayAppend(sbuf, (int[]) o);
            } else if (o instanceof long[]) {
                longArrayAppend(sbuf, (long[]) o);
            } else if (o instanceof float[]) {
                floatArrayAppend(sbuf, (float[]) o);
            } else if (o instanceof double[]) {
                doubleArrayAppend(sbuf, (double[]) o);
            } else {
                objectArrayAppend(sbuf, (Object[]) o, seenMap);
            }
        }
    }

    private static void safeObjectAppend(final StringBuilder sbuf, final Object o) {
        try {
            final String oAsString = o.toString();
            sbuf.append(oAsString);
        } catch (final Throwable t) {
            Util.report("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
            sbuf.append("[FAILED toString()]");
        }

    }

    private static void objectArrayAppend(final StringBuilder sbuf, final Object[] a,
            final Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            final int len = a.length;
            for (int i = 0; i < len; i++) {
                deeplyAppendParameter(sbuf, a[i], seenMap);
                if (i != len - 1) {
                    sbuf.append(", ");
                }
            }
            // allow repeats in siblings
            seenMap.remove(a);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(final StringBuilder sbuf, final boolean[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(final StringBuilder sbuf, final byte[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(final StringBuilder sbuf, final char[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(final StringBuilder sbuf, final short[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(final StringBuilder sbuf, final int[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(final StringBuilder sbuf, final long[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(final StringBuilder sbuf, final float[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(final StringBuilder sbuf, final double[] a) {
        sbuf.append('[');
        final int len = a.length;
        for (int i = 0; i < len; i++) {
            sbuf.append(a[i]);
            if (i != len - 1) {
                sbuf.append(", ");
            }
        }
        sbuf.append(']');
    }

}