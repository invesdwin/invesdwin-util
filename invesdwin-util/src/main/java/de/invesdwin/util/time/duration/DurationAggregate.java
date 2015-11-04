package de.invesdwin.util.time.duration;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.Lists;

@Immutable
class DurationAggregate implements IDurationAggregate {

    private Duration converter;
    private final List<? extends Duration> values;

    DurationAggregate(final List<? extends Duration> values) {
        this.values = values;
    }

    private Duration getConverter() {
        if (converter == null) {
            for (final Duration scaledValue : values) {
                if (scaledValue != null) {
                    converter = scaledValue;
                    break;
                }
            }
        }
        return converter;

    }

    @Override
    public IDurationAggregate reverse() {
        return new DurationAggregate(Lists.reverse(values));
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
        return sum().divide(values.size());
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

    @Override
    public String toString() {
        return values.toString();
    }

}
