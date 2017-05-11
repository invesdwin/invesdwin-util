package de.invesdwin.util.math;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.math.internal.ABooleansStaticFacade;

@StaticFacadeDefinition(name = "de.invesdwin.util.math.internal.ABooleansStaticFacade", targets = {
        com.google.common.primitives.Booleans.class })
@Immutable
public final class Booleans extends ABooleansStaticFacade {

    public static final ADelegateComparator<Boolean> COMPARATOR = new ADelegateComparator<Boolean>() {
        @Override
        protected Comparable<?> getCompareCriteria(final Boolean e) {
            return e;
        }
    };

    private Booleans() {}

}
