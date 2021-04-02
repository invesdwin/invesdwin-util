package de.invesdwin.util.concurrent.reference;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public class WeakThreadLocalReference<V> implements IMutableReference<V> {

    private static final FastThreadLocal<Map<Object, IMutableReference<Optional<Object>>>> THREAD_LOCAL = new FastThreadLocal<Map<Object, IMutableReference<Optional<Object>>>>() {
        @Override
        protected Map<Object, IMutableReference<Optional<Object>>> initialValue() throws Exception {
            final ConcurrentMap<Object, IMutableReference<Optional<Object>>> map = Caffeine.newBuilder()
                    .weakKeys()
                    .<Object, IMutableReference<Optional<Object>>> build(
                            (key) -> new MutableReference<Optional<Object>>())
                    .asMap();
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
        final Map<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
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
        final Map<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
        if (value == null) {
            map.remove(key);
        } else {
            final IMutableReference<Optional<Object>> reference = map.get(key);
            reference.set(Optional.ofNullable(value));
        }
    }

    public void remove() {
        final Map<Object, IMutableReference<Optional<Object>>> map = THREAD_LOCAL.get();
        map.remove(key);
    }

}
