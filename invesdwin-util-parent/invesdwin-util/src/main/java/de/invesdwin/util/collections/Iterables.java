package de.invesdwin.util.collections;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AIterablesStaticFacade;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AIterablesStaticFacade", targets = {
        org.apache.commons.collections4.IterableUtils.class,
        com.google.common.collect.Iterables.class }, filterSeeMethodSignatures = {
                "com.google.common.collect.Iterables#frequency(java.lang.Iterable, java.lang.Object)",
                "com.google.common.collect.Iterables#unmodifiableIterable(java.lang.Iterable)",
                "com.google.common.collect.Iterables#contains(java.lang.Iterable, java.lang.Object)",
                "com.google.common.collect.Iterables#toString(java.lang.Iterable)",
                "com.google.common.collect.Iterables#get(java.lang.Iterable, int)" })
public final class Iterables extends AIterablesStaticFacade {

    private Iterables() {}

    public static boolean elementsEqual(final Iterable<?> c, final Object obj) {
        if (c == obj) {
            return true;
        } else if (obj instanceof Iterable) {
            final Iterable<?> cObj = (Iterable<?>) obj;
            return elementsEqual(c, cObj);
        } else {
            return false;
        }
    }

    public static <T> String toStringPrefixed(final String prefix, final Iterable<T> iterable) {
        final StringBuilder sb = new StringBuilder();
        try (ICloseableIterator<T> it = WrapperCloseableIterable.maybeWrap(iterable).iterator()) {
            while (true) {
                final T next = it.next();
                sb.append(prefix);
                sb.append(next);
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        return sb.toString();
    }

}