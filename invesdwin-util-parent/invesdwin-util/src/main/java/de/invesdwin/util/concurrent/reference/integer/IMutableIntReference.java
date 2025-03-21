package de.invesdwin.util.concurrent.reference.integer;

public interface IMutableIntReference extends IIntReference {

    void set(int value);

    int getAndSet(int value);

    int incrementAndGet();

    int decrementAndGet();

}
