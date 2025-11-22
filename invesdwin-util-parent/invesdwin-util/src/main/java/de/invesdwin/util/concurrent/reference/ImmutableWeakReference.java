package de.invesdwin.util.concurrent.reference;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ImmutableWeakReference<T> implements IReference<T> {

    private final WeakReference<T> reference;

    public ImmutableWeakReference(final T value) {
        this.reference = WeakReferences.newInstance(value);
    }

    @Override
    public T get() {
        return reference.get();
    }

}
