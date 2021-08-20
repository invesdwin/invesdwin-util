package de.invesdwin.util.concurrent.reference;

public interface IMutableReference<T> extends IReference<T> {

    void set(T value);

    T getAndSet(T value);

}
