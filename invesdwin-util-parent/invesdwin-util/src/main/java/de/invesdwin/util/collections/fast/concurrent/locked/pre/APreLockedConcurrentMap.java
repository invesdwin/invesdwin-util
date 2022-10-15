package de.invesdwin.util.collections.fast.concurrent.locked.pre;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public abstract class APreLockedConcurrentMap<K, V> extends APreLockedMap<K, V> implements ConcurrentMap<K, V> {

    public APreLockedConcurrentMap(final TextDescription iteratorName) {
        super(iteratorName);
    }

    public APreLockedConcurrentMap(final TextDescription iteratorName, final ILock lock) {
        super(iteratorName, lock);
    }

    @Override
    protected abstract ConcurrentMap<K, V> getPreLockedDelegate();

}
