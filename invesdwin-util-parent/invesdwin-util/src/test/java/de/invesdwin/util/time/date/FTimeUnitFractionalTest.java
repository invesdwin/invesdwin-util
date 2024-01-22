package de.invesdwin.util.time.date;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class FTimeUnitFractionalTest {

    @Test
    public void testConvert() {
        int count = 0;
        for (final FTimeUnit from : FTimeUnit.values()) {
            for (final FTimeUnit to : FTimeUnit.values()) {
                count++;
                final long converted = to.convert(1, from);
                final double fractional = to.asFractional().convert(1D, from.asFractional());
                //CHECKSTYLE:OFF
                System.out.println(count + ": " + from + " -> " + to + ": " + converted + " == "
                        + Decimal.toString(fractional) + " => " + (converted == fractional));
                //CHECKSTYLE:ON
            }
        }
    }

}
