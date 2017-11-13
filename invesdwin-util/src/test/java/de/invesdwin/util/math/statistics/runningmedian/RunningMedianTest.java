package de.invesdwin.util.math.statistics.runningmedian;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.junit.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.statistics.RunningMedian;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class RunningMedianTest {

    private static final int ITERATIONS = 10_000;
    private static final int SIZE = 300;

    @Test
    public void testPerformance() {
        final List<Double> runMedians = run();
        final List<Double> runApacheCommonsMedians = runApacheCommons();
        Assertions.assertThat(runMedians).hasSameSizeAs(runApacheCommonsMedians);
        for (int i = 0; i < runMedians.size(); i++) {
            Assertions.checkEquals(runMedians.get(i), runApacheCommonsMedians.get(i));
        }
    }

    private List<Double> run() {
        Duration sumDuration = Duration.ZERO;
        final RunningMedian runningMedian = new RunningMedian(SIZE);

        final List<Double> medians = new ArrayList<>();
        for (double i = 0; i < ITERATIONS; i++) {
            final Instant time = new Instant();
            runningMedian.add(i);
            runningMedian.add(-i);
            final Double median = runningMedian.getMedian();
            sumDuration = sumDuration.add(time.toDuration());
            medians.add(median);
        }
        //CHECKSTYLE:OFF
        System.out.println("run() duration " + sumDuration);
        //CHECKSTYLE:ON

        return medians;
    }

    private List<Double> runApacheCommons() {
        Duration sumDuration = Duration.ZERO;
        final LinkedList<Double> sequentialValues = new LinkedList<Double>();

        final List<Double> medians = new ArrayList<>();
        final double[] values = new double[SIZE];
        for (double i = 0; i < ITERATIONS; i++) {
            if (sequentialValues.size() >= SIZE) {
                sequentialValues.removeFirst();
            }
            sequentialValues.add(i);
            if (sequentialValues.size() >= SIZE) {
                sequentialValues.removeFirst();
            }
            sequentialValues.add(-i);
            final Instant time = new Instant();
            for (int j = 0; j < sequentialValues.size(); j++) {
                values[j] = sequentialValues.get(j);
            }
            final Median medianAlgo = new Median();
            final double median = medianAlgo.evaluate(values, 0, sequentialValues.size());
            medians.add(median);
            sumDuration = sumDuration.add(time.toDuration());
        }
        //CHECKSTYLE:OFF
        System.out.println("runApacheCommons() duration " + sumDuration);
        //CHECKSTYLE:ON
        return medians;
    }

}
