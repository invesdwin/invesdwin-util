package de.invesdwin.util.collections;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.ASetsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.ASetsStaticFacade", targets = {
        org.apache.commons.collections4.SetUtils.class, com.google.common.collect.Sets.class })
public final class Sets extends ASetsStaticFacade {

    private Sets() {
    }

    public static boolean equals(final Set<?> set1, final Set<?> set2) {
        final boolean set1NullOrEmpty = set1 == null || set1.isEmpty();
        final boolean set2NullOrEmpty = set2 == null || set2.isEmpty();
        if (set1NullOrEmpty && set2NullOrEmpty) {
            return true;
        } else {
            return isEqualSet(set1, set2);
        }
    }

}