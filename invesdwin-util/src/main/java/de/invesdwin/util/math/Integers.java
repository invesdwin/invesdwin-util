package de.invesdwin.util.math;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AIntegersStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AIntegersStaticFacade", targets = {
        com.google.common.primitives.Ints.class })
@Immutable
public final class Integers extends AIntegersStaticFacade {

    public static final ADelegateComparator<Integer> COMPARATOR = new ADelegateComparator<Integer>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Integer e) {
            return e;
        }
    };

    private Integers() {}

    public static Integer max(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Integer min(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Integer avg(final Integer first, final Integer second) {
        final long sum = (long) first + (long) second;
        return (int) sum / 2;
    }

    public static Integer avg(final Integer... values) {
        long sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return (int) (sum / values.length);
    }

    public static Integer avg(final Collection<Integer> values) {
        long sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return (int) (sum / values.size());
    }

    public static Integer sum(final Collection<Integer> values) {
        int sum = 0;
        for (final Integer value : values) {
            sum += value;
        }
        return sum;
    }

    public static Integer between(final Integer value, final Integer min, final Integer max) {
        return max(min(value, max), min);
    }

    public static Integer checkedCast(final Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long || value instanceof Double || value instanceof Float) {
            final long longValue = value.longValue();
            return checkedCast(longValue);
        } else {
            return value.intValue();
        }
    }

    public static int checkedCast(final long longValue) {
        return Math.toIntExact(longValue);
    }
}
