package de.invesdwin.util.concurrent.reference;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.time.date.FDate;

/**
 * Pins the equals/hashCode contract chosen for TimedReference: equals is value-only (time is NOT part
 * of equality, and the wrapper is deliberately equal to a raw value it wraps), so the hash must fold
 * the value ONLY — never time, never the class token.
 */
@NotThreadSafe
public class TimedReferenceTest {

    @Test
    public void testEqualValueDifferentTimeAgreeOnHash() {
        final TimedReference<String> a = new TimedReference<>(new FDate(111L), "same");
        final TimedReference<String> b = new TimedReference<>(new FDate(999L), "same");
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        final Map<TimedReference<String>, String> map = ILockCollectionFactory.getInstance(false).newMap();
        map.put(a, "hit");
        Assertions.assertThat(map.get(b)).isEqualTo("hit");
        Assertions.assertThat(map.size()).isEqualTo(1);
    }

    @Test
    public void testCrossTypeEqualsWithRawValueAgreesOnHash() {
        final FDate value = new FDate(2000000000L);
        final TimedReference<FDate> a = new TimedReference<>(new FDate(111L), value);
        Assertions.assertThat(a.equals(value)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(value.hashCode());
    }

    @Test
    public void testNullValuePairsAgreeOnHash() {
        final TimedReference<String> a = new TimedReference<>(new FDate(111L), null);
        final TimedReference<String> b = new TimedReference<>(new FDate(999L), null);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void testDifferentValuesAreUnequal() {
        final TimedReference<String> a = new TimedReference<>(new FDate(111L), "one");
        final TimedReference<String> b = new TimedReference<>(new FDate(111L), "two");
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

}
