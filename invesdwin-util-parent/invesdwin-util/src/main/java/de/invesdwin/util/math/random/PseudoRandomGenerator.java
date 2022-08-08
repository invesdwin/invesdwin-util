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

}
