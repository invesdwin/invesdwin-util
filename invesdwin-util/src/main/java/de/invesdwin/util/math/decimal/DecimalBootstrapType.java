package de.invesdwin.util.math.decimal;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

@Immutable
public enum DecimalBootstrapType {

    Bootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomizeBootstrap(random);
        }
    },
    CircularBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomizeCircularBlockBootstrap(random);
        }
    },
    StationaryBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomizeStationaryBootstrap(random);
        }
    };

    public abstract <T extends ADecimal<T>> Iterator<T> randomize(IDecimalAggregate<T> values, RandomGenerator random);

}
