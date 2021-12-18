package de.invesdwin.util.math.random;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@Disabled("manual test")
@NotThreadSafe
public class RandomGeneratorsTest {

    @Test
    public void testPerformance() throws Exception {
        testRandomGenerator("Xoroshiro", new Callable<RandomGenerator>() {

            private final RandomGenerator random = RandomGenerators.newDefaultRandom();

            @Override
            public RandomGenerator call() throws Exception {
                return random;
            }
        });
        testRandomGenerator("ThreadLocalRandom", new Callable<RandomGenerator>() {
            @Override
            public RandomGenerator call() throws Exception {
                return RandomGenerators.currentThreadLocalRandom();
            }
        });
        testRandomGenerator("JdkThreadLocalRandom", new Callable<RandomGenerator>() {
            @Override
            public RandomGenerator call() throws Exception {
                return new RandomGeneratorAdapter(java.util.concurrent.ThreadLocalRandom.current());
            }
        });
    }

    private Duration testRandomGenerator(final String name, final Callable<RandomGenerator> random) throws Exception {
        final Instant start = new Instant();
        for (long i = 0; i < 1000000000L; i++) {
            random.call().nextDouble();
        }
        final Duration duration = start.toDuration();
        //CHECKSTYLE:OFF
        System.out.println(name + ": " + duration);
        //CHECKSTYLE:ON
        return duration;
    }

}
