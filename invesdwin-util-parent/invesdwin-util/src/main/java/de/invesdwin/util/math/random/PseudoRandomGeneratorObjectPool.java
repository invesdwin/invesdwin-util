package de.invesdwin.util.math.random;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
public final class PseudoRandomGeneratorObjectPool extends AAgronaObjectPool<IRandomGenerator> {

    public static final PseudoRandomGeneratorObjectPool INSTANCE = new PseudoRandomGeneratorObjectPool();

    private PseudoRandomGeneratorObjectPool() {
    }

    @Override
    protected IRandomGenerator newObject() {
        return PseudoRandomGenerators.newPseudoRandom();
    }

    @Override
    public void invalidateObject(final IRandomGenerator element) {
        //noop
    }

    @Override
    protected void passivateObject(final IRandomGenerator element) {
        //noop
    }

}