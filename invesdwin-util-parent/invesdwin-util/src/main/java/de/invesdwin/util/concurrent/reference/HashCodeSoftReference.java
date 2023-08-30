package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class HashCodeSoftReference<T> implements IReference<T> {

    private final SoftReference<T> reference;
    private final int hashCode;

    public HashCodeSoftReference(final T value) {
        this.reference = SoftReferences.newInstance(value);
        this.hashCode = Objects.hashCode(value);
    }

    public HashCodeSoftReference(final T value, final Object hashCodeProvider) {
        this.reference = SoftReferences.newInstance(value);
        this.hashCode = Objects.hashCode(hashCodeProvider);
    }

    public HashCodeSoftReference(final T value, final int hashCode) {
        this.reference = SoftReferences.newInstance(value);
        this.hashCode = hashCode;
    }

    @Override
    public T get() {
        return reference.get();
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
