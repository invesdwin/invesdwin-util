package de.invesdwin.util.collections;

import java.util.Collection;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.ACollectionsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.ACollectionsStaticFacade", targets = {
        java.util.Collections.class, org.apache.commons.collections4.CollectionUtils.class,
        com.google.common.collect.Collections2.class }, filterSeeMethodSignatures = {
                "java.util.Collections#min(java.util.Collection)", "java.util.Collections#max(java.util.Collection)",
                "org.apache.commons.collections4.CollectionUtils#addAll(java.util.Collection, C...)",
                "org.apache.commons.collections4.CollectionUtils#synchronizedCollection(java.util.Collection)",
                "org.apache.commons.collections4.CollectionUtils#unmodifiableCollection(java.util.Collection)",
                "java.util.Collections#unmodifiableSequencedCollection(java.util.SequencedCollection)",
                "java.util.Collections#unmodifiableSequencedSet(java.util.SequencedSet)",
                "java.util.Collections#unmodifiableSequencedMap(java.util.SequencedMap)",
                "java.util.Collections#newSequencedSetFromMap(java.util.SequencedMap)",
                "java.util.Collections#shuffle(java.util.List, java.util.random.RandomGenerator)" })
public class Collections extends ACollectionsStaticFacade {

    @SuppressWarnings("rawtypes")
    public static final Entry[] EMPTY_ENTRY_ARRAY = new Entry[0];

    /**
     * https://shipilev.net/blog/2016/arrays-wisdom-ancients/#_introduction
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final Collection<? extends T> col, final Class<T> type) {
        if (col == null) {
            return null;
        } else {
            try {
                final T[] newArray = (T[]) Arrays.newInstance(type, 0);
                return col.toArray(newArray);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String toString(final Collection<?> c) {
        return Iterables.toString(c);
    }

    public static boolean elementsEqual(final Collection<?> c, final Object obj) {
        return Iterables.elementsEqual(c, obj);
    }

}
