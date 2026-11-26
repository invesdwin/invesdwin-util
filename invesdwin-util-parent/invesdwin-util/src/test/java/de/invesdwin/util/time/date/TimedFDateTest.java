package de.invesdwin.util.time.date;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * Pins the equals/hashCode contract chosen for TimedFDate: equals is value-only (time is NOT part of
 * equality, and the wrapper is deliberately equal to its raw FDate value), so the hash must fold the
 * value ONLY — never time, never the class token.
 */
@NotThreadSafe
public class TimedFDateTest {

    @Test
    public void testEqualValueDifferentTimeAgreeOnHash() {
        final FDate value1 = new FDate(1000000000L);
        final FDate value2 = new FDate(1000000000L);
        final TimedFDate a = new TimedFDate(new FDate(111L), value1);
        final TimedFDate b = new TimedFDate(new FDate(999L), value2);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
        // map/intern behaviour: equal keys land in one slot despite different times
        final Map<TimedFDate, String> map = ILockCollectionFactory.getInstance(false).newMap();
        map.put(a, "hit");
        Assertions.assertThat(map.get(b)).isEqualTo("hit");
        Assertions.assertThat(map.size()).isEqualTo(1);
    }

    @Test
    public void testCrossTypeEqualsWithRawValueAgreesOnHash() {
        final FDate value = new FDate(2000000000L);
        final TimedFDate a = new TimedFDate(new FDate(111L), value);
        Assertions.assertThat(a.equals(value)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(value.hashCode());
    }

    @Test
    public void testDifferentValuesAreUnequal() {
        final TimedFDate a = new TimedFDate(new FDate(111L), new FDate(1000000000L));
        final TimedFDate b = new TimedFDate(new FDate(111L), new FDate(2000000000L));
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

}
