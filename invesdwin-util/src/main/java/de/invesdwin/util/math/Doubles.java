package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ADoublesStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ADoublesStaticFacade", targets = {
        com.google.common.primitives.Doubles.class })
@Immutable
public final class Doubles extends ADoublesStaticFacade {

    public static final ADelegateComparator<Double> COMPARATOR = new ADelegateComparator<Double>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Double e) {
            return e;
        }
    };

    private Doubles() {}

    public static Double max(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.max(first, second);
        }
    }

    public static Double min(final Double first, final Double second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

}
