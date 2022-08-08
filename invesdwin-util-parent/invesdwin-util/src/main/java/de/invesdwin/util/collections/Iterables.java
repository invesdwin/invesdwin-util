package de.invesdwin.util.collections;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AIterablesStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AIterablesStaticFacade", targets = {
        org.apache.commons.collections4.IterableUtils.class,
        com.google.common.collect.Iterables.class }, filterSeeMethodSignatures = {
                "com.google.common.collect.Iterables#frequency(java.lang.Iterable, java.lang.Object)",
                "com.google.common.collect.Iterables#unmodifiableIterable(java.lang.Iterable)",
                "com.google.common.collect.Iterables#contains(java.lang.Iterable, java.lang.Object)",
                "com.google.common.collect.Iterables#toString(java.lang.Iterable)" })
public final class Iterables extends AIterablesStaticFacade {

    private Iterables() {
    }

}