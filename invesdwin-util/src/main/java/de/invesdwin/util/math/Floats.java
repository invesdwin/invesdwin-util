package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.AFloatsStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastFloats;
import de.invesdwin.util.math.internal.CheckedCastFloatsObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.AFloatsStaticFacade", targets = {
        CheckedCastFloats.class, CheckedCastFloatsObj.class, com.google.common.primitives.Floats.class })
@Immutable
public final class Floats extends AFloatsStaticFacade {

    public static final ADelegateComparator<Float> COMPARATOR = new ADelegateComparator<Float>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Float e) {
            return e;
        }
    };

    private Floats() {}

    public static Float max(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Float min(final Float first, final Float second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    public static Float between(final Float value, final Float min, final Float max) {
        return max(min(value, max), min);
    }

}
