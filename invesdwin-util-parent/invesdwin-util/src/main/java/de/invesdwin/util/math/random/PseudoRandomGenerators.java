package de.invesdwin.util.math.random;

import javax.annotation.concurrent.Immutable;

import io.netty.util.concurrent.FastThreadLocal;

@Immutable
public final class PseudoRandomGenerators {

    private static final FastThreadLocal<IRandomGenerator> THREAD_LOCAL = new FastThreadLocal<IRandomGenerator>() {
        @Override
        protected IRandomGenerator initialValue() {
            return newPseudoRandom();
        }
    };

    private PseudoRandomGenerators() {
    }

    public static IRandomGenerator getThreadLocalPseudoRandom() {
        return THREAD_LOCAL.get();
    }

    /**
     * WARNING: for better performance, use thread local pseudorandom instead of a new one if it can not be reused for a
     * long period of time.
     * 
     * xoroshiro++ is not cryptographically secure
     * https://lemire.me/blog/2017/08/22/cracking-random-number-generators-xoroshiro128/
     * 
     * Use CryptoRandomGenerators instead for security purposes.
     */
    public static IRandomGenerator newPseudoRandom() {
        return new PseudoRandomGenerator();
    }

    public static IRandomGenerator newPseudoRandom(final long seed) {
        return new PseudoRandomGenerator(seed);
    }

    public static IRandomGenerator newPseudoRandom(final byte[] seed) {
        return new PseudoRandomGenerator(seed);
    }

}
