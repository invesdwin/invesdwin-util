package de.invesdwin.util.math.statistics.distribution;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

// CHECKSTYLE:OFF
@NotThreadSafe
public class AZScoreComparatorTest {
    //CHECKSTYLE:ON

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory
            .getXLogger(AZScoreComparatorTest.class);

    @Test
    public void testNewZScore() {
        final int count1 = 200;
        final int count2 = 200;
        final double mean1 = 1191.544;
        final double mean2 = 921.910;
        final double stdev1 = 514.988;
        final double stdev2 = 522.315;
        final double zScore = AZScoreComparator.newZScore(count1, count2, mean1, mean2, stdev1, stdev2);
        final Percent confidence = AZScoreComparator.newProbabilityOfDifference(zScore);
        //CHECKSTYLE:OFF
        LOG.info("zScore={} confidence={}", zScore, confidence.toString(PercentScale.PERCENT));
        //CHECKSTYLE:ON
    }

}
