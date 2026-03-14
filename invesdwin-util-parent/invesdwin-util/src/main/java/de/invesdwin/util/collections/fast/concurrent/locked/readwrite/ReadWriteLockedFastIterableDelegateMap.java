package de.invesdwin.util.collections.fast.concurrent.locked.readwrite;

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;

@ThreadSafe
public class ReadWriteLockedFastIterableDelegateMap<K, V> extends AReadWriteLockedFastIterableDelegateMap<K, V> {

    public ReadWriteLockedFastIterableDelegateMap(final Map<K, V> delegate) {
        super(delegate);
    }

    public ReadWriteLockedFastIterableDelegateMap(final Map<K, V> delegate, final IReadWriteLock lock) {
        super(delegate, lock);
    }

    @Override
    protected Map<K, V> newDelegate() {
        throw new UnsupportedOperationException();
    }

}
