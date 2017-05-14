package de.invesdwin.util.math;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AShortsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastShorts;
import de.invesdwin.util.math.internal.CheckedCastShortsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AShortsStaticFacade", targets = {
        CheckedCastShorts.class, CheckedCastShortsObj.class, com.google.common.primitives.Shorts.class })
@Immutable
public final class Shorts extends AShortsStaticFacade {

    public static final ADelegateComparator<Short> COMPARATOR = new ADelegateComparator<Short>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Short e) {
            return e;
        }
    };

    private Shorts() {}

    public static short[] toArray(final Collection<Short> collection) {
        if (collection == null) {
            return null;
        }
        return AShortsStaticFacade.toArray(collection);
    }

    public static java.util.List<java.lang.Short> asList(final short... backingArray) {
        if (backingArray == null) {
            return null;
        } else {
            return AShortsStaticFacade.asList(backingArray);
        }
    }

    public static Short min(final Short... values) {
        Short minValue = null;
        for (final Short value : values) {
            minValue = min(minValue, value);
        }
        return minValue;
    }

    public static Short min(final Short value1, final Short value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        }

        if (value1 < value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static Short max(final Short... values) {
        Short maxValue = null;
        for (final Short value : values) {
            maxValue = max(maxValue, value);
        }
        return maxValue;
    }

    public static Short max(final Short value1, final Short value2) {
        if (value1 == null) {
            return value2;
        } else if (value2 == null) {
            return value1;
        }

        if (value1 > value2) {
            return value1;
        } else {
            return value2;
        }
    }

    public static Short between(final Short value, final Short min, final Short max) {
        return max(min(value, max), min);
    }

}
