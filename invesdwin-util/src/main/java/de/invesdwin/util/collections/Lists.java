package de.invesdwin.util.collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AListsStaticFacade;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.internal.AListsStaticFacade", targets = {
        com.google.common.collect.Lists.class, org.apache.commons.collections.ListUtils.class })
public final class Lists extends AListsStaticFacade {

    private Lists() {}

    public static <T> List<T> join(final Collection<? extends Collection<T>> lists) {
        final List<T> result = new ArrayList<T>();
        for (final Collection<T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    @SafeVarargs
    public static <T> List<T> join(final Collection<T>... lists) {
        return join(Arrays.asList(lists));
    }

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterator<E> iterator) {
        final List<E> list = new ArrayList<E>();
        try {
            while (true) {
                list.add(iterator.next());
            }
        } catch (final NoSuchElementException e) {
            try {
                iterator.close();
            } catch (final IOException e1) {
                throw new RuntimeException(e);
            }
            return list;
        }
    }

    public static <E> List<E> toList(final ICloseableIterator<E> iterator) {
        final List<E> list = new ArrayList<E>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        try {
            iterator.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}