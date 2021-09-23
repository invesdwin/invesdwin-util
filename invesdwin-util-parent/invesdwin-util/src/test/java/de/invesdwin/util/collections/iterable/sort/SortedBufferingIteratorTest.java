package de.invesdwin.util.collections.iterable.sort;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.list.Lists;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;

@NotThreadSafe
public class SortedBufferingIteratorTest {

    @Test
    public void test() {
        final List<FDate> wrongOrder = new ArrayList<>();
        wrongOrder.add(FDateBuilder.newDate(2000));
        wrongOrder.add(FDateBuilder.newDate(2001));
        wrongOrder.add(FDateBuilder.newDate(1999));
        wrongOrder.add(FDateBuilder.newDate(2003));
        wrongOrder.add(FDateBuilder.newDate(2002));
        final List<FDate> correctOrder = new ArrayList<>(wrongOrder);
        FDate.COMPARATOR.sort(correctOrder, true);

        final List<FDate> sortedOrder = Lists.toListWithoutHasNext(new SortedBufferingIterable<FDate>(
                WrapperCloseableIterable.maybeWrap(wrongOrder), FDate.COMPARATOR, 3));

        for (int i = 0; i < correctOrder.size(); i++) {
            Assertions.checkEquals(correctOrder.get(i), sortedOrder.get(i));
        }

    }

}
