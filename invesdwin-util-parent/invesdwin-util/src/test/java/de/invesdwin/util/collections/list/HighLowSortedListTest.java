package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.comparator.Comparators;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.RandomAdapter;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

@NotThreadSafe
public class HighLowSortedListTest {

    @Test
    public void testAdd() {
        final List<Decimal> sorted = new HighLowSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
        sorted.add(Decimal.TWO);
        sorted.add(Decimal.THREE);
        sorted.add(Decimal.ONE);
        sorted.add(Decimal.TEN);
        sorted.add(Decimal.FIVE);
        sorted.add(Decimal.MINUS_THREE);
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending(), sorted);
    }

    @Test
    public void testAddIndex() {
        final List<Decimal> sorted = new HighLowSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
        sorted.add(0, Decimal.TWO);
        sorted.add(0, Decimal.THREE);
        sorted.add(0, Decimal.ONE);
        sorted.add(0, Decimal.TEN);
        sorted.add(0, Decimal.FIVE);
        sorted.add(0, Decimal.MINUS_THREE);
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
    }

    @Test
    public void testRandomReplay() {
        final List<Decimal> sorted = new HighLowSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
        sorted.add(new Decimal("10"));
        sorted.add(0, new Decimal("5"));
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
    }

    @Test
    public void testRandom() {
        final List<Decimal> original = new ArrayList<Decimal>();
        original.add(Decimal.TWO);
        original.add(Decimal.THREE);
        original.add(Decimal.ONE);
        original.add(Decimal.TEN);
        original.add(Decimal.FIVE);
        original.add(Decimal.MINUS_THREE);

        for (int i = 0; i < 10000; i++) {
            final List<Decimal> input = new ArrayList<Decimal>(original);
            final IRandomGenerator random = PseudoRandomGenerators.newPseudoRandom();
            Collections.shuffle(input, new RandomAdapter(random));
            final List<Decimal> sorted = new HighLowSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
            //            System.out.println("----------------"); //SUPPRESS CHECKSTYLE single line
            for (final Decimal in : input) {
                final boolean add0 = random.nextBoolean();
                if (add0) {
                    sorted.add(0, in);
                    //                    System.out.println("sorted.add(0, new Decimal(\"" + in + "\"));"); //SUPPRESS CHECKSTYLE single line
                } else {
                    sorted.add(in);
                    //                    System.out.println("sorted.add(new Decimal(\"" + in + "\"));"); //SUPPRESS CHECKSTYLE single line
                }
                Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);
            }
        }
    }

}
