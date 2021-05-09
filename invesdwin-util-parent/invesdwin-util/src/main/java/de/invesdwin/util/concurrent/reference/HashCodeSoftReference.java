package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class HashCodeSoftReference<T> implements IReference<T> {

    private final SoftReference<T> weakReference;
    private final int hashCode;

    public HashCodeSoftReference(final T value) {
        this.weakReference = new SoftReference<>(value);
        this.hashCode = Objects.hashCode(value);
    }

    public HashCodeSoftReference(final T value, final Object hashCodeProvider) {
        this.weakReference = new SoftReference<>(value);
        this.hashCode = Objects.hashCode(hashCodeProvider);
    }

    public HashCodeSoftReference(final T value, final int hashCode) {
        this.weakReference = new SoftReference<>(value);
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
        if (obj instanceof HashCodeSoftReference) {
            final HashCodeSoftReference<?> cObj = (HashCodeSoftReference<?>) obj;
            return cObj.hashCode == hashCode;
        }
        return false;
    }

}
