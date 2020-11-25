package de.invesdwin.util.assertions.type.internal.junit;

import static java.util.stream.Collectors.joining;

import java.util.Deque;
import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescription;

@Immutable
public final class AssertionUtils {

    private AssertionUtils() {
    }

    public static void fail(final String message) {
        fail(() -> message);
    }

    public static void fail(final String message, final Throwable cause) {
        throw new AssertionError(message, cause);
    }

    public static void fail(final Throwable cause) {
        throw new AssertionError(null, cause);
    }

    public static void fail(final Supplier<String> messageSupplier) {
        throw new AssertionError(nullSafeGet(messageSupplier));
    }

    public static String nullSafeGet(final Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

    public static String buildPrefix(final String message) {
        return (Strings.isNotBlank(message) ? message + " ==> " : "");
    }

    public static String getCanonicalName(final Class<?> clazz) {
        try {
            final String canonicalName = clazz.getCanonicalName();
            return (canonicalName != null ? canonicalName : clazz.getName());
        } catch (final Throwable t) {
            return clazz.getName();
        }
    }

    public static String format(final Object expected, final Object actual, final String message) {
        return buildPrefix(message) + formatValues(expected, actual);
    }

    public static String formatValues(final Object expected, final Object actual) {
        final String expectedString = toString(expected);
        final String actualString = toString(actual);
        if (expectedString.equals(actualString)) {
            return TextDescription.format("expected: %s but was: %s", formatClassAndValue(expected, expectedString),
                    formatClassAndValue(actual, actualString));
        }
        return TextDescription.format("expected: <%s> but was: <%s>", expectedString, actualString);
    }

    private static String formatClassAndValue(final Object value, final String valueString) {
        final String classAndHash = getClassName(value) + toHash(value);
        // if it's a class, there's no need to repeat the class name contained in the valueString.
        return (value instanceof Class ? "<" + classAndHash + ">" : classAndHash + "<" + valueString + ">");
    }

    private static String toString(final Object obj) {
        if (obj instanceof Class) {
            return getCanonicalName((Class<?>) obj);
        }
        return Strings.asStringNullText(obj);
    }

    private static String toHash(final Object obj) {
        return (obj == null ? "" : "@" + Integer.toHexString(System.identityHashCode(obj)));
    }

    private static String getClassName(final Object obj) {
        return (obj == null ? "null"
                : obj instanceof Class ? getCanonicalName((Class<?>) obj) : obj.getClass().getName());
    }

    public static String formatIndexes(final Deque<Integer> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return "";
        }
        final String indexesString = indexes.stream().map(Object::toString).collect(joining("][", "[", "]"));
        return " at index " + indexesString;
    }

    public static boolean floatsAreEqual(final float value1, final float value2, final float delta) {
        assertValidDelta(delta);
        return floatsAreEqual(value1, value2) || Math.abs(value1 - value2) <= delta;
    }

    public static void assertValidDelta(final float delta) {
        if (Float.isNaN(delta) || delta <= 0.0) {
            failIllegalDelta(String.valueOf(delta));
        }
    }

    public static void assertValidDelta(final double delta) {
        if (Double.isNaN(delta) || delta <= 0.0) {
            failIllegalDelta(String.valueOf(delta));
        }
    }

    public static boolean floatsAreEqual(final float value1, final float value2) {
        return Float.floatToIntBits(value1) == Float.floatToIntBits(value2);
    }

    public static boolean doublesAreEqual(final double value1, final double value2, final double delta) {
        assertValidDelta(delta);
        return doublesAreEqual(value1, value2) || Math.abs(value1 - value2) <= delta;
    }

    public static boolean doublesAreEqual(final double value1, final double value2) {
        return Double.doubleToLongBits(value1) == Double.doubleToLongBits(value2);
    }

    public static boolean objectsAreEqual(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            return (obj2 == null);
        }
        return obj1.equals(obj2);
    }

    private static void failIllegalDelta(final String delta) {
        fail("positive delta expected but was: <" + delta + ">");
    }

}
