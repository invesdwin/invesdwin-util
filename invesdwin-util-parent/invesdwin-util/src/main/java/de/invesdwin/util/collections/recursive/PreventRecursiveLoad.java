package de.invesdwin.util.collections.recursive;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.reference.WeakThreadLocalReference;

@ThreadSafe
public class PreventRecursiveLoad<K, V> {

    private final WeakThreadLocalReference<Set<K>> alreadyLoadingKeyRef = new WeakThreadLocalReference<Set<K>>() {
        @Override
        protected Set<K> initialValue() {
            return new HashSet<K>();
        }
    };

    public PreventRecursiveLoad() {}

    public V preventRecursiveLoad(final K key, final Function<? super K, ? extends V> delegate)
            throws RecursiveLoadException {
        final Set<K> alreadyLoading = alreadyLoadingKeyRef.get();
        if (alreadyLoading.add(key)) {
            try {
                final V loaded = delegate.apply(key);
                return loaded;
            } finally {
                alreadyLoading.remove(key);
            }
        } else {
            return onRecursiveLoad(key);
        }
    }

    public V preventRecursiveLoad(final K key, final Supplier<? extends V> delegate) throws RecursiveLoadException {
        final Set<K> alreadyLoading = alreadyLoadingKeyRef.get();
        if (alreadyLoading.add(key)) {
            try {
                final V loaded = delegate.get();
                return loaded;
            } finally {
                alreadyLoading.remove(key);
            }
        } else {
            return onRecursiveLoad(key);
        }
    }

    protected V onRecursiveLoad(final K key) throws RecursiveLoadException {
        throw FastRecursiveLoadException.getInstance("Already loading recursively key: %s", key);
    }

}
