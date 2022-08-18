package de.invesdwin.util.math.random;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@Disabled("manual test")
@NotThreadSafe
public class PseudoRandomGeneratorsTest {

    @Test
    public void testPerformance() throws Exception {
        testRandomGenerator("Xoroshiro", new Callable<IRandomGenerator>() {

            private final IRandomGenerator random = PseudoRandomGenerators.newPseudoRandom();

            @Override
            public IRandomGenerator call() throws Exception {
                return random;
            }
        });
        testRandomGenerator("ThreadLocalRandom", new Callable<IRandomGenerator>() {
            @Override
            public IRandomGenerator call() throws Exception {
                return PseudoRandomGenerators.getThreadLocalPseudoRandom();
            }
        });
        testRandomGenerator("JdkThreadLocalRandom", new Callable<IRandomGenerator>() {
            @Override
            public IRandomGenerator call() throws Exception {
                return new RandomGeneratorAdapter(java.util.concurrent.ThreadLocalRandom.current());
            }
        });
    }

    private Duration testRandomGenerator(final String name, final Callable<IRandomGenerator> random) throws Exception {
        final Instant start = new Instant();
        for (long i = 0; i < 1000000000L; i++) {
            final IRandomGenerator instance = random.call();
            instance.nextDouble();
        }
        final Duration duration = start.toDuration();
        //CHECKSTYLE:OFF
        System.out.println(name + ": " + duration);
        //CHECKSTYLE:ON
        return duration;
    }

}
