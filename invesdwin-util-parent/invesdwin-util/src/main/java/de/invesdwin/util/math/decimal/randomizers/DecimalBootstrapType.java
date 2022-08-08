package de.invesdwin.util.math.decimal.randomizers;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.random.IRandomGenerator;

@Immutable
public enum DecimalBootstrapType {

    Bootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final IRandomGenerator random) {
            return values.randomize().bootstrap(random);
        }
    },
    CircularBlockBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final IRandomGenerator random) {
            return values.randomize().circularBlockBootstrap(random);
        }
    },
    StationaryBootstrap {
        @Override
        public <T extends ADecimal<T>> Iterator<T> randomize(final IDecimalAggregate<T> values,
                final IRandomGenerator random) {
            return values.randomize().stationaryBootstrap(random);
        }
    };

    public abstract <T extends ADecimal<T>> Iterator<T> randomize(IDecimalAggregate<T> values, IRandomGenerator random);

}
