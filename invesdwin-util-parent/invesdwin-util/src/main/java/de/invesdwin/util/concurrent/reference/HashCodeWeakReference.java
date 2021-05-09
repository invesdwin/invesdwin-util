package de.invesdwin.util.concurrent.reference;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class HashCodeWeakReference<T> implements IReference<T> {

    private final WeakReference<T> weakReference;
    private final int hashCode;

    public HashCodeWeakReference(final T value) {
        this.weakReference = new WeakReference<>(value);
        this.hashCode = Objects.hashCode(value);
    }

    public HashCodeWeakReference(final T value, final Object hashCodeProvider) {
        this.weakReference = new WeakReference<>(value);
        this.hashCode = Objects.hashCode(hashCodeProvider);
    }

    public HashCodeWeakReference(final T value, final int hashCode) {
        this.weakReference = new WeakReference<>(value);
        this.hashCode = hashCode;
    }

    @Override
    public T get() {
        return weakReference.get();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof HashCodeWeakReference) {
            final HashCodeWeakReference<?> cObj = (HashCodeWeakReference<?>) obj;
            return cObj.hashCode == hashCode;
        }
        return false;
    }

}
