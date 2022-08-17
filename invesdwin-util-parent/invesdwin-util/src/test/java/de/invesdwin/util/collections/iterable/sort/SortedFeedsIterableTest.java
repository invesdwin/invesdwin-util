package de.invesdwin.util.collections.iterable.sort;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class SortedFeedsIterableTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSortedFeedsAscending() {
        final List<Integer> list1 = Arrays.asList(1, 3, 5);
        final List<Integer> list2 = Arrays.asList(2, 4, 6, 7);
        final List<Integer> expectedResult = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        final SortedFeedsIterable<Integer> iterable = new SortedFeedsIterable<>(Integers.COMPARATOR.asAscending(),
                Arrays.asList(list1, list2));
        final List<Integer> actualResult = Lists.toListWithoutHasNext(iterable);
        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSortedFeedsDescending() {
        final List<Integer> list1 = Arrays.asList(5, 3, 1);
        final List<Integer> list2 = Arrays.asList(7, 6, 4, 2);
        final List<Integer> expectedResult = Arrays.asList(7, 6, 5, 4, 3, 2, 1);
        final SortedFeedsIterable<Integer> iterable = new SortedFeedsIterable<>(Integers.COMPARATOR.asDescending(),
                Arrays.asList(list1, list2));
        final List<Integer> actualResult = Lists.toListWithoutHasNext(iterable);
        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

}
