package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ImmutableSoftReference<T> implements IReference<T> {

    private final SoftReference<T> reference;

    public ImmutableSoftReference(final T value) {
        this.reference = SoftReferences.newInstance(value);
    }

    @Override
    public T get() {
        return reference.get();
    }

}
