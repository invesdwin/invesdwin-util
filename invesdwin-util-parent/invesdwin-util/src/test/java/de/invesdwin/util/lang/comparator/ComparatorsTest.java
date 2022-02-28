package de.invesdwin.util.lang.comparator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class ComparatorsTest {

    @Test
    public void testSort() {
        final List<Decimal> sorted = new ArrayList<>();
        sorted.add(Decimal.TWO);
        sorted.add(Decimal.THREE);
        sorted.add(Decimal.ONE);
        sorted.add(Decimal.TEN);
        sorted.add(Decimal.FIVE);
        sorted.add(Decimal.MINUS_THREE);
        Comparators.sort(sorted, Decimal.COMPARATOR.asNotNullSafe());
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
    }

    @Test
    public void testMergeSort() {
        final List<Decimal> sorted = new ArrayList<>();
        sorted.add(Decimal.TWO);
        sorted.add(Decimal.THREE);
        sorted.add(Decimal.ONE);
        sorted.add(Decimal.TEN);
        sorted.add(Decimal.FIVE);
        sorted.add(Decimal.MINUS_THREE);
        Comparators.mergeSort(sorted, Decimal.COMPARATOR.asNotNullSafe());
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
    }

    @Test
    public void testBubbleSort() {
        final List<Decimal> sorted = new ArrayList<>();
        sorted.add(Decimal.TWO);
        sorted.add(Decimal.THREE);
        sorted.add(Decimal.ONE);
        sorted.add(Decimal.TEN);
        sorted.add(Decimal.FIVE);
        sorted.add(Decimal.MINUS_THREE);
        Comparators.bubbleSort(sorted, Decimal.COMPARATOR.asNotNullSafe());
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
    }

}
