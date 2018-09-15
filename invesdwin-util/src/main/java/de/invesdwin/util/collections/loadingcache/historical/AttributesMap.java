package de.invesdwin.util.collections.loadingcache.historical;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.delegate.ADelegateMap;

@ThreadSafe
public class AttributesMap extends ADelegateMap<String, Object> {

    @Override
    protected Map<String, Object> newDelegate() {
        return Collections.synchronizedMap(new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(final String key, final Callable<T> createCallable) {
        T v;
        final Map<String, Object> delegate = getDelegate();
        synchronized (delegate) {
            v = (T) delegate.get(key);
        }
        if (v == null) {
            //bad idea to synchronize in apply, this might cause deadlocks when threads are used inside of it
            try {
                v = createCallable.call();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
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

}
