package de.invesdwin.util.math.decimal.stream;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class DecimalStreamRelativeDetrendingTest {

    @Test
    public void testDetrendPositiveToNegative() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 100; i < 120; i++) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = detrendRelative(values);
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

    private List<Decimal> detrendRelative(final List<Decimal> values) {
        final DecimalPoint<Decimal, Decimal> from = new DecimalPoint<Decimal, Decimal>(Decimal.ZERO, values.get(0));
        final DecimalPoint<Decimal, Decimal> to = new DecimalPoint<Decimal, Decimal>(new Decimal(values.size()),
                values.get(values.size() - 1));
        final DecimalStreamRelativeDetrending<Decimal> detrending = new DecimalStreamRelativeDetrending<Decimal>(from,
                to);
        final List<Decimal> results = new ArrayList<Decimal>();
        for (int i = 0; i < values.size(); i++) {
            final DecimalPoint<Decimal, Decimal> value = new DecimalPoint<Decimal, Decimal>(new Decimal(i),
                    values.get(i));
            final DecimalPoint<Decimal, Decimal> detrendedValue = detrending.process(value);
            results.add(detrendedValue.getY());
        }
        return results;
    }

    @Test
    public void testDetrendPositive() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 100; i >= 80; i--) {
            values.add(new Decimal(i));
        }
        final List<Decimal> detrended = detrendRelative(values);
        //CHECKSTYLE:OFF
        System.out.println(values + " " + Decimal.valueOf(values).growthRates().avg());
        System.out.println(detrended + " " + Decimal.valueOf(detrended).growthRates().avg());
        //CHECKSTYLE:ON
    }

}
