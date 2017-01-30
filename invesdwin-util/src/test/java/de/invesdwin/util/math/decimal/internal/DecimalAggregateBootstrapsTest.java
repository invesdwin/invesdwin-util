package de.invesdwin.util.math.decimal.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Lists;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.random.RandomGenerators;

@NotThreadSafe
public class DecimalAggregateBootstrapsTest {

    private static final int REPEAT_COUNT = 100;

    @Test
    public void testRandomize() {
        final List<Decimal> values = new ArrayList<Decimal>();
        for (int i = 0; i < 100000; i++) {
            values.add(new Decimal(i));
        }

        final IDecimalAggregate<Decimal> agg = Decimal.valueOf(values);
        for (int i = 0; i < REPEAT_COUNT; i++) {
            final List<? extends Decimal> resampled = Lists.toList(agg.randomize(RandomGenerators.newDefaultRandom()));
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
                    .toList(agg.randomizeBootstrap(RandomGenerators.newDefaultRandom()));
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
                    .toList(agg.randomizeCircularBlockBootstrap(RandomGenerators.newDefaultRandom()));
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
                    .toList(agg.randomizeStationaryBootstrap(RandomGenerators.newDefaultRandom()));
            Assertions.assertThat(values).hasSameSizeAs(resampled);
        }
    }

}
