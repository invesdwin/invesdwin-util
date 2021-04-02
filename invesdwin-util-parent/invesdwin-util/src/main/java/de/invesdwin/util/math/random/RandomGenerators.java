package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import io.netty.util.concurrent.FastThreadLocal;
import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;

@Immutable
public final class RandomGenerators {

    private static final FastThreadLocal<RandomGenerator> THREAD_LOCAL = new FastThreadLocal<RandomGenerator>() {
        @Override
        protected RandomGenerator initialValue() {
            return newDefaultRandom();
        }
    };

    private RandomGenerators() {
    }

    public static RandomGenerator currentThreadLocalRandom() {
        return THREAD_LOCAL.get();
    }

    public static RandomGenerator newDefaultRandom() {
        return new XoRoShiRo128PlusRandomGenerator();
    }

    public static RandomGenerator newDefaultRandom(final long seed) {
        return new XoRoShiRo128PlusRandomGenerator(seed);
    }

}
