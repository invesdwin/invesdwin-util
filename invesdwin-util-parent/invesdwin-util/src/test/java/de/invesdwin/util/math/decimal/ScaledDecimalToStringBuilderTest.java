package de.invesdwin.util.math.decimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class ScaledDecimalToStringBuilderTest {

    @Test
    public void testNormalizeNegativeZero() {
        Assertions.assertThat(ScaledDecimalToStringBuilder.normalizeNegativeZero("-0.00000", 0)).isEqualTo("0.00000");
        Assertions.assertThat(ScaledDecimalToStringBuilder.normalizeNegativeZero("-0.00001", 0)).isEqualTo("-0.00001");
        Assertions.assertThat(ScaledDecimalToStringBuilder.normalizeNegativeZero("-0.00000EUR", "EUR".length()))
                .isEqualTo("0.00000EUR");
        Assertions.assertThat(ScaledDecimalToStringBuilder.normalizeNegativeZero("-0.00001EUR", "EUR".length()))
                .isEqualTo("-0.00001EUR");
    }

}
