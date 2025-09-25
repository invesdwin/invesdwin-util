package de.invesdwin.util.collections.recursive;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public class PreventRecursiveLoad<K, V> {

    private final Set<K> alreadyLoadingKeySet = ILockCollectionFactory.getInstance(true).newConcurrentSet();

    public PreventRecursiveLoad() {}

    public void preventRecursiveLoad(final K key) throws RecursiveLoadException {
        if (alreadyLoadingKeySet.contains(key)) {
            throw newRecursiveLoadException(key);
        }
    }

    public V preventRecursiveLoad(final K key, final Function<? super K, ? extends V> delegate)
            throws RecursiveLoadException {
        if (alreadyLoadingKeySet.add(key)) {
            try {
                final V loaded = delegate.apply(key);
                return loaded;
            } finally {
                alreadyLoadingKeySet.remove(key);
            }
        } else {
            throw newRecursiveLoadException(key);
        }
    }

    public V preventRecursiveLoad(final K key, final Supplier<? extends V> delegate) throws RecursiveLoadException {
        if (alreadyLoadingKeySet.add(key)) {
            try {
                final V loaded = delegate.get();
                return loaded;
            } finally {
                alreadyLoadingKeySet.remove(key);
            }
        } else {
            throw newRecursiveLoadException(key);
        }
    }

    protected RecursiveLoadException newRecursiveLoadException(final K key) {
        return FastRecursiveLoadException.getInstance("Already loading recursively key: %s", key);
    }

}
