package de.invesdwin.util.math.timed;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;

/**
 * Pins the equals/hashCode contract chosen for TimedBoolean: equals is value-only (time is NOT part of
 * equality, and the wrapper is deliberately equal to the raw Boolean it wraps), so the hash must fold
 * the value ONLY — never time, never the class token.
 */
@NotThreadSafe
public class TimedBooleanTest {

    @Test
    public void testEqualValueDifferentTimeAgreeOnHash() {
        final TimedBoolean a = new TimedBoolean(new FDate(111L), true);
        final TimedBoolean b = new TimedBoolean(new FDate(999L), true);
        Assertions.assertThat(a.equals(b)).isTrue();
        Assertions.assertThat(b.equals(a)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void testCrossTypeEqualsWithRawValueAgreesOnHash() {
        final TimedBoolean a = new TimedBoolean(new FDate(111L), true);
        Assertions.assertThat(a.equals(Boolean.TRUE)).isTrue();
        Assertions.assertThat(a.hashCode()).isEqualTo(Boolean.hashCode(true));
    }

    @Test
    public void testDifferentValuesAreUnequal() {
        final TimedBoolean a = new TimedBoolean(new FDate(111L), true);
        final TimedBoolean b = new TimedBoolean(new FDate(111L), false);
        Assertions.assertThat(a.equals(b)).isFalse();
        Assertions.assertThat(b.equals(a)).isFalse();
    }

}
