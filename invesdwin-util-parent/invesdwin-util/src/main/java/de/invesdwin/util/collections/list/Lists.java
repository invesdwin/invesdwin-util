package de.invesdwin.util.collections.list;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.fast.IFastIterableList;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.list.internal.AListsStaticFacade;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.date.BisectDuplicateKeyHandling;
import de.invesdwin.util.time.date.FDates;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.collections.list.internal.AListsStaticFacade", targets = {
        org.apache.commons.collections4.ListUtils.class,
        com.google.common.collect.Lists.class }, filterSeeMethodSignatures = {
                "com.google.common.collect.Lists#partition(java.util.List, int)" })
public final class Lists extends AListsStaticFacade {

    public static final MethodHandle ARRAYLIST_REMOVERANGE_MH;

    static {
        final Method removeRange = Reflections.findMethod(ArrayList.class, "removeRange", int.class, int.class);
        Reflections.makeAccessible(removeRange);
        try {
            ARRAYLIST_REMOVERANGE_MH = MethodHandles.lookup().unreflect(removeRange);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Lists() {}

    public static int maybeTrimSizeStart(final List<?> list, final int maxSize) {
        if (list.size() > maxSize) {
            final int tooMany = list.size() - maxSize;
            return removeRange(list, 0, tooMany);
        } else {
            return 0;
        }
    }

    public static int removeRange(final List<?> list, final int fromIndexInclusive, final int toIndexExclusive) {
        if (fromIndexInclusive == toIndexExclusive) {
            return 0;
        }
        if (list instanceof IFastIterableList) {
            final IFastIterableList<?> cList = (IFastIterableList<?>) list;
            return cList.removeRange(fromIndexInclusive, toIndexExclusive);
        } else if (list instanceof ArrayList) {
            try {
                ARRAYLIST_REMOVERANGE_MH.invoke(list, fromIndexInclusive, toIndexExclusive);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            if (fromIndexInclusive > toIndexExclusive) {
                throw new IndexOutOfBoundsException(
                        "From Index: " + fromIndexInclusive + " > To Index: " + toIndexExclusive);
            }
            final int index = fromIndexInclusive;
            for (int i = fromIndexInclusive; i < toIndexExclusive; i++) {
                list.remove(index);
            }
        }
        return toIndexExclusive - fromIndexInclusive;
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

    @SafeVarargs
    public static <T> boolean containsAny(final List<T> list, final T... elements) {
        if (list instanceof AbstractSequentialList) {
            for (final T v : list) {
                if (Arrays.contains(elements, v)) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                final T v = list.get(i);
                if (Arrays.contains(elements, v)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tells at which index to insert an item using list.add(index, item) to keep the list sorted
     */
    public static <T> int bisectForAdd(final List<T> values, final IComparator<T> comparator,
            final T skippingKeysAbove) {
        if (values.isEmpty()) {
            return 0;
        }
        int lo = 0;
        if (comparator.compareTyped(values.get(lo), skippingKeysAbove) > 0) {
            return lo;
        }
        int hi = values.size();
        if (comparator.compareTyped(values.get(hi - 1), skippingKeysAbove) <= 0) {
            return hi;
        }
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            if (comparator.compareTyped(values.get(mid), skippingKeysAbove) > 0) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    public static <T> int bisect(final List<T> values, final IComparator<T> comparator, final T skippingKeysAbove,
            final BisectDuplicateKeyHandling duplicateKeyHandling) {
        if (values.isEmpty()) {
            return FDates.MISSING_INDEX;
        }
        int lo = 0;
        final T firstKey = values.get(lo);
        if (comparator.compareTyped(firstKey, skippingKeysAbove) >= 0) {
            return duplicateKeyHandling.apply(values, comparator, lo, firstKey);
        }
        int hi = values.size();
        final int lastIndex = hi - 1;
        final T lastKey = values.get(lastIndex);
        if (comparator.compareTyped(lastKey, skippingKeysAbove) <= 0) {
            return duplicateKeyHandling.apply(values, comparator, lastIndex, lastKey);
        }
        while (lo < hi) {
            // same as (low+high)/2
            final int mid = (lo + hi) >>> 1;
            //if (x < list.get(mid)) {
            final T midKey = values.get(mid);
            final int compareTo = comparator.compareTyped(midKey, skippingKeysAbove);
            switch (compareTo) {
            case FDates.MISSING_INDEX:
                lo = mid + 1;
                break;
            case 0:
                return duplicateKeyHandling.apply(values, comparator, mid, midKey);
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        if (lo <= 0) {
            return 0;
        }
        if (lo >= values.size()) {
            lo = lo - 1;
        }
        final T loKey = values.get(lo);
        if (comparator.compareTypedNotNullSafe(loKey, skippingKeysAbove) > 0) {
            //no duplicate key handling needed because this is the last value before the actual requested key
            final int index = lo - 1;
            return index;
        } else {
            return duplicateKeyHandling.apply(values, comparator, lo, loKey);
        }
    }

}