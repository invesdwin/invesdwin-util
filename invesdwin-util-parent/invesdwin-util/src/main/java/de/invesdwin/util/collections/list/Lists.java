package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.list.internal.AListsStaticFacade;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.list.internal.AListsStaticFacade", targets = {
        com.google.common.collect.Lists.class, org.apache.commons.collections4.ListUtils.class })
public final class Lists extends AListsStaticFacade {

    private Lists() {
    }

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

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final ICloseableIterator<? extends E> iterator, final List<E> list) {
        if (iterator instanceof IFastToListProvider) {
            final IFastToListProvider<E> cIterator = (IFastToListProvider<E>) iterator;
            return cIterator.toList(list);
        } else {
            try {
                while (true) {
                    final E next = iterator.next();
                    if (next == null) {
                        throw new NullPointerException("null");
                    }
                    list.add(next);
                }
            } catch (final NoSuchElementException e) {
                return list;
            } finally {
                iterator.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final ICloseableIterable<? extends E> iterable, final List<E> list) {
        final Iterable<E> unwrapped = WrapperCloseableIterable.maybeUnwrap(iterable);
        if (unwrapped instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) unwrapped;
            return cUnwrapped.toList(list);
        } else if (unwrapped instanceof Collection) {
            list.addAll((Collection<E>) unwrapped);
            return list;
        } else {
            return toListWithoutHasNext(iterable.iterator(), list);
        }
    }

    public static <E> List<E> toListWithoutHasNext(final ICloseableIterator<? extends E> iterator) {
        return toListWithoutHasNext(iterator, new ArrayList<E>());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final ICloseableIterable<? extends E> iterable) {
        final Iterable<E> unwrapped = WrapperCloseableIterable.maybeUnwrap(iterable);
        if (unwrapped instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) unwrapped;
            return cUnwrapped.toList();
        } else if (unwrapped instanceof List) {
            return (List<E>) unwrapped;
        } else if (unwrapped instanceof Collection) {
            return new ArrayList<E>((Collection<E>) unwrapped);
        }
        final ICloseableIterator<? extends E> iterator;
        try {
            iterator = iterable.iterator();
        } catch (final NoSuchElementException e) {
            return new ArrayList<>();
        }
        return toListWithoutHasNext(iterator);
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final ICloseableIterator<? extends E> iterator, final List<E> list) {
        if (iterator instanceof IFastToListProvider) {
            final IFastToListProvider<E> cIterator = (IFastToListProvider<E>) iterator;
            return cIterator.toList(list);
        } else {
            try {
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            } finally {
                iterator.close();
            }
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final ICloseableIterable<? extends E> iterable, final List<E> list) {
        final Iterable<E> unwrapped = WrapperCloseableIterable.maybeUnwrap(iterable);
        if (unwrapped instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) unwrapped;
            return cUnwrapped.toList(list);
        } else if (unwrapped instanceof Collection) {
            list.addAll((Collection<E>) unwrapped);
            return list;
        } else {
            final ICloseableIterator<? extends E> iterator;
            try {
                iterator = iterable.iterator();
            } catch (final NoSuchElementException e) {
                return new ArrayList<>();
            }
            return toList(iterator, list);
        }
    }

    public static <E> List<E> toList(final ICloseableIterator<? extends E> iterator) {
        return toList(iterator, new ArrayList<E>());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final ICloseableIterable<? extends E> iterable) {
        final Iterable<E> unwrapped = WrapperCloseableIterable.maybeUnwrap(iterable);
        if (unwrapped instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) unwrapped;
            return cUnwrapped.toList();
        } else if (unwrapped instanceof List) {
            return (List<E>) unwrapped;
        } else if (unwrapped instanceof Collection) {
            return new ArrayList<E>((Collection<E>) unwrapped);
        }
        return toList(iterable.iterator());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final Iterable<? extends E> c) {
        if (c instanceof ICloseableIterable) {
            return toList((ICloseableIterable<E>) c);
        } else if (c instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) c;
            return cUnwrapped.toList();
        } else if (c instanceof List) {
            return (List<E>) c;
        } else if (c instanceof Collection) {
            return new ArrayList<E>((Collection<E>) c);
        }
        return toList(c.iterator());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final Iterable<? extends E> c, final List<E> list) {
        if (c instanceof ICloseableIterable) {
            return toList((ICloseableIterable<E>) c, list);
        } else if (c instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) c;
            return cUnwrapped.toList(list);
        } else if (c instanceof Collection) {
            list.addAll((Collection<E>) c);
            return list;
        }
        return toList(c.iterator(), list);
    }

    public static <E> List<E> toList(final Iterator<? extends E> iterator) {
        return toList(iterator, new ArrayList<E>());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(final Iterator<? extends E> iterator, final List<E> list) {
        if (iterator instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) iterator;
            return cUnwrapped.toList(list);
        } else {
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final Iterable<? extends E> c) {
        if (c instanceof ICloseableIterable) {
            return toListWithoutHasNext((ICloseableIterable<E>) c);
        } else if (c instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) c;
            return cUnwrapped.toList();
        } else if (c instanceof List) {
            return (List<E>) c;
        } else if (c instanceof Collection) {
            return new ArrayList<E>((Collection<E>) c);
        }
        return toListWithoutHasNext(c.iterator());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final Iterable<? extends E> c, final List<E> list) {
        if (c instanceof ICloseableIterable) {
            return toListWithoutHasNext((ICloseableIterable<E>) c, list);
        } else if (c instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) c;
            return cUnwrapped.toList(list);
        } else if (c instanceof Collection) {
            list.addAll((Collection<E>) c);
            return list;
        }
        return toList(c.iterator(), list);
    }

    public static <E> List<E> toListWithoutHasNext(final Iterator<? extends E> iterator) {
        return toListWithoutHasNext(iterator, new ArrayList<E>());
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> toListWithoutHasNext(final Iterator<? extends E> iterator, final List<E> list) {
        if (iterator instanceof IFastToListProvider) {
            final IFastToListProvider<E> cUnwrapped = (IFastToListProvider<E>) iterator;
            return cUnwrapped.toList(list);
        } else {
            try {
                while (true) {
                    final E next = iterator.next();
                    if (next == null) {
                        throw new IllegalArgumentException("null");
                    }
                    list.add(next);
                }
            } catch (final NoSuchElementException e) {
                return list;
            }
        }
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
        if (packageCount <= 0) {
            throw new IllegalArgumentException("packageCount needs to be at least 1: " + packageCount);
        }
        final List<List<E>> packages = new ArrayList<List<E>>();
        if (packageCount == 1) {
            packages.add(list);
            return packages;
        }
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

    public static int reverseIndex(final int i, final int size) {
        return size - 1 - i;
    }

    public static <T> int indexOfIdentity(final List<T> list, final Object item) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * https://stackoverflow.com/questions/16764007/insert-into-an-already-sorted-list/16764413
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void insertSorted(final List<T> list, final T item, final Comparator comparator) {
        int index = Collections.binarySearch(list, item, comparator);
        if (index < 0) {
            index = -index - 1;
        }
        list.add(index, item);
    }

    public static <T> List<T> singletonListNullable(final T value) {
        if (value == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(value);
        }
    }

}