package de.invesdwin.util.collections;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AIteratorsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AIteratorsStaticFacade", targets = {
        org.apache.commons.collections4.IteratorUtils.class,
        com.google.common.collect.Iterators.class }, filterSeeMethodSignatures = {
                "com.google.common.collect.Iterators#singletonIterator(T)",
                "com.google.common.collect.Iterators#unmodifiableIterator(java.util.Iterator)",
                "com.google.common.collect.Iterators#contains(java.util.Iterator, java.lang.Object)",
                "com.google.common.collect.Iterators#toString(java.util.Iterator)",
                "com.google.common.collect.Iterators#toArray(java.util.Iterator, java.lang.Class)",
                "com.google.common.collect.Iterators#get(java.util.Iterator, int)",
                "com.google.common.collect.Iterators#asEnumeration(java.util.Iterator)",
                "org.apache.commons.collections4.IteratorUtils#peekingIterator(java.util.Iterator)" })
public final class Iterators extends AIteratorsStaticFacade {

    private Iterators() {
    }

}