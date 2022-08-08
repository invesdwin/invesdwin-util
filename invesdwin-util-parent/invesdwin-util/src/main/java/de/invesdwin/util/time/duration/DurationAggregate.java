package de.invesdwin.util.time.duration;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.stream.number.NumberStreamAvg;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
class DurationAggregate implements IDurationAggregate {

    private final List<? extends Duration> values;

    DurationAggregate(final List<? extends Duration> values) {
        this.values = values;
    }

    @Override
    public IDurationAggregate reverse() {
        return new DurationAggregate(Lists.reverse(values));
    }

    /**
     * Returns a weighted average where the first value has the least weight and the last value has the highest weight.
     */
    @Override
    public Duration avgWeightedAsc() {
        return reverse().avgWeightedDesc();
    }

    /**
     * Returns a weighted average where the first value has the highest weight and the last value has the least weight.
     */
    @Override
    public Duration avgWeightedDesc() {
        long sumOfWeights = 0;
        Decimal sumOfWeightedValues = Decimal.ZERO;
        for (int i = 0, weight = count(); i < count(); i++, weight--) {
            final Decimal weightedValue = new Decimal(values.get(i).decimalValue(FTimeUnit.NANOSECONDS))
                    .multiply(weight);
            sumOfWeights += weight;
            sumOfWeightedValues = sumOfWeightedValues.add(weightedValue);
        }
        return new Duration(sumOfWeightedValues.divide(sumOfWeights).longValue(), FTimeUnit.NANOSECONDS);
    }

    @Override
    public Duration sum() {
        Duration sum = Duration.ZERO;
        for (final Duration value : values) {
            if (value != null) {
                sum = sum.add(value);
            }
        }
        return sum;
    }

    /**
     * x_quer = (x_1 + x_2 + ... + x_n) / n
     * 
     * @see <a href="http://de.wikipedia.org/wiki/Arithmetisches_Mittel">Source</a>
     */
    @Override
    public Duration avg() {
        final NumberStreamAvg<Double> avg = new NumberStreamAvg<>();
        for (final Duration value : values) {
            avg.process(value.doubleValue(FTimeUnit.NANOSECONDS));
        }
        return new Duration(Longs.checkedCast(avg.getAvg()), FTimeUnit.NANOSECONDS);
    }

    @Override
    public Duration max() {
        Duration highest = null;
        for (final Duration value : values) {
            if (highest == null) {
                highest = value;
            } else if (value == null) {
                continue;
            } else if (highest.compareTo(value) < 0) {
                highest = value;
            }
        }
        return highest;
    }

    @Override
    public Duration min() {
        Duration lowest = null;
        for (final Duration value : values) {
            if (lowest == null) {
                lowest = value;
            } else if (value == null) {
                continue;
            } else if (value.compareTo(lowest) < 0) {
                lowest = value;
            }
        }
        return lowest;
    }

    @Override
    public List<? extends Duration> values() {
        return Collections.unmodifiableList(values);
    }

    public int count() {
        return values.size();
    }

    @Override
    public String toString() {
        return values.toString();
    }

}
