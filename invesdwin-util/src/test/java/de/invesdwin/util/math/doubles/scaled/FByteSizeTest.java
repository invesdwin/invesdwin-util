package de.invesdwin.util.math.doubles.scaled;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.doubles.FDouble;

@ThreadSafe
public class FByteSizeTest {

    @Test
    public void test() {
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).getValue(FByteSizeScale.BYTES)).isEqualTo(1D);
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).getValue(FByteSizeScale.KILOBYTES))
                .isEqualTo(new Decimal("0.001"));
        Assertions
                .assertThat(FDouble.valueOf(new FByteSize(1D, FByteSizeScale.BYTES).getValue(FByteSizeScale.MEGABYTES)))
                .isEqualTo(new Decimal("0.000001").toString());
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).getValue(FByteSizeScale.MEGABYTES))
                .isEqualTo(new Decimal("0.000001"));

        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.KILOBYTES).getValue(FByteSizeScale.KILOBYTES))
                .isEqualTo(1D);
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.KILOBYTES).getValue(FByteSizeScale.MEGABYTES))
                .isEqualTo(new Decimal("0.001"));
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.KILOBYTES).getValue(FByteSizeScale.BYTES))
                .isEqualTo(new Decimal("1000"));

        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.MEGABYTES).getValue(FByteSizeScale.MEGABYTES))
                .isEqualTo(1D);
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.MEGABYTES).getValue(FByteSizeScale.KILOBYTES))
                .isEqualTo(new Decimal("1000"));
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.MEGABYTES).getValue(FByteSizeScale.BYTES))
                .isEqualTo(new Decimal("1000000"));

        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).toString(FByteSizeScale.BYTES, true))
                .isEqualTo("1B");
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).toString(FByteSizeScale.KILOBYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "001KB");
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).toString(FByteSizeScale.MEGABYTES, true))
                .isEqualTo("0" + DecimalFormatSymbols.getInstance(Locale.ENGLISH).getDecimalSeparator() + "000001MB");
        Assertions.assertThat(new FByteSize(1D, FByteSizeScale.BYTES).toString(FByteSizeScale.TERABYTES, true))
                .isEqualTo("0TB");
    }
}
