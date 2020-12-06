package de.invesdwin.util.math.stream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.stream.decimal.DecimalStreamStdev;
import de.invesdwin.util.math.stream.doubl.DoubleStreamStdev;
import de.invesdwin.util.math.stream.number.NumberStreamStdev;

@Immutable
public enum StandardDeviationType {
    /**
     * Warning: normally one will use the sampleStandardDeciation since it is hard to come by a complete set of values
     * representing the distribution of reality
     */
    @Deprecated
    Population {
        @Override
        public double get(final DoubleStreamStdev stream) {
            return stream.getStandardDeviation();
        }

        @Override
        public double get(final NumberStreamStdev<?> stream) {
            return stream.getStandardDeviation();
        }

        @Override
        public <T extends ADecimal<T>> T get(final DecimalStreamStdev<T> stream) {
            return stream.getStandardDeviation();
        }

        @Override
        public int adjustSize(final int size) {
            return size;
        }
    },
    Sample {
        @Override
        public double get(final DoubleStreamStdev stream) {
            return stream.getSampleStandardDeviation();
        }

        @Override
        public double get(final NumberStreamStdev<?> stream) {
            return stream.getSampleStandardDeviation();
        }

        @Override
        public <T extends ADecimal<T>> T get(final DecimalStreamStdev<T> stream) {
            return stream.getSampleStandardDeviation();
        }

        @Override
        public int adjustSize(final int size) {
            return size - 1;
        }
    };

    public abstract double get(DoubleStreamStdev stream);

    public abstract double get(NumberStreamStdev<?> stream);

    public abstract <T extends ADecimal<T>> T get(DecimalStreamStdev<T> stream);

    public abstract int adjustSize(int size);
}
