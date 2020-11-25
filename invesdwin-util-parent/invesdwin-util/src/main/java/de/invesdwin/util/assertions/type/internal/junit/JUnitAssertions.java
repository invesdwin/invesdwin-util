package de.invesdwin.util.assertions.type.internal.junit;

import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Executable;
import de.invesdwin.util.assertions.ThrowingSupplier;

/**
 * TODO: replace this with the actual junit jupiter assertions as soon as it is possible to upgrade to junit 5 without
 * breaking aspectj
 * 
 * @author subes
 *
 */
@Immutable
public final class JUnitAssertions {

    private JUnitAssertions() {}

    // --- assert exceptions ---------------------------------------------------

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} throws an exception of the
     * {@code expectedType} and returns the exception.
     *
     * <p>
     * If no exception is thrown, or if an exception of a different type is thrown, this method will fail.
     *
     * <p>
     * If you do not want to perform additional checks on the exception instance, simply ignore the return value.
     */
    public static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable) {
        return AssertThrows.assertThrows(expectedType, executable);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} throws an exception of the
     * {@code expectedType} and returns the exception.
     *
     * <p>
     * If no exception is thrown, or if an exception of a different type is thrown, this method will fail.
     *
     * <p>
     * If you do not want to perform additional checks on the exception instance, simply ignore the return value.
     */
    public static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable,
            final String message) {
        return AssertThrows.assertThrows(expectedType, executable, message);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} throws an exception of the
     * {@code expectedType} and returns the exception.
     *
     * <p>
     * If no exception is thrown, or if an exception of a different type is thrown, this method will fail.
     *
     * <p>
     * If necessary, the failure message will be retrieved lazily from the supplied {@code messageSupplier}.
     *
     * <p>
     * If you do not want to perform additional checks on the exception instance, simply ignore the return value.
     */
    public static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable,
            final Supplier<String> messageSupplier) {
        return AssertThrows.assertThrows(expectedType, executable, messageSupplier);
    }

    // --- assertTimeout -------------------------------------------------------

    // --- executable ---

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code executable} will not be preemptively aborted if the timeout is exceeded.
     *
     * @see #assertTimeout(Duration, Executable, String)
     * @see #assertTimeout(Duration, Executable, Supplier)
     * @see #assertTimeout(Duration, ThrowingSupplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, String)
     * @see #assertTimeout(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeoutPreemptively(Duration, Executable)
     */
    public static void assertTimeout(final java.time.Duration timeout, final Executable executable) {
        AssertTimeout.assertTimeout(timeout, executable);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code executable} will not be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * Fails with the supplied failure {@code message}.
     *
     * @see #assertTimeout(Duration, Executable)
     * @see #assertTimeout(Duration, Executable, Supplier)
     * @see #assertTimeout(Duration, ThrowingSupplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, String)
     * @see #assertTimeout(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     */
    public static void assertTimeout(final java.time.Duration timeout, final Executable executable,
            final String message) {
        AssertTimeout.assertTimeout(timeout, executable, message);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code executable} will not be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * If necessary, the failure message will be retrieved lazily from the supplied {@code messageSupplier}.
     *
     * @see #assertTimeout(Duration, Executable)
     * @see #assertTimeout(Duration, Executable, String)
     * @see #assertTimeout(Duration, ThrowingSupplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, String)
     * @see #assertTimeout(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     */
    public static void assertTimeout(final java.time.Duration timeout, final Executable executable,
            final Supplier<String> messageSupplier) {
        AssertTimeout.assertTimeout(timeout, executable, messageSupplier);
    }

    // --- supplier ---

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code supplier} will not be preemptively aborted if the timeout is exceeded.
     *
     * @see #assertTimeout(Duration, Executable)
     * @see #assertTimeout(Duration, Executable, String)
     * @see #assertTimeout(Duration, Executable, Supplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, String)
     * @see #assertTimeout(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeoutPreemptively(Duration, Executable)
     */
    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier) {
        return AssertTimeout.assertTimeout(timeout, supplier);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code supplier} will not be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * Fails with the supplied failure {@code message}.
     *
     * @see #assertTimeout(Duration, Executable)
     * @see #assertTimeout(Duration, Executable, String)
     * @see #assertTimeout(Duration, Executable, Supplier)
     * @see #assertTimeout(Duration, ThrowingSupplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     */
    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final String message) {
        return AssertTimeout.assertTimeout(timeout, supplier, message);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in the same thread as that of the calling code. Consequently,
     * execution of the {@code supplier} will not be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * If necessary, the failure message will be retrieved lazily from the supplied {@code messageSupplier}.
     *
     * @see #assertTimeout(Duration, Executable)
     * @see #assertTimeout(Duration, Executable, String)
     * @see #assertTimeout(Duration, Executable, Supplier)
     * @see #assertTimeout(Duration, ThrowingSupplier)
     * @see #assertTimeout(Duration, ThrowingSupplier, String)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     */
    public static <T> T assertTimeout(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final Supplier<String> messageSupplier) {
        return AssertTimeout.assertTimeout(timeout, supplier, messageSupplier);
    }

    // --- executable - preemptively ---

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code executable} will be preemptively aborted if the timeout is exceeded.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, String)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeout(Duration, Executable)
     */
    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable) {
        AssertTimeout.assertTimeoutPreemptively(timeout, executable);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code executable} will be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * Fails with the supplied failure {@code message}.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, String)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeout(Duration, Executable, String)
     */
    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable,
            final String message) {
        AssertTimeout.assertTimeoutPreemptively(timeout, executable, message);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code executable} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * Note: the {@code executable} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code executable} will be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * If necessary, the failure message will be retrieved lazily from the supplied {@code messageSupplier}.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, String)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeout(Duration, Executable, Supplier)
     */
    public static void assertTimeoutPreemptively(final java.time.Duration timeout, final Executable executable,
            final Supplier<String> messageSupplier) {
        AssertTimeout.assertTimeoutPreemptively(timeout, executable, messageSupplier);
    }

    // --- supplier - preemptively ---

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code supplier} will be preemptively aborted if the timeout is exceeded.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, String)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeout(Duration, Executable)
     */
    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout,
            final ThrowingSupplier<T> supplier) {
        return AssertTimeout.assertTimeoutPreemptively(timeout, supplier);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code supplier} will be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * Fails with the supplied failure {@code message}.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, Supplier)
     * @see #assertTimeout(Duration, Executable, String)
     */
    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final String message) {
        return AssertTimeout.assertTimeoutPreemptively(timeout, supplier, message);
    }

    /**
     * <em>Asserts</em> that execution of the supplied {@code supplier} completes before the given {@code timeout} is
     * exceeded.
     *
     * <p>
     * If the assertion passes then the {@code supplier}'s result is returned.
     *
     * <p>
     * Note: the {@code supplier} will be executed in a different thread than that of the calling code. Furthermore,
     * execution of the {@code supplier} will be preemptively aborted if the timeout is exceeded.
     *
     * <p>
     * If necessary, the failure message will be retrieved lazily from the supplied {@code messageSupplier}.
     *
     * @see #assertTimeoutPreemptively(Duration, Executable)
     * @see #assertTimeoutPreemptively(Duration, Executable, String)
     * @see #assertTimeoutPreemptively(Duration, Executable, Supplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier)
     * @see #assertTimeoutPreemptively(Duration, ThrowingSupplier, String)
     * @see #assertTimeout(Duration, Executable, Supplier)
     */
    public static <T> T assertTimeoutPreemptively(final java.time.Duration timeout, final ThrowingSupplier<T> supplier,
            final Supplier<String> messageSupplier) {
        return AssertTimeout.assertTimeoutPreemptively(timeout, supplier, messageSupplier);
    }

}
