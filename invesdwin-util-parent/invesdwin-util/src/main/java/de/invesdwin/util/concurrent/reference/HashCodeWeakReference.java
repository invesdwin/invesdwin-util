package de.invesdwin.util.concurrent.reference;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

/**
 * Weak reference that retains the hashCode taken at construction time (optionally from a separate
 * hashCodeProvider) so that the hash stays valid after the referent has been collected — the wrapper
 * is meant to serve as a key in hash-based collections across collection of the referent.
 *
 * The equals/hashCode pair is intentionally defined on the retained hash rather than on the referent:
 * registries keyed by this wrapper rely on hash-equal wrappers to be equal — two registrations of
 * different listeners of the same parent must deduplicate, and an entry whose referent was collected
 * must still be removable from the map. Referent-based equality would break both (collected referents
 * can no longer be compared), so this is a documented, accepted contract shape, not an oversight.
 *
 * Consequence (contract drift by design): distinct referents whose hash codes collide are
 * indistinguishable through this wrapper; consumers that care must guard for it (the trading
 * BroadcastingLastValueFromFeedListener, the only known registry, throws an IllegalStateException when
 * a duplicate key turns out to be a different parent identity).
 */
@ThreadSafe
public class HashCodeWeakReference<T> implements IReference<T> {

    private final WeakReference<T> reference;
    private final int hashCode;

    public HashCodeWeakReference(final T value) {
        this.reference = WeakReferences.newInstance(value);
        this.hashCode = Objects.hashCode(value);
    }

    public HashCodeWeakReference(final T value, final Object hashCodeProvider) {
        this.reference = WeakReferences.newInstance(value);
        this.hashCode = Objects.hashCode(hashCodeProvider);
    }

    public HashCodeWeakReference(final T value, final int hashCode) {
        this.reference = WeakReferences.newInstance(value);
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
        if (obj instanceof HashCodeWeakReference) {
            final HashCodeWeakReference<?> cObj = (HashCodeWeakReference<?>) obj;
            return cObj.hashCode == hashCode;
        }
        return false;
    }

}
