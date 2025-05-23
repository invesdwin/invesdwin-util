package de.invesdwin.util.collections.list;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.comparator.Comparators;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.random.PseudoRandomGenerators;
import de.invesdwin.util.math.random.RandomAdapter;
import de.invesdwin.util.time.date.BisectDuplicateKeyHandling;

@NotThreadSafe
public class BisectSortedListTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Test
    public void testAdd() {
        final BisectSortedList<Decimal> sorted = new BisectSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
        sorted.add(Decimal.TWO);
        sorted.add(Decimal.THREE);
        sorted.add(Decimal.ONE);
        sorted.add(Decimal.TEN);
        sorted.add(Decimal.FIVE);
        sorted.add(Decimal.MINUS_THREE);
        Comparators.assertOrderAll(Decimal.COMPARATOR.asAscending().asNotNullSafe(), sorted);

        Assertions.checkEquals(0, sorted.bisectForAdd(new Decimal(-5D)));
        Assertions.checkEquals(1, sorted.bisectForAdd(Decimal.MINUS_THREE));

        Assertions.checkEquals(sorted.size() - 1, sorted.bisectForAdd(Decimal.FIVE));
        Assertions.checkEquals(sorted.size(), sorted.bisectForAdd(Decimal.TEN));
        Assertions.checkEquals(sorted.size(), sorted.bisectForAdd(new Decimal(20D)));

        Assertions.checkEquals(0, sorted.bisect(new Decimal(-5D), BisectDuplicateKeyHandling.UNDEFINED));
        Assertions.checkEquals(0, sorted.bisect(Decimal.MINUS_THREE, BisectDuplicateKeyHandling.UNDEFINED));

        Assertions.checkEquals(sorted.size() - 2, sorted.bisect(Decimal.FIVE, BisectDuplicateKeyHandling.UNDEFINED));
        Assertions.checkEquals(sorted.size() - 1, sorted.bisect(Decimal.TEN, BisectDuplicateKeyHandling.UNDEFINED));
        Assertions.checkEquals(sorted.size() - 1,
                sorted.bisect(new Decimal(20D), BisectDuplicateKeyHandling.UNDEFINED));
    }

    @Test
    public void testAddIndex() {
        final List<Decimal> sorted = new BisectSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
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
        final List<Decimal> sorted = new BisectSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
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

        final RandomAdapter random = new RandomAdapter(PseudoRandomGenerators.newPseudoRandom());
        for (int i = 0; i < 10000; i++) {
            final List<Decimal> input = new ArrayList<Decimal>(original);
            Collections.shuffle(input, random);
            final BisectSortedList<Decimal> sorted = new BisectSortedList<Decimal>(Decimal.COMPARATOR.asNotNullSafe());
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
                Assertions.checkEquals(sorted.indexOf(in), sorted.bisect(in, BisectDuplicateKeyHandling.LOWEST));
            }
        }
    }

}
