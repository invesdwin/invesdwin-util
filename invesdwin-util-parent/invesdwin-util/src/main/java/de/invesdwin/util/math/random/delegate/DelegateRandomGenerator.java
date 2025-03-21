package de.invesdwin.util.math.random.delegate;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.random.IRandomGenerator;

@NotThreadSafe
public class DelegateRandomGenerator<K, V> extends ADelegateRandomGenerator {

    public DelegateRandomGenerator(final IRandomGenerator delegate) {
        super(delegate);
    }

    @Deprecated
    @Override
    protected IRandomGenerator newDelegate() {
        throw new UnsupportedOperationException();
    }

}