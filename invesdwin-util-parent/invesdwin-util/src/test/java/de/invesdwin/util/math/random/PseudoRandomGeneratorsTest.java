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

    private static final long ITERATIONS = 100000000L;

    //java17
    //reusing instance
    //    JdkThreadLocalRandom: PT0.016.215.122S
    //    Xoroshiro: PT0.078.339.688S
    //    ThreadLocalRandom: PT0.084.355.868S
    //    L32X64MixRandom: PT0.595.409.955S
    //    PseudoRandomGeneratorObjectPool(OneToOne): PT0.617.342.129S
    //    Xoshiro256PlusPlus: PT0.668.045.409S
    //    SplittableRandom: PT0.680.262.234S
    //    Xoroshiro128PlusPlus: PT0.685.917.377S
    //    L64X128StarStarRandom: PT0.711.857.223S
    //    L64X128MixRandom: PT0.718.201.388S
    //    L128X128MixRandom: PT0.739.724.578S
    //    L64X1024MixRandom: PT0.769.200.932S
    //    L64X256MixRandom: PT0.800.963.750S
    //    jdkDefaultRandomGenerator(jdk.random.L32X64MixRandom@460d0a57): PT0.927.418.560S
    //    PseudoRandomGeneratorObjectPool(OneToMany): PT0.969.295.116S
    //    L128X1024MixRandom: PT1.079.990.847S
    //    L128X256MixRandom: PT1.277.342.881S
    //    PseudoRandomGeneratorObjectPool(ManyToMany): PT1.437.368.973S
    //    java.util.Random: PT1.467.674.367S
    //    PseudoRandomGeneratorObjectPool(BufferingIterator): PT1.703.566.841S
    //    Random: PT1.722.829.884S
    //    PseudoRandomGeneratorObjectPool(SynchronizedBufferingIterator): PT3.961.031.695S
    //    SecureRandom: PT40.990.572.100S
    //not reusing instance
    //    ThreadLocalRandom: PT0.434.856.781S
    //    PseudoRandomGeneratorObjectPool(OneToOne): PT0.617.342.129S
    //    JdkThreadLocalRandom: PT0.716.884.015S
    //    PseudoRandomGeneratorObjectPool(OneToMany): PT0.969.295.116S
    //    PseudoRandomGeneratorObjectPool(ManyToMany): PT1.437.368.973S
    //  PseudoRandomGeneratorObjectPool(BufferingIterator): PT1.703.566.841S
    //    L32X64MixRandom: PT2.173.823.411S
    //    Xoroshiro128PlusPlus: PT2.187.755.401S
    //    L64X128MixRandom: PT2.337.536.419S
    //    L64X128StarStarRandom: PT2.440.845.334S
    //    L128X128MixRandom: PT2.466.457.691S
    //    SplittableRandom: PT2.475.223.179S
    //    Xoshiro256PlusPlus: PT2.593.130.465S
    //    Xoroshiro: PT2.611.973.364S
    //    jdkDefaultRandomGenerator(jdk.random.L32X64MixRandom@3901d134): PT2.830.466.753S
    //    L64X256MixRandom: PT2.906.695.444S
    //    L128X256MixRandom: PT3.279.234.752S
    //    PseudoRandomGeneratorObjectPool(SynchronizedBufferingIterator): PT3.961.031.695S
    //    java.util.Random: PT4.181.718.875S
    //    L64X1024MixRandom: PT4.625.684.015S
    //    Random: PT4.965.431.836S
    //    L128X1024MixRandom: PT5.054.260.496S
    @Test
    public void testPerformance() throws Exception {
        testRandomGenerator("Xorshiro", new Callable<IRandomGenerator>() {
            @Override
            public IRandomGenerator call() throws Exception {
                return PseudoRandomGenerators.newPseudoRandom();
            }
        });
        testRandomPooledGenerator();
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
        testRandomGenerator("java.util.Random", new Callable<IRandomGenerator>() {
            @Override
            public IRandomGenerator call() throws Exception {
                return new RandomGeneratorAdapter(new java.util.Random());
            }
        });
        //        for (final RandomGeneratorFactory<java.util.random.RandomGenerator> randomFactory : java.util.random.RandomGeneratorFactory
        //                .all()
        //                .toList()) {
        //            if (randomFactory.name().equals("SecureRandom")) {
        //                continue;
        //            }
        //            testRandomGenerator(randomFactory.name(), new Callable<IRandomGenerator>() {
        //                @Override
        //                public IRandomGenerator call() throws Exception {
        //                    return new RandomGeneratorAdapter17(randomFactory.create());
        //                }
        //            });
        //        }
        //        testRandomGenerator("jdkDefaultRandomGenerator(" + RandomGenerator.getDefault() + ")",
        //                new Callable<IRandomGenerator>() {
        //                    @Override
        //                    public IRandomGenerator call() throws Exception {
        //                        return new RandomGeneratorAdapter17(RandomGenerator.getDefault());
        //                    }
        //                });

    }

    private Duration testRandomGenerator(final String name, final Callable<IRandomGenerator> random) throws Exception {
        final Instant start = new Instant();
        for (long i = 0; i < ITERATIONS; i++) {
            random.call().nextDouble();
        }
        final Duration duration = start.toDuration();
        //CHECKSTYLE:OFF
        System.out.println(name + ": " + duration);
        //CHECKSTYLE:ON
        return duration;
    }

    private Duration testRandomPooledGenerator() throws Exception {
        final Instant start = new Instant();
        for (long i = 0; i < ITERATIONS; i++) {
            final IRandomGenerator random = PseudoRandomGeneratorObjectPool.INSTANCE.borrowObject();
            try {
                random.nextDouble();
            } finally {
                PseudoRandomGeneratorObjectPool.INSTANCE.returnObject(random);
            }
        }
        final Duration duration = start.toDuration();
        //CHECKSTYLE:OFF
        System.out.println("PseudoRandomGeneratorObjectPool: " + duration);
        //CHECKSTYLE:ON
        return duration;
    }

    //    private static final class RandomGeneratorAdapter17 implements IRandomGenerator {
    //
    //        private final java.util.random.RandomGenerator delegate;
    //
    //        private RandomGeneratorAdapter17(final java.util.random.RandomGenerator delegate) {
    //            this.delegate = delegate;
    //        }
    //
    //        @Override
    //        public void setSeed(final int seed) {
    //            setSeed((long) seed);
    //        }
    //
    //        @Override
    //        public void setSeed(final int[] seed) {
    //            // the following number is the largest prime that fits in 32 bits (it is 2^32 - 5)
    //            final long prime = 4294967291L;
    //
    //            long combined = 0L;
    //            for (final int s : seed) {
    //                combined = combined * prime + s;
    //            }
    //            setSeed(combined);
    //        }
    //
    //        @Override
    //        public void setSeed(final long seed) {
    //        }
    //
    //        @Override
    //        public void nextBytes(final byte[] bytes) {
    //            delegate.nextBytes(bytes);
    //        }
    //
    //        @Override
    //        public int nextInt() {
    //            return delegate.nextInt();
    //        }
    //
    //        @Override
    //        public int nextInt(final int n) {
    //            return delegate.nextInt(n);
    //        }
    //
    //        @Override
    //        public long nextLong() {
    //            return delegate.nextLong();
    //        }
    //
    //        @Override
    //        public boolean nextBoolean() {
    //            return delegate.nextBoolean();
    //        }
    //
    //        @Override
    //        public float nextFloat() {
    //            return delegate.nextFloat();
    //        }
    //
    //        @Override
    //        public double nextDouble() {
    //            return delegate.nextDouble();
    //        }
    //
    //        @Override
    //        public double nextGaussian() {
    //            return delegate.nextGaussian();
    //        }
    //
    //    }

}
