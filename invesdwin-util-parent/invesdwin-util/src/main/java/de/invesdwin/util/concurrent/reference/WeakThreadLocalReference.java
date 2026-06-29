package de.invesdwin.util.concurrent.reference;

import java.util.Optional;

import javax.annotation.concurrent.ThreadSafe;

import org.jspecify.annotations.Nullable;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;

import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.streams.closeable.ISafeCloseable;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * WARNING: Instances of ThreadLocal objects should always be defined in static variables or else the threadLocal
 * instances might cause memory leaks.
 * 
 * Use WeakThreadLocalReference instead for instance thread locals without memory leaks. Though make sure not to leak
 * any references from the surrounding class by e.g. creating this instance from a static factory method.
 */
@ThreadSafe
public class WeakThreadLocalReference<V> implements IMutableReference<V> {

    private static final FastThreadLocal<LoadingCache<Object, Reference>> THREAD_LOCAL = new FastThreadLocal<LoadingCache<Object, Reference>>() {
        @Override
        protected LoadingCache<Object, Reference> initialValue() throws Exception {
            final LoadingCache<Object, Reference> map = Caffeine.newBuilder()
                    .weakKeys()
                    .removalListener(
                            (@Nullable final Object key, @Nullable final Reference value, final RemovalCause cause) -> {
                                WeakThreadLocalReference.onRemoval(value);
                            })
                    .<Object, Reference> build((key) -> {
                        return new Reference();
                    });
            return map;
        }

        @Override
        protected void onRemoval(final com.github.benmanes.caffeine.cache.LoadingCache<Object, Reference> value)
                throws Exception {
            for (final Reference reference : value.asMap().values()) {
                WeakThreadLocalReference.onRemoval(reference);
            }
        };
    };

    private final Object key = new Object();

    private static void onRemoval(final Reference reference) {
        if (reference == null) {
            return;
        }
        reference.close();
    }

    protected V initialValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get() {
        final LoadingCache<Object, Reference> map = THREAD_LOCAL.get();
        final Reference reference = map.get(key);
        final Optional<V> optional = (Optional<V>) reference.get();
        if (optional != null) {
            return optional.orElse(null);
        } else {
            final V initialValue = initialValue();
            reference.set(initialValue, isCloseOnRemoval());
            return initialValue;
        }
    }

    @Override
    public void set(final V value) {
        final LoadingCache<Object, Reference> map = THREAD_LOCAL.get();
        if (value == null) {
            map.invalidate(key);
        } else {
            final Reference reference = map.get(key);
            reference.set(value, isCloseOnRemoval());
        }
    }

    @Override
    public V getAndSet(final V value) {
        final V get = get();
        set(value);
        return get;
    }

    public void remove() {
        final LoadingCache<Object, Reference> map = THREAD_LOCAL.get();
        map.invalidate(key);
    }

    protected boolean isCloseOnRemoval() {
        return false;
    }

    private static final class Reference implements ISafeCloseable {

        private Object value;
        private boolean closeOnRemoval;

        public void set(final Object value, final boolean closeOnRemoval) {
            this.value = value;
            this.closeOnRemoval = closeOnRemoval;
        }

        public Object get() {
            return value;
        }

        @Override
        public void close() {
            if (!closeOnRemoval) {
                return;
            }
            final Object valueCopy = value;
            if (valueCopy != null) {
                Closeables.close(valueCopy);
                value = null;
            }
            closeOnRemoval = false;
        }

    }

}
