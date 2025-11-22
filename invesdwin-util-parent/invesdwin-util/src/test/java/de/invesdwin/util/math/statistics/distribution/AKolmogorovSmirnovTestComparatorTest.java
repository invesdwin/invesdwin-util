package de.invesdwin.util.math.statistics.distribution;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;

// CHECKSTYLE:OFF
@Immutable
public class AKolmogorovSmirnovTestComparatorTest {
    //CHECKSTYLE:ON

    @Test
    public void testSort() {
        final List<double[]> elements = new ArrayList<>();
        elements.add(new double[] { 1.1, 1.2, 1.3 });
        elements.add(new double[] { 12.1, 12.2, 12.3 });
        elements.add(new double[] { 20.1, 20.2, 20.3 });
        final List<double[]> elementsReverse = new ArrayList<>(elements);
        Collections.reverse(elementsReverse);

        new AKolmogorovSmirnovTestComparator<double[]>() {
            @Override
            protected boolean isHigherBetter(final double[] element) {
                return true;
            }

            @Override
            protected double[] getValues(final double[] element) {
                return element;
            }
        }.sort(elements);

        Assertions.assertThat(elements).isEqualTo(elementsReverse);

        new AKolmogorovSmirnovTestComparator<double[]>() {
            @Override
            protected boolean isHigherBetter(final double[] element) {
                return false;
            }

            @Override
            protected double[] getValues(final double[] element) {
                return element;
            }
        }.sort(elements);

        Collections.reverse(elementsReverse);
        Assertions.assertThat(elements).isEqualTo(elementsReverse);
    }

}
