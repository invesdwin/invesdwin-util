package de.invesdwin.util.concurrent.reference.bool;

import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;
import de.invesdwin.util.concurrent.reference.integer.MutableIntReference;

public class CountingMutableBooleanReference implements IMutableBooleanReference {

    private final IMutableIntReference count;
    private final int expectedTrueCount;

    public CountingMutableBooleanReference(final int expectedTrueCount) {
        this.expectedTrueCount = expectedTrueCount;
        this.count = newCount();
    }

    protected IMutableIntReference newCount() {
        return new MutableIntReference();
    }

    @Override
    public boolean get() {
        return count.get() >= expectedTrueCount;
    }

    @Override
    public void set(final boolean value) {
        if (value) {
            count.incrementAndGet();
        } else {
            count.decrementAndGet();
        }
    }

    @Override
    public boolean getAndSet(final boolean value) {
        final boolean get = get();
        if (value) {
            count.incrementAndGet();
        } else {
            count.decrementAndGet();
        }
        return get;
    }

}
