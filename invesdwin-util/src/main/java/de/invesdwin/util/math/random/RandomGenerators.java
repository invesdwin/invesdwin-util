package de.invesdwin.util.math.random;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;

@Immutable
public final class RandomGenerators {

    private RandomGenerators() {}

    public static RandomGenerator currentThreadLocalRandom() {
        return new RandomGeneratorAdapter(ThreadLocalRandom.current());
    }

    public static RandomGenerator newDefaultRandom() {
        return new XoRoShiRo128PlusRandomGenerator();
    }

}
