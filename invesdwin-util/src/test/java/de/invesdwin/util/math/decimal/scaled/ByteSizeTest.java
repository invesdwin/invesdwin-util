package de.invesdwin.util.math.decimal.scaled;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;

@ThreadSafe
public class ByteSizeTest {

    @Test
    public void test() {
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).getValue(ByteSizeScale.BYTES))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).getValue(ByteSizeScale.KILOBYTES))
                .isEqualTo(new Decimal("0.001"));
        Assertions
                .assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).getValue(ByteSizeScale.MEGABYTES).toString())
                .isEqualTo(new Decimal("0.000001").toString());
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).getValue(ByteSizeScale.MEGABYTES))
                .isEqualTo(new Decimal("0.000001"));

        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.KILOBYTES))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.MEGABYTES))
                .isEqualTo(new Decimal("0.001"));
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.BYTES))
                .isEqualTo(new Decimal("1000"));

        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.MEGABYTES))
                .isEqualTo(Decimal.ONE);
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.KILOBYTES))
                .isEqualTo(new Decimal("1000"));
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.BYTES))
                .isEqualTo(new Decimal("1000000"));

        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).toString(ByteSizeScale.BYTES, true))
                .isEqualTo("1B");
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).toString(ByteSizeScale.KILOBYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "001KB");
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).toString(ByteSizeScale.MEGABYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "000001MB");
        Assertions.assertThat(new ByteSize(Decimal.ONE, ByteSizeScale.BYTES).toString(ByteSizeScale.TERABYTES, true))
                .isEqualTo("0TB");
    }
}
