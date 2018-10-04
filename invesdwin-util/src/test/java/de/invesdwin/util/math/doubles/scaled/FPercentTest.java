package de.invesdwin.util.math.doubles.scaled;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class FPercentTest {

    @Test
    public void test() {
        Assertions.assertThat(new FPercent(1D, FPercentScale.RATE).getValue(FPercentScale.RATE)).isEqualTo(1D);
        Assertions.assertThat(new FPercent(1D, FPercentScale.RATE).getValue(FPercentScale.PERCENT))
                .isEqualTo(new Decimal("100"));
        Assertions.assertThat(new FPercent(1D, FPercentScale.RATE).getValue(FPercentScale.PERMILLE))
                .isEqualTo(new Decimal("1000"));

        Assertions.assertThat(new FPercent(1D, FPercentScale.PERCENT).getValue(FPercentScale.PERCENT)).isEqualTo(1D);
        Assertions.assertThat(new FPercent(1D, FPercentScale.PERCENT).getValue(FPercentScale.PERMILLE))
                .isEqualTo(new Decimal("10"));
        Assertions.assertThat(new FPercent(1D, FPercentScale.PERCENT).getValue(FPercentScale.RATE))
                .isEqualTo(new Decimal("0.01"));

        Assertions.assertThat(new FPercent(1D, FPercentScale.PERMILLE).getValue(FPercentScale.PERMILLE)).isEqualTo(1D);
        Assertions.assertThat(new FPercent(1D, FPercentScale.PERMILLE).getValue(FPercentScale.PERCENT))
                .isEqualTo(new Decimal("0.1"));
        Assertions.assertThat(new FPercent(1D, FPercentScale.PERMILLE).getValue(FPercentScale.RATE))
                .isEqualTo(new Decimal("0.001"));

        Assertions.assertThat(new FPercent(1D, FPercentScale.PERMILLE).toString(FPercentScale.PERMILLE))
                .isEqualTo("1" + FPercentScale.PERMILLE.getSymbol());
    }

    @Test
    public void testToString() {
        Assertions.assertThat(new FPercent(0.001D, FPercentScale.PERCENT).toString(FPercentScale.RATE)).isEqualTo("0");
        Assertions.assertThat(new FPercent(0.001D, FPercentScale.PERCENT).toStringBuilder()
                .withScale(FPercentScale.RATE)
                .withDecimalDigits(5)
                .toString()).isEqualTo("0.00001");
        Assertions.assertThat(new FPercent(0.001D, FPercentScale.PERCENT).toString(FPercentScale.PERCENT))
                .isEqualTo("0%");
        Assertions.assertThat(new FPercent(0.001D, FPercentScale.PERCENT).toStringBuilder()
                .withScale(FPercentScale.PERCENT)
                .withDecimalDigits(3)
                .toString()).isEqualTo("0.001%");
        final FPercent permilleValue = new FPercent(0.001D, FPercentScale.PERCENT).asScale(FPercentScale.PERMILLE);
        Assertions.assertThat(permilleValue.toString(FPercentScale.PERMILLE))
                .isEqualTo("0" + FPercentScale.PERMILLE.getSymbol());
        Assertions.assertThat(
                permilleValue.toStringBuilder().withScale(FPercentScale.PERMILLE).withDecimalDigits(2).toString())
                .isEqualTo("0.01" + FPercentScale.PERMILLE.getSymbol());
    }
}
