package de.invesdwin.util.math.random;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;
import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;

@Ignore("manual test")
@NotThreadSafe
public class RandomGeneratorsTest {

    @Test
    public void testPerformance() {
        testRandomGenerator("Xoroshiro", new XoRoShiRo128PlusRandomGenerator());
        testRandomGenerator("ThreadLocalRandom", RandomGenerators.currentThreadLocalRandom());
    }

    private Duration testRandomGenerator(final String name, final RandomGenerator random) {
        final Instant start = new Instant();
        for (long i = 0; i < 10000000000L; i++) {
            random.nextDouble();
        }
        final Duration duration = start.toDuration();
        //CHECKSTYLE:OFF
        System.out.println(name + ": " + duration);
        //CHECKSTYLE:ON
        return duration;
    }

}
