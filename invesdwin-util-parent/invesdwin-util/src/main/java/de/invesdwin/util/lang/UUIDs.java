package de.invesdwin.util.lang;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.math3.random.RandomGenerator;

import de.invesdwin.util.math.random.RandomGenerators;

@Immutable
public final class UUIDs {

    private UUIDs() {
    }

    public static String newRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * This implementation is significantly faster than the random one; though has the drawback of a higher rate of
     * collisions. If this is no concern, this implementation is fine to be used.
     * 
     * See: http://stackoverflow.com/questions/14532976/performance-of-random-uuid-generation-with-java-7-or-java-6
     */
    public static String newPseudorandomUUID() {
        final RandomGenerator random = RandomGenerators.currentThreadLocalRandom();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }

}
