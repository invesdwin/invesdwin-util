package de.invesdwin.util.collections.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.delegate.ADelegateMap;

@ThreadSafe
public class AttributesMap extends ADelegateMap<String, Object> implements IAttributesMap {

    @Override
    protected Map<String, Object> newDelegate() {
        return Collections.synchronizedMap(new HashMap<String, Object>());
    }

    /**
     * Use getOrCreate instead
     */
    @Deprecated
    @Override
    public Object computeIfAbsent(final String key, final Function<? super String, ? extends Object> mappingFunction) {
        Object v;
        final Map<String, Object> delegate = getDelegate();
        synchronized (delegate) {
            v = delegate.get(key);
        }
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = mappingFunction.apply(key);
            if (v != null) {
                synchronized (delegate) {
                    final Object oldV = delegate.get(key);
                    if (oldV != null) {
                        v = oldV;
                    } else {
                        delegate.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(final String key, final Supplier<T> createSupplier) {
        T v;
        final Map<String, Object> delegate = getDelegate();
        synchronized (delegate) {
            v = (T) delegate.get(key);
        }
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            v = createSupplier.get();
            if (v != null) {
                synchronized (delegate) {
                    final T oldV = (T) delegate.get(key);
                    if (oldV != null) {
                        v = oldV;
                    } else {
                        delegate.put(key, v);
                    }
                }
            }
        }
        return v;
    }

    @Override
    public SoftAttributesMap getSoft() {
        return getOrCreate(SoftAttributesMap.class.getSimpleName() + "_INSTANCE", () -> new SoftAttributesMap());
    }

    @Override
    public WeakAttributesMap getWeak() {
        return getOrCreate(WeakAttributesMap.class.getSimpleName() + "_INSTANCE", () -> new WeakAttributesMap());
    }
}
