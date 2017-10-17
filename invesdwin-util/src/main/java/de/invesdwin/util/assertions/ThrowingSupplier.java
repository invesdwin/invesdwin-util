package de.invesdwin.util.assertions;

// CHECKSTYLE:OFF
@FunctionalInterface
public interface ThrowingSupplier<T> {
    //CHECKSTYLE:ON

    T get() throws Throwable;

}
