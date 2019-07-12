package de.invesdwin.util.math;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class DoublesTest {

    @Test
    public void testCompareNaN() {
        Assertions.assertThat(Doubles.compare(Double.NaN, Double.NaN)).isEqualTo(0);
        Assertions.assertThat(Doubles.compare(1, Double.NaN)).isEqualTo(1);
        Assertions.assertThat(Doubles.compare(-1, Double.NaN)).isEqualTo(1);
        Assertions.assertThat(Doubles.compare(Double.NaN, 1)).isEqualTo(-1);
        Assertions.assertThat(Doubles.compare(Double.NaN, -1)).isEqualTo(-1);
    }

}
