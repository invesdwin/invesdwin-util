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
        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.BYTES).getValue(ByteSizeScale.BYTES)).isEqualTo(1D);
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.BYTES).getValue(ByteSizeScale.KILOBYTES)))
                .isEqualTo(new Decimal("0.0009765625"));

        Assertions
                .assertThat(Decimal.valueOf(new ByteSize(1D, ByteSizeScale.BYTES).getValue(ByteSizeScale.MEGABYTES))
                        .toString())
                .isEqualTo(new Decimal("0.00000095367431640625").toString());
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.BYTES).getValue(ByteSizeScale.MEGABYTES)))
                .isEqualTo(new Decimal("0.00000095367431640625"));

        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.KILOBYTES))
                .isEqualTo(1D);
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.MEGABYTES)))
                .isEqualTo(new Decimal("0.0009765625"));
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.KILOBYTES).getValue(ByteSizeScale.BYTES)))
                .isEqualTo(new Decimal("1024"));

        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.MEGABYTES))
                .isEqualTo(1D);
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.KILOBYTES)))
                .isEqualTo(new Decimal("1024"));
        Assertions.assertThat(new Decimal(new ByteSize(1D, ByteSizeScale.MEGABYTES).getValue(ByteSizeScale.BYTES)))
                .isEqualTo(new Decimal("1048576"));

        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.BYTES).toString(ByteSizeScale.BYTES, true))
                .isEqualTo("1B");
        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.BYTES).toString(ByteSizeScale.KILOBYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "001KB");
        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.BYTES).toString(ByteSizeScale.MEGABYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "000001MB");
        Assertions.assertThat(new ByteSize(1D, ByteSizeScale.BYTES).toString(ByteSizeScale.TERABYTES, true))
                .isEqualTo("0.000000000001TB");
    }
}
