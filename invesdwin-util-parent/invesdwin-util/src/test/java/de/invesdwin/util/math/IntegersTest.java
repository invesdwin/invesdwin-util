package de.invesdwin.util.math;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

@NotThreadSafe
public class IntegersTest {

    @Test(expected = ArithmeticException.class)
    public void testDivideByZero() {
        final int division = 5 / 0;
        org.assertj.core.api.Assertions.fail("exception expected: " + division);
    }

    @Test
    public void testOverflowAdd() {
        final int overflow = Integer.MAX_VALUE + 1;
        org.assertj.core.api.Assertions.assertThat(overflow).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testOverflowMultiply() {
        final int overflow = Integer.MAX_VALUE * 2;
        org.assertj.core.api.Assertions.assertThat(overflow).isEqualTo(-2);
    }

}
