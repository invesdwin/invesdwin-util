package de.invesdwin.util.concurrent.reference;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MutableWeakReference<T> implements IMutableReference<T> {

    private WeakReference<T> reference;

    public MutableWeakReference(final T value) {
        this.reference = WeakReferences.newInstance(value);
    }

    @Override
    public T get() {
        return reference.get();
    }

    @Override
    public void set(final T value) {
        reference = WeakReferences.newInstance(value);
    }

    @Override
    public T getAndSet(final T value) {
        final T get = reference.get();
        set(value);
        return get;
    }

}
