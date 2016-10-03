package de.invesdwin.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.internal.AListsStaticFacade;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
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

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterator<? extends E> iterator, final List<E> list) {
        try {
            while (true) {
                final E next = iterator.next();
                if (next == null) {
                    throw new IllegalArgumentException("null");
                }
                list.add(next);
            }
        } catch (final NoSuchElementException e) {
            iterator.close();
            return list;
        }
    }

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterable<? extends E> iterable, final List<E> list) {
        return toListWithoutHasNext(iterable.iterator(), list);
    }

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterator<? extends E> iterator) {
        return toListWithoutHasNext(iterator, new ArrayList<E>());
    }

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterable<? extends E> iterable) {
        return toListWithoutHasNext(iterable.iterator());
    }

    public static <E> List<E> toList(final ICloseableIterator<? extends E> iterator, final List<E> list) {
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        iterator.close();
        return list;
    }

    public static <E> List<E> toList(final ICloseableIterable<? extends E> iterable, final List<E> list) {
        return toList(iterable.iterator(), list);
    }

    public static <E> List<E> toList(final ICloseableIterator<? extends E> iterator) {
        return toList(iterator, new ArrayList<E>());
    }

    public static <E> List<E> toList(final ICloseableIterable<? extends E> iterable) {
        return toList(iterable.iterator());
    }

    /**
     * Examples:
     * 
     * null,null -> true
     * 
     * [],null -> true
     * 
     * null,[] -> true
     * 
     * [],[] -> true
     * 
     * [x],[] -> false
     * 
     * null,[x] -> false
     */
    public static boolean equals(final List<?> list1, final List<?> list2) {
        final boolean list1NullOrEmpty = list1 == null || list1.isEmpty();
        final boolean list2NullOrEmpty = list2 == null || list2.isEmpty();
        if (list1NullOrEmpty && list2NullOrEmpty) {
            return true;
        } else {
            return isEqualList(list1, list2);
        }
    }

    public static <E> List<? extends List<E>> splitIntoPackageCount(final List<E> list, final int packageCount) {
        final List<List<E>> packages = new ArrayList<List<E>>();
        final int realPackageCount = Math.min(packageCount, list.size());
        for (int i = 0; i < realPackageCount; i++) {
            packages.add(new ArrayList<E>());
        }
        int curPackage = 0;
        for (final E e : list) {
            packages.get(curPackage).add(e);
            curPackage++;
            if (curPackage >= realPackageCount) {
                curPackage = 0;
            }
        }
        return packages;
    }

    public static <E> E getLastElement(final List<E> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

}