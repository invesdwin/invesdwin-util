package de.invesdwin.util.math.decimal.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

@NotThreadSafe
public class DecimalAggregateRandomizersTest {

    private static final int REPEAT_COUNT = 100;

    @Test
    public void testRandomizeShuffle() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }

        final IDecimalAggregate<Decimal> agg = Decimal.valueOf(values);
        for (int i = 0; i < REPEAT_COUNT; i++) {
            final List<? extends Decimal> resampled = Lists
                    .toList(agg.randomize().shuffle(PseudoRandomGenerators.newPseudoRandom()));
            Assertions.assertThat(values).hasSameSizeAs(resampled);
        }
    }

    @Test
    public void testRandomizeBootstrap() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }

        final IDecimalAggregate<Decimal> agg = Decimal.valueOf(values);
        for (int i = 0; i < REPEAT_COUNT; i++) {
            final List<? extends Decimal> resampled = Lists
                    .toList(agg.randomize().bootstrap(PseudoRandomGenerators.newPseudoRandom()));
            Assertions.assertThat(values).hasSameSizeAs(resampled);
        }
    }

    @Test
    public void testRandomizeCircularBlockBootstrap() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }

        final IDecimalAggregate<Decimal> agg = Decimal.valueOf(values);
        for (int i = 0; i < REPEAT_COUNT; i++) {
            final List<? extends Decimal> resampled = Lists
                    .toList(agg.randomize().circularBlockBootstrap(PseudoRandomGenerators.newPseudoRandom()));
            Assertions.assertThat(values).hasSameSizeAs(resampled);
        }
    }

    @Test
    public void testRandomizeStationaryBootstrap() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }

        final IDecimalAggregate<Decimal> agg = Decimal.valueOf(values);
        for (int i = 0; i < REPEAT_COUNT; i++) {
            final List<? extends Decimal> resampled = Lists
                    .toList(agg.randomize().stationaryBootstrap(PseudoRandomGenerators.newPseudoRandom()));
            Assertions.assertThat(values).hasSameSizeAs(resampled);
        }
    }

}
