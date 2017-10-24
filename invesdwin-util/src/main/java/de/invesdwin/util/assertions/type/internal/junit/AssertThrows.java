package de.invesdwin.util.assertions.type.internal.junit;

import java.util.function.Supplier;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Executable;

@Immutable
public final class AssertThrows {

    private AssertThrows() {}

    static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable) {
        return assertThrows(expectedType, executable, () -> null);
    }

    static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable,
            final String message) {
        return assertThrows(expectedType, executable, () -> message);
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> T assertThrows(final Class<T> expectedType, final Executable executable,
            final Supplier<String> messageSupplier) {

        try {
            executable.execute();
        } catch (final Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return (T) actualException;
            } else {
                final String message = AssertionUtils.buildPrefix(AssertionUtils.nullSafeGet(messageSupplier))
                        + AssertionUtils.format(expectedType, actualException.getClass(),
                                "Unexpected exception type thrown");
                throw new AssertionError(message, actualException);
            }
        }

        final String message = AssertionUtils.buildPrefix(AssertionUtils.nullSafeGet(messageSupplier)) + String.format(
                "Expected %s to be thrown, but nothing was thrown.", AssertionUtils.getCanonicalName(expectedType));
        throw new AssertionError(message);
    }

}
