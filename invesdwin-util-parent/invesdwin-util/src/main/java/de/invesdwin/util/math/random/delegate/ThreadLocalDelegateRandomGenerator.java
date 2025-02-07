package de.invesdwin.util.math.random.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.random.IRandomGenerator;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

@NotThreadSafe
public class ThreadLocalDelegateRandomGenerator extends ADelegateRandomGenerator {

    public ThreadLocalDelegateRandomGenerator() {
        super(null);
    }

    @Override
    protected IRandomGenerator getDelegate() {
        return PseudoRandomGenerators.getThreadLocalPseudoRandom();
    }

    @Deprecated
    @Override
    protected IRandomGenerator newDelegate() {
        throw new UnsupportedOperationException();
    }

}
