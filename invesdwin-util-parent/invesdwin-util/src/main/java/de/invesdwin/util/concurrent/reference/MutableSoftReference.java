package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MutableSoftReference<T> implements IMutableReference<T> {

    private SoftReference<T> reference;

    public MutableSoftReference(final T value) {
        this.reference = SoftReferences.newInstance(value);
    }

    @Override
    public T get() {
        return reference.get();
    }

    @Override
    public void set(final T value) {
        reference = SoftReferences.newInstance(value);
    }

    @Override
    public T getAndSet(final T value) {
        final T get = reference.get();
        set(value);
        return get;
    }

}
