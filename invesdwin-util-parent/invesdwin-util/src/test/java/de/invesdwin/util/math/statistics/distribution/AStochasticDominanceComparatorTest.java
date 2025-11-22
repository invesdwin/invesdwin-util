package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF
@NotThreadSafe
public class AStochasticDominanceComparatorTest {
    //CHECKSTYLE:ON

    //    > y = 2:4
    //    > x = 1:3
    //    > w1=wtdpapb(x, y)
    //    > w1
    //    $wpa
    //    [1] 0.05555556 0.05555556 0.00000000 0.05555556 0.00000000 0.00000000
    //    $wpb
    //    [1] 0.00000000 0.00000000 0.05555556 0.00000000 0.05555556 0.05555556
    //    $dj
    //    [1] 0 1 1 2 2 3
    //    > stochdom2(w1$dj, w1$wpa, w1$wpb)
    //    $sd1b
    //    [1] 0.00000000 0.08333333 0.16666667 0.33333333 0.50000000 0.58333333
    //    $sd2b
    //    [1] 0.00000000 0.04166667 0.16666667 0.66666667 1.50000000 3.12500000
    //    $sd3b
    //    [1]  0.00000000  0.02083333  0.12500000  0.95833333  3.12500000 10.06250000
    //    $sd4b
    //    [1]  0.00000000  0.01041667  0.08333333  1.16666667  5.25000000 25.03125000
    @Test
    public void testCompare() {
        final double sdb = AStochasticDominanceComparator.calculateSd(new double[] { 1, 2, 3 },
                new double[] { 2, 3, 4 }, 2);
        Assertions.assertThat(sdb).isEqualByComparingTo(3.125);
        Assertions.assertThat(AStochasticDominanceComparator.compareSd(sdb)).isEqualByComparingTo(-1);
    }

    //    > x = 2:4
    //    > y = 1:3
    //    > w1=wtdpapb(x, y)
    //    > w1
    //    $wpa
    //    [1] 0.00000000 0.05555556 0.00000000 0.05555556 0.00000000 0.05555556
    //    $wpb
    //    [1] 0.05555556 0.00000000 0.05555556 0.00000000 0.05555556 0.00000000
    //    $dj
    //    [1] 0 1 1 2 2 3
    //    > stochdom2(w1$dj, w1$wpa, w1$wpb)
    //    $sd1b
    //    [1]  0.00000000 -0.02777778 -0.05555556 -0.11111111 -0.16666667 -0.25000000
    //    $sd2b
    //    [1]  0.00000000 -0.01388889 -0.05555556 -0.22222222 -0.50000000 -1.12500000
    //    $sd3b
    //    [1]  0.000000000 -0.006944444 -0.041666667 -0.319444444 -1.041666667 -3.479166667
    //    $sd4b
    //    [1]  0.000000000 -0.003472222 -0.027777778 -0.388888889 -1.750000000 -8.531250000
    @Test
    public void testCompareInverse() {
        final double sdb = AStochasticDominanceComparator.calculateSd(new double[] { 2, 3, 4 },
                new double[] { 1, 2, 3 }, 2);
        Assertions.assertThat(sdb).isEqualByComparingTo(-1.125);
        Assertions.assertThat(AStochasticDominanceComparator.compareSd(sdb)).isEqualByComparingTo(1);
    }

}
