package de.invesdwin.util.concurrent.reference;

import java.lang.ref.SoftReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

/**
 * Soft reference that retains the hashCode taken at construction time (optionally from a separate
 * hashCodeProvider) so that the hash stays valid after the referent has been collected — the wrapper
 * is meant to serve as a key in hash-based collections across collection of the referent.
 *
 * The equals/hashCode pair is intentionally defined on the retained hash rather than on the referent
 * (same documented contract shape as HashCodeWeakReference): hash-equal wrappers are equal, which is
 * what hash-keyed registries of interned wrappers depend on; referent-based equality would make
 * entries whose referent was collected uncomparable and hence unremovable.
 *
 * Consequence (contract drift by design): distinct referents whose hash codes collide are
 * indistinguishable through this wrapper; consumers that care must guard for it themselves.
 */
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
