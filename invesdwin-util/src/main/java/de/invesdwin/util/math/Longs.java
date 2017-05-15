package de.invesdwin.util.math;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ALongsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastLongs;
import de.invesdwin.util.math.internal.CheckedCastLongsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ALongsStaticFacade", targets = { CheckedCastLongs.class,
        CheckedCastLongsObj.class, com.google.common.primitives.Longs.class })
@Immutable
public final class Longs extends ALongsStaticFacade {

    public static final ADelegateComparator<Long> COMPARATOR = new ADelegateComparator<Long>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Long e) {
            return e;
        }
    };

    private Longs() {}

    public static long[] toArray(final Collection<? extends Number> collection) {
        if (collection == null) {
            return null;
        }
        return ALongsStaticFacade.toArray(collection);
    }

    public static List<Long> asList(final long... backingArray) {
        if (backingArray == null) {
            return null;
        } else {
            return ALongsStaticFacade.asList(backingArray);
        }
    }

    public static Long min(final Long... times) {
        Long minTime = null;
        for (final Long time : times) {
            minTime = min(minTime, time);
        }
        return minTime;
    }

    public static Long min(final Long time1, final Long time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 < time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Long max(final Long... times) {
        Long maxTime = null;
        for (final Long time : times) {
            maxTime = max(maxTime, time);
        }
        return maxTime;
    }

    public static Long max(final Long time1, final Long time2) {
        if (time1 == null) {
            return time2;
        } else if (time2 == null) {
            return time1;
        }

        if (time1 > time2) {
            return time1;
        } else {
            return time2;
        }
    }

    public static Long between(final Long value, final Long min, final Long max) {
        return max(min(value, max), min);
    }

}
