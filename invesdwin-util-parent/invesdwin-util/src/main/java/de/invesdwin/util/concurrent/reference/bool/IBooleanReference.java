package de.invesdwin.util.concurrent.reference.bool;

@FunctionalInterface
public interface IBooleanReference {

    boolean get();

    default boolean isTrue() {
        return get();
    }

    default boolean isFalse() {
        return !get();
    }

}
