package de.invesdwin.util.math.decimal.randomizers;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;

@Immutable
public enum DecimalBootstrapType {

    Bootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomize().bootstrap(random);
        }
    },
    CircularBlockBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomize().circularBlockBootstrap(random);
        }
    },
    StationaryBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final RandomGenerator random) {
            return values.randomize().stationaryBootstrap(random);
        }
    };

    public abstract <T extends ADecimal<T>> Iterator<T> randomize(IDecimalAggregate<T> values, RandomGenerator random);

}
