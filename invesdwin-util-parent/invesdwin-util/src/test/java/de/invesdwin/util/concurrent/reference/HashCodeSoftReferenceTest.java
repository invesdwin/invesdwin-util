package de.invesdwin.util.concurrent.reference;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

/**
 * Pins the accepted equals/hashCode contract of HashCodeSoftReference (same hash-based shape as
 * HashCodeWeakReference, see its test for the full contract argument).
 */
@NotThreadSafe
public class HashCodeSoftReferenceTest {

    @Test
    public void testSameValueIsEqualWithStableHash() {
        final HashCodeSoftReference<String> a = new HashCodeSoftReference<>("abc");
        final HashCodeSoftReference<String> b = new HashCodeSoftReference<>("abc");
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        Assertions.assertThat(a.get()).isEqualTo("abc");
    }

    @Test
    public void testDifferentValueHashIsUnequal() {
        final HashCodeSoftReference<String> a = new HashCodeSoftReference<>("abc");
        final HashCodeSoftReference<String> b = new HashCodeSoftReference<>("xyz");
        Assertions.assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

}
