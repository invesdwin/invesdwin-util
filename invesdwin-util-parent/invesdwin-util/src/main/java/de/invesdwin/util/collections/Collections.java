package de.invesdwin.util.collections;

import java.lang.reflect.Array;
import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.ACollectionsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.ACollectionsStaticFacade", targets = {
        java.util.Collections.class }, filterSeeMethodSignatures = { "java.util.Collections#min(java.util.Collection)",
                "java.util.Collections#max(java.util.Collection)" })
public class Collections extends ACollectionsStaticFacade {

    /**
     * https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_introduction
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final Collection<? extends T> col, final Class<T> type) {
        if (col == null) {
            return null;
        } else {
            try {
                final T[] newArray = (T[]) Array.newInstance(type, 0);
                return col.toArray(newArray);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}