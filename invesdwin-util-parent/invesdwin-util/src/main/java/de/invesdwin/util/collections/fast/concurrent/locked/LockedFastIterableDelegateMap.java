package de.invesdwin.util.collections.fast.concurrent.locked;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.ILock;

@ThreadSafe
public class LockedFastIterableDelegateMap<K, V> extends ALockedFastIterableDelegateMap<K, V> {

    public LockedFastIterableDelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    public LockedFastIterableDelegateMap(final Map<K, V> delegate, final ILock lock) {
        super(delegate, lock);
    }

    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
