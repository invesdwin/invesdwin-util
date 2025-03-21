package de.invesdwin.util.concurrent.reference.bool;

public interface IMutableBooleanReference extends IBooleanReference {

    void set(boolean value);

    default void setTrue() {
        set(true);
    }

    default void setFalse() {
        set(false);
    }

    boolean getAndSet(boolean value);

}
