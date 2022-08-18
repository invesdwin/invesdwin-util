package de.invesdwin.util.math.random;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.util.XoRoShiRo128PlusRandomGenerator;

@NotThreadSafe
public class PseudoRandomGenerator extends XoRoShiRo128PlusRandomGenerator implements IRandomGenerator {

    public PseudoRandomGenerator() {
        super();
    }

    public PseudoRandomGenerator(final long seed) {
        super(seed);
    }

    public PseudoRandomGenerator(final byte[] seed) {
        super();
        setSeed(seed);
    }

    public void setSeed(final byte[] seed) {
        // the following number is the largest prime that fits in 32 bits (it is 2^32 - 5)
        final long prime = 4294967291L;

        long combined = 0L;
        for (int i = 0; i < seed.length; i++) {
            combined = combined * prime + seed[i];
        }
        setSeed(combined);
    }

}
