package de.invesdwin.util.math.stream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.decimal.DecimalStreamStandardDeviation;
import de.invesdwin.util.math.stream.doubl.DoubleStreamStandardDeviation;
import de.invesdwin.util.math.stream.number.NumberStreamStandardDeviation;

@Immutable
public enum StandardDeviationType {
    /**
     * Warning: normally one will use the sampleStandardDeciation since it is hard to come by a complete set of values
     * representing the distribution of reality
     */
    @Deprecated
    Population {
        @Override
        public double get(final DoubleStreamStandardDeviation stream) {
            return stream.getStandardDeviation();
        }

        @Override
        public double get(final NumberStreamStandardDeviation<?> stream) {
            return stream.getStandardDeviation();
        }

        @Override
        public <T extends ADecimal<T>> T get(final DecimalStreamStandardDeviation<T> stream) {
            return stream.getStandardDeviation();
        }
    },
    Sample {
        @Override
        public double get(final DoubleStreamStandardDeviation stream) {
            return stream.getSampleStandardDeviation();
        }

        @Override
        public double get(final NumberStreamStandardDeviation<?> stream) {
            return stream.getSampleStandardDeviation();
        }

        @Override
        public <T extends ADecimal<T>> T get(final DecimalStreamStandardDeviation<T> stream) {
            return stream.getSampleStandardDeviation();
        }
    };

    public abstract double get(DoubleStreamStandardDeviation stream);

    public abstract double get(NumberStreamStandardDeviation<?> stream);

    public abstract <T extends ADecimal<T>> T get(DecimalStreamStandardDeviation<T> stream);
}
