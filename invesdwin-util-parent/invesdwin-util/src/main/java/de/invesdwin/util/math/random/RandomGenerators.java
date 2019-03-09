package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.random.internal.XoRoShiRo128PlusRandomGeneratorFactory;

@Immutable
public final class RandomGenerators {

    private static final ThreadLocal<RandomGenerator> THREAD_LOCAL = new ThreadLocal<RandomGenerator>() {
        @Override
        protected RandomGenerator initialValue() {
            return newDefaultRandom();
        }
    };

    private RandomGenerators() {}

    public static RandomGenerator currentThreadLocalRandom() {
        return THREAD_LOCAL.get();
    }

    public static RandomGenerator newDefaultRandom() {
        return XoRoShiRo128PlusRandomGeneratorFactory.newInstance();
    }

}
