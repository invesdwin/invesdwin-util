package de.invesdwin.util.concurrent.reference;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * WARNING: Instances of ThreadLocal objects should always be defined in static variables or else the threadLocal
 * instances might cause memory leaks.
 * 
 * Use WeakThreadLocalReference instead for instance thread locals without memory leaks.
 */
@ThreadSafe
public class WeakThreadLocalReference<V> implements IMutableReference<V> {

    private static final FastThreadLocal<LoadingCache<Object, IMutableReference<Optional<Object>>>> THREAD_LOCAL = new FastThreadLocal<LoadingCache<Object, IMutableReference<Optional<Object>>>>() {
        @Override
        protected LoadingCache<Object, IMutableReference<Optional<Object>>> initialValue() throws Exception {
            final LoadingCache<Object, IMutableReference<Optional<Object>>> map = Caffeine.newBuilder()
                    .weakKeys()
                    .<Object, IMutableReference<Optional<Object>>> build((key) -> {
                        return new MutableReference<Optional<Object>>();
                    });
            return map;
        }
    };

    private final Object key = new Object();

    protected V initialValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get() {
        final LoadingCache<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
        final IMutableReference<Optional<Object>> reference = map.get(key);
        final Optional<V> optional = (Optional<V>) reference.get();
        if (optional != null) {
            return optional.orElse(null);
        } else {
            final V initialValue = initialValue();
            reference.set(Optional.ofNullable(initialValue));
            return initialValue;
        }
    }

    @Override
    public void set(final V value) {
        final LoadingCache<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
        if (value == null) {
            map.invalidate(key);
        } else {
            final IMutableReference<Optional<Object>> reference = map.get(key);
            reference.set(Optional.ofNullable(value));
        }
    }

    public void remove() {
        final LoadingCache<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
        map.invalidate(key);
    }

}
