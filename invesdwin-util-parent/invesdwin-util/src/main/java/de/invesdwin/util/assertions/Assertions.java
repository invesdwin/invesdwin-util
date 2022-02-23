package de.invesdwin.util.assertions;

import java.util.Collection;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.internal.AAssertionsStaticFacade;
import de.invesdwin.util.assertions.type.DecimalAssert;
import de.invesdwin.util.assertions.type.FDateAssert;
import de.invesdwin.util.assertions.type.StringAssert;
import de.invesdwin.util.assertions.type.internal.junit.JUnitAssertions;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@StaticFacadeDefinition(name = "de.invesdwin.util.assertions.internal.AAssertionsStaticFacade", targets = {
        org.assertj.core.api.Assertions.class, org.assertj.guava.api.Assertions.class,
        com.google.common.base.Preconditions.class, JUnitAssertions.class,
        org.assertj.jodatime.api.Assertions.class }, filterMethodSignatureExpressions = {
                ".* org\\.assertj\\.core\\.api\\.StringAssert assertThat\\(java\\.lang\\.String .*",
                ".* fail\\(java\\.lang\\.String .*" })
@Immutable
public final class Assertions extends AAssertionsStaticFacade {

    public static final int COMPARISON_FAILURE_MESSAGE_LIMIT = 1000;

    private static final boolean JUNIT_AVAILABLE;

    static {
        JUNIT_AVAILABLE = Reflections.classExists("org.junit.Assert");
        Assertions.setMaxStackTraceElementsDisplayed(COMPARISON_FAILURE_MESSAGE_LIMIT);
    }

    private Assertions() {
    }

    public static <T extends ADecimal<T>> DecimalAssert<T> assertThat(final T actual) {
        return new DecimalAssert<T>(actual);
    }

    public static StringAssert assertThat(final String actual) {
        return new StringAssert(actual);
    }

    public static FDateAssert assertThat(final FDate actual) {
        return new FDateAssert(actual);
    }

    public static void checkEquals(final char expected, final char actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final byte expected, final byte actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final short expected, final short actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final int expected, final int actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final long expected, final long actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final float expected, final float actual) {
        if (expected != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final double expected, final double actual) {
        if (expected != actual) {
            if (Doubles.isNaN(expected) && Doubles.isNaN(actual)) {
                return;
            }
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final char expected, final Character actual) {
        if (actual == null || expected != actual.charValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final byte expected, final Byte actual) {
        if (actual == null || expected != actual.byteValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final short expected, final Short actual) {
        if (actual == null || expected != actual.shortValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final int expected, final Integer actual) {
        if (actual == null || expected != actual.intValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final long expected, final Long actual) {
        if (actual == null || expected != actual.longValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final float expected, final Float actual) {
        if (actual == null || expected != actual.floatValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final double expected, final Double actual) {
        if (actual == null || expected != actual.doubleValue()) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Character expected, final char actual) {
        if (expected == null || expected.charValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Byte expected, final byte actual) {
        if (expected == null || expected.byteValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Short expected, final short actual) {
        if (expected == null || expected.shortValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Integer expected, final int actual) {
        if (expected == null || expected.intValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Long expected, final long actual) {
        if (expected == null || expected.longValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Float expected, final float actual) {
        if (expected == null || expected.floatValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Double expected, final double actual) {
        if (expected == null || expected.doubleValue() != actual) {
            assertThat(actual).isEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Object expected, final Object actual) {
        if (!Objects.equals(expected, actual)) {
            if (JUNIT_AVAILABLE && expected instanceof String && actual instanceof String) {
                de.invesdwin.util.assertions.type.internal.junit.JUnit5CheckEquals.checkEqualsJunit((String) expected,
                        (String) actual);
            } else {
                assertThat(actual).isEqualTo(expected);
            }
            failExceptionExpected();
        }
    }

    public static void checkEquals(final Object expected, final Object actual, final String message,
            final Object... args) {
        if (!Objects.equals(expected, actual)) {
            if (JUNIT_AVAILABLE && expected instanceof String && actual instanceof String) {
                de.invesdwin.util.assertions.type.internal.junit.JUnit5CheckEquals.checkEqualsJunit((String) expected,
                        (String) actual, message, args);
            } else {
                assertThat(actual).as(message, args).isEqualTo(expected);
            }
            failExceptionExpected();
        }
    }

    public static void checkEquals(final String expected, final String actual, final String message,
            final Object... args) {
        if (!Objects.equals(expected, actual)) {
            if (JUNIT_AVAILABLE) {
                de.invesdwin.util.assertions.type.internal.junit.JUnit5CheckEquals.checkEqualsJunit(expected, actual,
                        message, args);
            } else {
                assertThat(actual).as(message, args).isEqualTo(expected);
            }
            failExceptionExpected();
        }
    }

    public static void checkNotEquals(final Object expected, final Object actual) {
        if (Objects.equals(expected, actual)) {
            assertThat(actual).isNotEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void checkNotEquals(final Object expected, final Object actual, final String message,
            final Object... args) {
        if (Objects.equals(expected, actual)) {
            assertThat(actual).as(message, args).isNotEqualTo(expected);
            failExceptionExpected();
        }
    }

    public static void failExceptionExpected() {
        fail("Exception expected");
    }

    public static void checkSame(final Object expected, final Object actual) {
        if (expected != actual) {
            assertThat(actual).isSameAs(expected);
            failExceptionExpected();
        }
    }

    public static void checkSame(final Object expected, final Object actual, final String message,
            final Object... args) {
        if (expected != actual) {
            assertThat(actual).as(message, args).isSameAs(expected);
            failExceptionExpected();
        }
    }

    public static void checkNull(final Object obj) {
        if (obj != null) {
            assertThat(obj).isNull();
            failExceptionExpected();
        }
    }

    public static void checkNull(final Object obj, final String message, final Object... args) {
        if (obj != null) {
            assertThat(obj).as(message, args).isNull();
            failExceptionExpected();
        }
    }

    public static void checkTrue(final boolean expression) {
        if (!expression) {
            assertThat(expression).isTrue();
            failExceptionExpected();
        }
    }

    public static void checkTrue(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            assertThat(expression).as(message, args).isTrue();
            failExceptionExpected();
        }
    }

    public static void checkFalse(final boolean expression) {
        if (expression) {
            assertThat(expression).isFalse();
            failExceptionExpected();
        }
    }

    public static void checkBlank(final String str) {
        if (Strings.isNotBlank(str)) {
            assertThat(str).isBlank();
            failExceptionExpected();
        }
    }

    public static void checkBlank(final String str, final String message, final Object... args) {
        if (Strings.isNotBlank(str)) {
            assertThat(str).as(message, args).isBlank();
            failExceptionExpected();
        }
    }

    public static void checkNotBlank(final String str) {
        if (Strings.isBlank(str)) {
            assertThat(str).isNotBlank();
            failExceptionExpected();
        }
    }

    public static void checkNotBlank(final String str, final String message, final Object... args) {
        if (Strings.isBlank(str)) {
            assertThat(str).as(message, args).isNotBlank();
            failExceptionExpected();
        }
    }

    public static void checkNotNaN(final double value) {
        if (Doubles.isNaN(value)) {
            assertThat(value).isNotNaN();
            failExceptionExpected();
        }
    }

    public static void checkNotNaN(final double value, final String message, final Object... args) {
        if (Doubles.isNaN(value)) {
            assertThat(value).as(message, args).isNotNaN();
            failExceptionExpected();
        }
    }

    public static void checkNotNaN(final Number value) {
        if (Doubles.isNaN(value)) {
            assertThat(value.doubleValue()).isNotNaN();
            failExceptionExpected();
        }
    }

    public static void checkNotNaN(final Number value, final String message, final Object... args) {
        if (Doubles.isNaN(value)) {
            assertThat(value.doubleValue()).as(message, args).isNotNaN();
            failExceptionExpected();
        }
    }

    public static void checkFalse(final boolean expression, final String message, final Object... args) {
        if (expression) {
            assertThat(expression).as(message, args).isFalse();
            failExceptionExpected();
        }
    }

    public static <S> void checkNotEmpty(final Collection<S> collection) {
        if (collection.isEmpty()) {
            assertThat(collection).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static <S> void checkNotEmpty(final Collection<S> collection, final String message, final Object... args) {
        if (collection.isEmpty()) {
            assertThat(collection).as(message, args).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static <K, V> void checkNotEmpty(final Map<K, V> collection) {
        if (collection.isEmpty()) {
            assertThat(collection).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static <K, V> void checkNotEmpty(final Map<K, V> collection, final String message, final Object... args) {
        if (collection.isEmpty()) {
            assertThat(collection).as(message, args).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Object[] collection) {
        if (collection.length == 0) {
            assertThat(collection).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkNotEmpty(final Object[] collection, final String message, final Object... args) {
        if (collection.length == 0) {
            assertThat(collection).as(message, args).isNotEmpty();
            failExceptionExpected();
        }
    }

    public static void checkEmpty(final Map<?, ?> map) {
        if (!map.isEmpty()) {
            assertThat(map).isEmpty();
            failExceptionExpected();
        }
    }

    public static void checkEmpty(final Map<?, ?> map, final String message, final Object... args) {
        if (!map.isEmpty()) {
            assertThat(map).as(message, args).isEmpty();
            failExceptionExpected();
        }
    }

    public static <S> void checkEmpty(final Collection<S> collection) {
        if (!collection.isEmpty()) {
            assertThat(collection).isEmpty();
            failExceptionExpected();
        }
    }

    public static <S> void checkEmpty(final Collection<S> collection, final String message, final Object... args) {
        if (!collection.isEmpty()) {
            assertThat(collection).as(message, args).isEmpty();
            failExceptionExpected();
        }
    }

    public static void checkEmpty(final Object[] collection) {
        if (collection.length != 0) {
            assertThat(collection).isEmpty();
            failExceptionExpected();
        }
    }

    public static void checkEmpty(final Object[] collection, final String message, final Object... args) {
        if (collection.length != 0) {
            assertThat(collection).as(message, args).isEmpty();
            failExceptionExpected();
        }
    }

    public static <S> void checkContains(final Collection<S> collection, final S element) {
        if (!collection.contains(element)) {
            assertThat(collection).contains(element);
            failExceptionExpected();
        }
    }

    public static <S> void checkContains(final Collection<S> collection, final S element, final String message,
            final Object... args) {
        if (!collection.contains(element)) {
            assertThat(collection).as(message, args).contains(element);
            failExceptionExpected();
        }
    }

    public static void fail(final String failureMessage) {
        org.assertj.core.api.Assertions.fail(failureMessage);
    }

    public static void fail(final String failureMessage, final java.lang.Throwable realCause) {
        org.assertj.core.api.Assertions.fail(failureMessage, realCause);
    }

    public static void assertTimeout(final Duration timeout, final Executable executable) {
        JUnitAssertions.assertTimeout(timeout.javaTimeValue(), executable);
    }

    public static void assertTimeout(final Duration timeout, final Executable executable, final String message) {
        JUnitAssertions.assertTimeout(timeout.javaTimeValue(), executable, message);
    }

    public static void assertTimeout(final Duration timeout, final Executable executable,
            final java.util.function.Supplier<String> messageSupplier) {
        JUnitAssertions.assertTimeout(timeout.javaTimeValue(), executable, messageSupplier);
    }

    public static <T extends java.lang.Object> T assertTimeout(final Duration timeout,
            final ThrowingSupplier<T> supplier) {
        return JUnitAssertions.assertTimeout(timeout.javaTimeValue(), supplier);
    }

    public static <T extends java.lang.Object> T assertTimeout(final Duration timeout,
            final ThrowingSupplier<T> supplier, final String message) {
        return JUnitAssertions.assertTimeout(timeout.javaTimeValue(), supplier, message);
    }

    public static <T extends java.lang.Object> T assertTimeout(final Duration timeout,
            final ThrowingSupplier<T> supplier, final java.util.function.Supplier<String> messageSupplier) {
        return JUnitAssertions.assertTimeout(timeout.javaTimeValue(), supplier, messageSupplier);
    }

    public static void assertTimeoutPreemptively(final Duration timeout, final Executable executable) {
        JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), executable);
    }

    public static void assertTimeoutPreemptively(final Duration timeout, final Executable executable,
            final String message) {
        JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), executable, message);
    }

    public static void assertTimeoutPreemptively(final Duration timeout, final Executable executable,
            final java.util.function.Supplier<String> messageSupplier) {
        JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), executable, messageSupplier);
    }

    public static <T extends java.lang.Object> T assertTimeoutPreemptively(final Duration timeout,
            final ThrowingSupplier<T> supplier) {
        return JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), supplier);
    }

    public static <T extends java.lang.Object> T assertTimeoutPreemptively(final Duration timeout,
            final ThrowingSupplier<T> supplier, final String message) {
        return JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), supplier, message);
    }

    public static <T extends java.lang.Object> T assertTimeoutPreemptively(final Duration timeout,
            final ThrowingSupplier<T> supplier, final java.util.function.Supplier<String> messageSupplier) {
        return JUnitAssertions.assertTimeoutPreemptively(timeout.javaTimeValue(), supplier, messageSupplier);
    }

}
