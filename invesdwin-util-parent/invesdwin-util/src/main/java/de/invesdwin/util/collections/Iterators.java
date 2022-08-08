package de.invesdwin.util.collections;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AIteratorsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AIteratorsStaticFacade", targets = {
        org.apache.commons.collections4.IteratorUtils.class,
        com.google.common.collect.Iterators.class }, filterSeeMethodSignatures = {
                "com.google.common.collect.Iterators#singletonIterator(T)" })
public final class Iterators extends AIteratorsStaticFacade {

    private Iterators() {
    }

}