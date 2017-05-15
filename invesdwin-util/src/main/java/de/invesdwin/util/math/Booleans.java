package de.invesdwin.util.math;

import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ABooleansStaticFacade;
import de.invesdwin.util.math.internal.CheckedCastBooleans;
import de.invesdwin.util.math.internal.CheckedCastBooleansObj;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABooleansStaticFacade", targets = {
        CheckedCastBooleans.class, CheckedCastBooleansObj.class, com.google.common.primitives.Booleans.class })
@Immutable
public final class Booleans extends ABooleansStaticFacade {

    public static final ADelegateComparator<Boolean> COMPARATOR = new ADelegateComparator<Boolean>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Boolean e) {
            return e;
        }
    };

    private Booleans() {}

    public static boolean[] toArray(final Collection<Boolean> collection) {
        if (collection == null) {
            return null;
        }
        return ABooleansStaticFacade.toArray(collection);
    }

    public static List<Boolean> asList(final boolean... backingArray) {
        if (backingArray == null) {
            return null;
        } else {
            return ABooleansStaticFacade.asList(backingArray);
        }
    }

}
