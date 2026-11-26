package de.invesdwin.util.concurrent.reference;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * Pins the accepted equals/hashCode contract of HashCodeWeakReference: equality is defined on the
 * retained hashCode (NOT on the referent). This is what the only known registry — the trading
 * BroadcastingLastValueFromFeedListener and its WeakListener keys — demonstrably relies on: two
 * wrappers around different listeners of the same parent must deduplicate in the map, and entries
 * whose referent was garbage-collected must stay removable. The tests pin exactly that so a future
 * "fix" to referent equality fails loudly here.
 */
@NotThreadSafe
public class HashCodeWeakReferenceTest {

    @Test
    public void testSameValueIsEqualWithStableHash() {
        final HashCodeWeakReference<String> a = new HashCodeWeakReference<>("abc");
        final HashCodeWeakReference<String> b = new HashCodeWeakReference<>("abc");
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assertions.assertThat(a.get()).isEqualTo("abc");
    }

    @Test
    public void testDifferentValueHashIsUnequal() {
        final HashCodeWeakReference<String> a = new HashCodeWeakReference<>("abc");
        final HashCodeWeakReference<String> b = new HashCodeWeakReference<>("xyz");
        Assertions.assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

    @Test
    public void testSameHashCodeProviderDeduplicatesInMap() {
        // WeakListener flow: super(listener, listener.getParent()) — the map key equality the
        // registry depends on is hashCode-of-parent equality, so two different listeners of the
        // same parent must land on one map slot.
        final Object parent = new Object();
        final Object listenerA = new Object();
        final Object listenerB = new Object();
        final HashCodeWeakReference<Object> w1 = new HashCodeWeakReference<>(listenerA, parent);
        final HashCodeWeakReference<Object> w2 = new HashCodeWeakReference<>(listenerB, parent);
        Assertions.assertThat(w1.hashCode()).isEqualTo(parent.hashCode());
        Assertions.assertThat(w1.equals(w2)).isTrue();
        final Map<HashCodeWeakReference<Object>, Integer> map = ILockCollectionFactory.getInstance(false)
                .newLinkedMap();
        Assertions.assertThat(map.put(w1, 1)).isNull();
        Assertions.assertThat(map.put(w2, 2)).isEqualTo(1);
        Assertions.assertThat(map.size()).isEqualTo(1);
        Assertions.assertThat(map.remove(w2)).isEqualTo(2);
        Assertions.assertThat(map.isEmpty()).isTrue();
    }

    @Test
    public void testExplicitHashCodeCollisionFoldsToEqualByDesign() {
        // documented contract drift: distinct referents with an equal retained hash are equal
        final Object x = new Object();
        final Object y = new Object();
        final HashCodeWeakReference<Object> a = new HashCodeWeakReference<>(x, 7);
        final HashCodeWeakReference<Object> b = new HashCodeWeakReference<>(y, 7);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(7);
    }

}
