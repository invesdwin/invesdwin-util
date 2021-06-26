package de.invesdwin.util.concurrent.reference;

@FunctionalInterface
public interface IReference<T> {

    T get();

}
