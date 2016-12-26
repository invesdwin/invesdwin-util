package de.invesdwin.util.math.decimal.scaled;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class PercentTest {

    @Test
    public void test() {
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.RATE).getValue(PercentScale.RATE))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.RATE).getValue(PercentScale.PERCENT))
                .isEqualTo(new Decimal("100"));
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.RATE).getValue(PercentScale.PERMILLE))
                .isEqualTo(new Decimal("1000"));

        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERCENT).getValue(PercentScale.PERCENT))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERCENT).getValue(PercentScale.PERMILLE))
                .isEqualTo(new Decimal("10"));
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERCENT).getValue(PercentScale.RATE))
                .isEqualTo(new Decimal("0.01"));

        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERMILLE).getValue(PercentScale.PERMILLE))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERMILLE).getValue(PercentScale.PERCENT))
                .isEqualTo(new Decimal("0.1"));
        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERMILLE).getValue(PercentScale.RATE))
                .isEqualTo(new Decimal("0.001"));

        Assertions.assertThat(new Percent(Decimal.ONE, PercentScale.PERMILLE).toString(PercentScale.PERMILLE))
                .isEqualTo("1" + PercentScale.PERMILLE.getSymbol());
    }

    @Test
    public void testToString() {
        Assertions.assertThat(new Percent(new Decimal("0.001"), PercentScale.PERCENT).toString(PercentScale.RATE))
                .isEqualTo("0");
        Assertions.assertThat(new Percent(new Decimal("0.001"), PercentScale.PERCENT).toStringBuilder()
                .withScale(PercentScale.RATE)
                .withDecimalDigits(5)
                .toString()).isEqualTo("0.00001");
        Assertions.assertThat(new Percent(new Decimal("0.001"), PercentScale.PERCENT).toString(PercentScale.PERCENT))
                .isEqualTo("0%");
        Assertions.assertThat(new Percent(new Decimal("0.001"), PercentScale.PERCENT).toStringBuilder()
                .withScale(PercentScale.PERCENT)
                .withDecimalDigits(3)
                .toString()).isEqualTo("0.001%");
        final Percent permilleValue = new Percent(new Decimal("0.001"), PercentScale.PERCENT)
                .asScale(PercentScale.PERMILLE);
        Assertions.assertThat(permilleValue.toString(PercentScale.PERMILLE))
                .isEqualTo("0" + PercentScale.PERMILLE.getSymbol());
        Assertions
                .assertThat(permilleValue.toStringBuilder()
                        .withScale(PercentScale.PERMILLE)
                        .withDecimalDigits(2)
                        .toString())
                .isEqualTo("0.01" + PercentScale.PERMILLE.getSymbol());
    }
}
