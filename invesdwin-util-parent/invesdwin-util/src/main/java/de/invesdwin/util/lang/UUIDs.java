package de.invesdwin.util.lang;

import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

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
    public static String newPseudoRandomUUID() {
        final IRandomGenerator random = PseudoRandomGenerators.getThreadLocalPseudoRandom();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }

}
