package de.invesdwin.util.collections.loadingcache.historical;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.assertj.core.description.TextDescription;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.concurrent.AFastIterableDelegateList;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.loadingcache.ADelegateLoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.HistoricalCachePreviousKeysQueryInterceptorSupport;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.HistoricalCacheRangeQueryInterceptorSupport;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCachePreviousKeysQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.key.APullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheIncreaseMaximumSizeListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.TrailingHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.filter.FilteringHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.refresh.HistoricalCacheRefreshManager;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AHistoricalCache<V>
        implements IHistoricalCacheIncreaseMaximumSizeListener, IHistoricalCachePutListener {

    public static final Integer DISABLED_MAXIMUM_SIZE = 0;
    public static final Integer UNLIMITED_MAXIMUM_SIZE = null;
    /**
     * 10k is normally sufficient for daily bars of stocks and also fast enough for intraday ticks to load.
     */
    public static final Integer DEFAULT_MAXIMUM_SIZE = 100;
    public static final int DEFAULT_MAXIMUM_SIZE_LIMIT = 100000;
    public static final EvictionMode EVICTION_MODE = EvictionMode.LeastRecentlyAdded;
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AHistoricalCache.class);
    private static boolean debugAutomaticReoptimization = false;

    protected final IHistoricalCacheInternalMethods<V> internalMethods = new HistoricalCacheInternalMethods();

    private final IHistoricalCacheQueryCore<V> queryCore = newHistoricalCacheQueryCore();
    private IHistoricalCacheAdjustKeyProvider adjustKeyProvider = new InnerHistoricalCacheAdjustKeyProvider();
    private final Set<IHistoricalCacheOnClearListener> onClearListeners = newListenerSet();
    private final Set<IHistoricalCacheIncreaseMaximumSizeListener> increaseMaximumSizeListeners = newListenerSet();

    private IHistoricalCachePutProvider<V> putProvider = new InnerHistoricalCachePutProvider();
    private boolean isPutDisabled = getMaximumSize() != null && getMaximumSize() == 0;

    private volatile FDate lastRefresh = HistoricalCacheRefreshManager.getLastRefresh();
    private volatile Integer maximumSize = getInitialMaximumSize();
    private IHistoricalCacheShiftKeyProvider shiftKeyProvider = new InnerHistoricalCacheShiftKeyProvider();
    private IHistoricalCacheExtractKeyProvider<V> extractKeyProvider = new InnerHistoricalCacheExtractKeyProvider();
    private final ILoadingCache<FDate, V> valuesMap = new ADelegateLoadingCache<FDate, V>() {

        @Override
        public V get(final FDate key) {
            invokeRefreshIfRequested();
            return super.get(key);
        }

        @Override
        protected ILoadingCache<FDate, V> createDelegate() {
            final Integer size = getMaximumSize();
            if (size == null || size > 0) {
                HistoricalCacheRefreshManager.register(AHistoricalCache.this);
            }
            return newLoadingCacheProvider(new Function<FDate, V>() {
                @Override
                public V apply(final FDate key) {
                    try {
                        final V value = AHistoricalCache.this.loadValue(key);
                        return value;
                    } catch (final Throwable t) {
                        throw new RuntimeException("At: " + AHistoricalCache.this.toString(), t);
                    }
                }

            }, size);
        }
    };
    private volatile boolean refreshRequested;

    public AHistoricalCache() {}

    /**
     * You can enable this setting to get useful info when the automatic reoptimization happens, so you can hardcode the
     * optimal values for getMaximumSize() and getReadBackStepMillis() for this cache in these circumstances.
     */
    public static void setDebugAutomaticReoptimization(final boolean debugAutomaticReoptimization) {
        AHistoricalCache.debugAutomaticReoptimization = debugAutomaticReoptimization;
    }

    public static boolean isDebugAutomaticReoptimization() {
        return debugAutomaticReoptimization;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        final Integer size = getMaximumSize();
        if (size == null || size > 0) {
            HistoricalCacheRefreshManager.unregister(this);
        }
    }

    /**
     * null means unlimited and 0 means no caching at all.
     */
    protected Integer getInitialMaximumSize() {
        return DEFAULT_MAXIMUM_SIZE;
    }

    @Override
    public final Integer getMaximumSize() {
        return maximumSize;
    }

    @Override
    public final synchronized void increaseMaximumSize(final int maximumSize, final String reason) {
        final Integer existingMaximumSize = this.maximumSize;
        final int usedMaximumSize = Math.min(getMaximumSizeLimit(), maximumSize);
        if (existingMaximumSize == null || existingMaximumSize < usedMaximumSize) {
            innerIncreaseMaximumSize(usedMaximumSize, reason);
        }
    }

    @Override
    public int getMaximumSizeLimit() {
        return DEFAULT_MAXIMUM_SIZE_LIMIT;
    }

    protected void innerIncreaseMaximumSize(final int maximumSize, final String reason) {
        queryCore.increaseMaximumSize(maximumSize);
        if (isDebugAutomaticReoptimization() || maximumSize >= getMaximumSizeLimit()) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(this + ": Increasing maximum size from [" + this.maximumSize + "] to [" + maximumSize
                        + "] with reason [" + reason + "]");
            }
        }
        this.maximumSize = maximumSize;
        for (final IHistoricalCacheIncreaseMaximumSizeListener l : increaseMaximumSizeListeners) {
            final Integer listenerMaximumSize = l.getMaximumSize();
            final int listenerMaximumSizeLimited = Math.min(maximumSize, l.getMaximumSizeLimit());
            if (listenerMaximumSize == null || listenerMaximumSize.intValue() < listenerMaximumSizeLimited) {
                l.increaseMaximumSize(maximumSize, reason);
            }
        }
    }

    protected IHistoricalCacheQueryCore<V> newHistoricalCacheQueryCore() {
        /*
         * always use lookback cache to make getPreviousXyz faster even though this instance might not cache anything in
         * the values map
         */
        return new TrailingHistoricalCacheQueryCore<V>(internalMethods);
    }

    private <T> Set<T> newListenerSet() {
        final ConcurrentMap<T, Boolean> map = Caffeine.newBuilder().weakKeys().<T, Boolean> build().asMap();
        return Collections.newSetFromMap(map);
    }

    protected void setAdjustKeyProvider(final IHistoricalCacheAdjustKeyProvider adjustKeyProvider) {
        Assertions.assertThat(this.adjustKeyProvider)
                .as("%s can only be set once", IHistoricalCacheAdjustKeyProvider.class.getSimpleName())
                .isInstanceOf(InnerHistoricalCacheAdjustKeyProvider.class);
        Assertions.assertThat(adjustKeyProvider.registerHistoricalCache(this)).isTrue(); //need to first register, then set provider or else we might clear the provider too often
        this.adjustKeyProvider = adjustKeyProvider;
    }

    @SuppressWarnings("unchecked")
    protected void setShiftKeyDelegate(final AHistoricalCache<?> shiftKeyDelegate, final boolean alsoExtractKey) {
        Assertions.assertThat(shiftKeyDelegate).as("Use null instead of this").isNotSameAs(this);
        Assertions.assertThat(this.shiftKeyProvider)
                .as("%s can only be set once", IHistoricalCacheShiftKeyProvider.class.getSimpleName())
                .isInstanceOf(InnerHistoricalCacheShiftKeyProvider.class);
        this.shiftKeyProvider = new DelegateHistoricalCacheShiftKeyProvider(
                (AHistoricalCache<Object>) shiftKeyDelegate);
        if (alsoExtractKey) {
            this.extractKeyProvider = DelegateHistoricalCacheExtractKeyProvider.maybeWrap(shiftKeyDelegate);
        }
        //propagate the setting downwards without risking an endless recursion
        registerIncreaseMaximumSizeListener(shiftKeyDelegate);
        isPutDisabled = false;
    }

    protected void setPutDelegate(final AHistoricalCache<? extends V> putDelegate) {
        Assertions.assertThat(putDelegate).as("Use null instead of this").isNotSameAs(this);
        setPutDelegate(putDelegate.getPutProvider());
    }

    @SuppressWarnings("unchecked")
    protected void setPutDelegate(final IHistoricalCachePutProvider<? extends V> putProvider) {
        Assertions.assertThat(this.putProvider)
                .as("%s can only be set once", InnerHistoricalCachePutProvider.class.getSimpleName())
                .isInstanceOf(InnerHistoricalCachePutProvider.class);
        if (InnerHistoricalCachePutProvider.class.isAssignableFrom(putProvider.getClass())) {
            this.putProvider = DelegateHistoricalCachePutProvider.maybeWrap(putProvider);
        } else {
            this.putProvider = (IHistoricalCachePutProvider<V>) putProvider;
        }
    }

    public final IHistoricalCachePutProvider<V> getPutProvider() {
        return putProvider;
    }

    public IHistoricalCacheExtractKeyProvider<V> getExtractKeyProvider() {
        return extractKeyProvider;
    }

    protected FDate adjustKey(final FDate key) {
        return adjustKeyProvider.maybeAdjustKey(key);
    }

    /**
     * Requests a refresh of the cache on the next get() operation. We would risk deadlocks if we did not make that
     * detour.
     */
    public final void requestRefresh() {
        final FDate lastRefreshFromManager = HistoricalCacheRefreshManager.getLastRefresh();
        if (lastRefresh.isBefore(lastRefreshFromManager)) {
            lastRefresh = new FDate();
            refreshRequested = true;
        }
    }

    private void invokeRefreshIfRequested() {
        if (refreshRequested) {
            maybeRefresh();
            refreshRequested = false;
        }
    }

    /**
     * Checks if the dependant data has changed and refreshes if it has to by clearing the cache.
     * 
     * Per default this does not check anything and just clears the cache. More complex logic can be added by overriding
     * this method
     */
    protected boolean maybeRefresh() {
        clear();
        return true;
    }

    protected abstract V loadValue(FDate key);

    protected <T> ILoadingCache<FDate, T> newLoadingCacheProvider(final Function<FDate, T> loadValue,
            final Integer maximumSize) {
        final ALoadingCache<FDate, T> loadingCache = new ALoadingCache<FDate, T>() {

            @Override
            protected Integer getInitialMaximumSize() {
                return maximumSize;
            }

            @Override
            protected T loadValue(final FDate key) {
                return loadValue.apply(key);
            }

            @Override
            protected EvictionMode getEvictionMode() {
                return EVICTION_MODE;
            }

        };
        increaseMaximumSizeListeners.add(new IHistoricalCacheIncreaseMaximumSizeListener() {
            @Override
            public void increaseMaximumSize(final int maximumSize, final String reason) {
                loadingCache.increaseMaximumSize(maximumSize);
            }

            @Override
            public Integer getMaximumSize() {
                return null;
            }

            @Override
            public int getMaximumSizeLimit() {
                return AHistoricalCache.this.getMaximumSizeLimit();
            }
        });
        return loadingCache;
    }

    /**
     * Should return the key if the value does not contain a key itself.
     */
    public final FDate extractKey(final FDate key, final V value) {
        final FDate extractedKey = extractKeyProvider.extractKey(key, value);
        return adjustKeyProvider.newAlreadyAdjustedKey(extractedKey);
    }

    /**
     * This is only for internal purposes, use extractKey instead.
     */
    protected FDate innerExtractKey(final FDate key, final V value) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    protected final FDate calculatePreviousKey(final FDate key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null!");
        }
        final FDate prevKey = shiftKeyProvider.calculatePreviousKey(key);
        return adjustKeyProvider.newAlreadyAdjustedKey(prevKey);
    }

    protected final FDate calculateNextKey(final FDate key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null!");
        }
        final FDate nextKey = shiftKeyProvider.calculateNextKey(key);
        return adjustKeyProvider.adjustKey(nextKey);
    }

    public IHistoricalCacheShiftKeyProvider getShiftKeyProvider() {
        return shiftKeyProvider;
    }

    public IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider() {
        return adjustKeyProvider;
    }

    /**
     * This is only for internal purposes, use calculatePreviousKey instead.
     */
    protected FDate innerCalculatePreviousKey(final FDate key) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * This is only for internal purposes, use calculateNextKey instead.
     */
    protected FDate innerCalculateNextKey(final FDate key) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Does not allow values from future per default.
     */
    public final IHistoricalCacheQuery<V> query() {
        return new FilteringHistoricalCacheQuery<V>(queryCore, adjustKeyProvider.newQuery(queryCore));
    }

    public boolean containsKey(final FDate key) {
        return getValuesMap().containsKey(key);
    }

    /**
     * For internal use only. Removal is only required in very special cases.
     */
    @Deprecated
    public void remove(final FDate key) {
        getValuesMap().remove(key);
        if (shiftKeyProvider.getPreviousKeysCache().containsKey(key)) {
            final FDate previousKey = shiftKeyProvider.getPreviousKeysCache().get(key);
            shiftKeyProvider.getPreviousKeysCache().remove(previousKey);
        }
        shiftKeyProvider.getPreviousKeysCache().remove(key);
        shiftKeyProvider.getNextKeysCache().remove(key);
    }

    public void clear() {
        valuesMap.clear();
        //when clearning other caches they might become inconsistent...
        if (adjustKeyProvider.getParent() == this) {
            adjustKeyProvider.clear();
        }
        if (shiftKeyProvider.getParent() == this) {
            shiftKeyProvider.clear();
        }
        queryCore.clear();
        for (final IHistoricalCacheOnClearListener listener : onClearListeners) {
            listener.onClear();
        }
        lastRefresh = HistoricalCacheRefreshManager.getLastRefresh();
    }

    public Set<IHistoricalCacheOnClearListener> getOnClearListeners() {
        return Collections.unmodifiableSet(onClearListeners);
    }

    public boolean registerOnClearListener(final IHistoricalCacheOnClearListener l) {
        return onClearListeners.add(l);
    }

    public boolean unregisterOnClearListener(final IHistoricalCacheOnClearListener l) {
        return onClearListeners.remove(l);
    }

    public Set<IHistoricalCacheIncreaseMaximumSizeListener> getIncreaseMaximumSizeListeners() {
        return Collections.unmodifiableSet(increaseMaximumSizeListeners);
    }

    public boolean registerIncreaseMaximumSizeListener(final IHistoricalCacheIncreaseMaximumSizeListener l) {
        return increaseMaximumSizeListeners.add(l);
    }

    public boolean unregisterIncreaseMaximumSizeListener(final IHistoricalCacheIncreaseMaximumSizeListener l) {
        return increaseMaximumSizeListeners.remove(l);
    }

    protected IHistoricalCacheRangeQueryInterceptor<V> getRangeQueryInterceptor() {
        return new HistoricalCacheRangeQueryInterceptorSupport<V>();
    }

    public IHistoricalCachePreviousKeysQueryInterceptor getPreviousKeysQueryInterceptor() {
        return new HistoricalCachePreviousKeysQueryInterceptorSupport();
    }

    protected ILoadingCache<FDate, V> getValuesMap() {
        return valuesMap;
    }

    protected final FDate minKey() {
        return FDate.MIN_DATE;
    }

    protected final FDate maxKey() {
        return FDate.MAX_DATE;
    }

    private final class HistoricalCacheInternalMethods implements IHistoricalCacheInternalMethods<V> {
        @Override
        public IHistoricalCacheRangeQueryInterceptor<V> getRangeQueryInterceptor() {
            return AHistoricalCache.this.getRangeQueryInterceptor();
        }

        @Override
        public IHistoricalCachePreviousKeysQueryInterceptor getPreviousKeysQueryInterceptor() {
            return AHistoricalCache.this.getPreviousKeysQueryInterceptor();
        }

        @Override
        public FDate calculatePreviousKey(final FDate key) {
            return AHistoricalCache.this.calculatePreviousKey(key);
        }

        @Override
        public FDate calculateNextKey(final FDate key) {
            return AHistoricalCache.this.calculateNextKey(key);
        }

        @Override
        public ILoadingCache<FDate, V> getValuesMap() {
            return AHistoricalCache.this.getValuesMap();
        }

        @Override
        public FDate adjustKey(final FDate key) {
            return AHistoricalCache.this.adjustKey(key);
        }

        @Override
        public void remove(final FDate key) {
            AHistoricalCache.this.remove(key);
        }

        @Override
        public FDate extractKey(final FDate key, final V value) {
            return AHistoricalCache.this.extractKey(key, value);
        }

        @Override
        public Integer getMaximumSize() {
            return AHistoricalCache.this.getMaximumSize();
        }

        @Override
        public void increaseMaximumSize(final int maximumSize, final String reason) {
            AHistoricalCache.this.increaseMaximumSize(maximumSize, reason);
        }

        @Override
        public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
            return AHistoricalCache.this.getShiftKeyProvider().newKeysQueryInterceptor();
        }

        @Override
        public String toString() {
            return AHistoricalCache.this.toString();
        }

        @Override
        public V computeValue(final FDate key) {
            return AHistoricalCache.this.loadValue(key);
        }

        @Override
        public Object getLock() {
            return AHistoricalCache.this;
        }

        @Override
        public IHistoricalCachePutProvider<V> getPutProvider() {
            return AHistoricalCache.this.getPutProvider();
        }

    }

    private final class InnerHistoricalCacheExtractKeyProvider implements IHistoricalCacheExtractKeyProvider<V> {

        @Override
        public FDate extractKey(final FDate key, final V value) {
            return innerExtractKey(key, value);
        }

    }

    private final class InnerHistoricalCacheShiftKeyProvider implements IHistoricalCacheShiftKeyProvider {

        private final ILoadingCache<FDate, FDate> previousKeysCache = new ADelegateLoadingCache<FDate, FDate>() {

            @Override
            public FDate get(final FDate key) {
                invokeRefreshIfRequested();
                return super.get(key);
            }

            @Override
            public void put(final FDate key, final FDate value) {
                //don't cache null values to prevent moving time issues of the underlying source (e.g. JForexTickCache getNextValue)
                if (value != null && !key.equals(value)) {
                    super.put(key, value);
                }
            }

            @Override
            protected ILoadingCache<FDate, FDate> createDelegate() {
                return newLoadingCacheProvider(new Function<FDate, FDate>() {
                    @Override
                    public FDate apply(final FDate key) {
                        return innerCalculatePreviousKey(key);
                    }
                }, getMaximumSize());
            }

        };

        private final ILoadingCache<FDate, FDate> nextKeysCache = new ADelegateLoadingCache<FDate, FDate>() {

            @Override
            public FDate get(final FDate key) {
                invokeRefreshIfRequested();
                final FDate value = super.get(key);
                if (value == null || key.equals(value)) {
                    remove(key);
                }
                return value;
            }

            @Override
            public void put(final FDate key, final FDate value) {
                //don't cache null values to prevent moving time issues of the underlying source (e.g. JForexTickCache getNextValue)
                if (value != null && !key.equals(value)) {
                    super.put(key, value);
                }
            }

            @Override
            protected ILoadingCache<FDate, FDate> createDelegate() {
                return newLoadingCacheProvider(new Function<FDate, FDate>() {
                    @Override
                    public FDate apply(final FDate key) {
                        return innerCalculateNextKey(key);
                    }
                }, getMaximumSize());
            }
        };

        @Override
        public FDate calculatePreviousKey(final FDate key) {
            return previousKeysCache.get(key);
        }

        @Override
        public FDate calculateNextKey(final FDate key) {
            return nextKeysCache.get(key);
        }

        @Override
        public ILoadingCache<FDate, FDate> getPreviousKeysCache() {
            return previousKeysCache;
        }

        @Override
        public ILoadingCache<FDate, FDate> getNextKeysCache() {
            return nextKeysCache;
        }

        @Override
        public void clear() {
            previousKeysCache.clear();
            nextKeysCache.clear();
        }

        @Override
        public AHistoricalCache<?> getParent() {
            return AHistoricalCache.this;
        }

        @Override
        public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
            return null;
        }

    }

    private final class InnerHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

        @Override
        public FDate adjustKey(final FDate key) {
            return key;
        }

        @Override
        public FDate newAlreadyAdjustedKey(final FDate key) {
            return key;
        }

        @Override
        public FDate maybeAdjustKey(final FDate key) {
            return key;
        }

        @Override
        public void clear() {}

        @Override
        public FDate getHighestAllowedKey() {
            return null;
        }

        @Override
        public boolean registerHistoricalCache(final AHistoricalCache<?> historicalCcache) {
            return true;
        }

        @Override
        public AHistoricalCache<?> getParent() {
            return AHistoricalCache.this;
        }

        @Override
        public <T> IHistoricalCacheQuery<T> newQuery(final IHistoricalCacheQueryCore<T> queryCore) {
            return new HistoricalCacheQuery<T>(queryCore);
        }

        @Override
        public boolean isAlreadyAdjustingKey() {
            return APullingHistoricalCacheAdjustKeyProvider.isGlobalAlreadyAdjustingKey();
        }

    }

    private final class InnerHistoricalCachePutProvider implements IHistoricalCachePutProvider<V> {

        private final Set<IHistoricalCachePutListener> putListeners = newListenerSet();
        @SuppressWarnings("rawtypes")
        private final AFastIterableDelegateList<WeakReference> putListenersFast = new AFastIterableDelegateList<WeakReference>() {
            @Override
            protected List<WeakReference> newDelegate() {
                return new ArrayList<>();
            }
        };

        @Override
        public void put(final FDate newKey, final V newValue, final FDate prevKey, final V prevValue) {
            if (isPutDisabled) {
                return;
            }
            if (newValue != null) {
                if (prevValue != null) {
                    putPrevAndNext(newKey, prevKey, prevValue, null);
                    putPrevAndNext(null, newKey, newValue, prevKey);
                } else {
                    putPrevAndNext(null, newKey, newValue, null);
                }
            }
        }

        @Override
        public void put(final V newValue, final V prevValue) {
            if (isPutDisabled) {
                return;
            }
            if (newValue != null) {
                final FDate newKey = extractKey(null, newValue);
                if (prevValue != null) {
                    final FDate prevKey = extractKey(null, prevValue);
                    putPrevAndNext(newKey, prevKey, prevValue, null);
                    putPrevAndNext(null, newKey, newValue, prevKey);
                } else {
                    putPrevAndNext(null, newKey, newValue, null);
                }
            }
        }

        @Override
        public void put(final Entry<FDate, V> newEntry, final Entry<FDate, V> prevEntry) {
            if (isPutDisabled) {
                return;
            }
            if (newEntry != null) {
                final V newValue = newEntry.getValue();
                if (newValue != null) {
                    final FDate newKey = newEntry.getKey();
                    if (prevEntry != null) {
                        final FDate prevKey = prevEntry.getKey();
                        final V prevValue = prevEntry.getValue();
                        putPrevAndNext(newKey, prevKey, prevValue, null);
                        putPrevAndNext(null, newKey, newValue, prevKey);
                    } else {
                        putPrevAndNext(null, newKey, newValue, null);
                    }
                }
            }
        }

        private void putPrevAndNext(final FDate nextKey, final FDate valueKey, final V value, final FDate previousKey) {
            if (previousKey != null && nextKey != null) {
                if (!(previousKey.compareTo(nextKey) <= 0)) {
                    throw new IllegalArgumentException(new TextDescription(
                            "%s: previousKey [%s] <= nextKey [%s] not matched", this, previousKey, nextKey).toString());
                }
            }
            getValuesMap().put(valueKey, value);
            if (previousKey != null) {
                putPrevious(previousKey, value, valueKey);
            }
            if (nextKey != null) {
                putNext(nextKey, value, valueKey);
            }
        }

        @SuppressWarnings("rawtypes")
        private void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
            final int compare = previousKey.compareTo(valueKey);
            if (!(compare <= 0)) {
                throw new IllegalArgumentException(new TextDescription("%s: previousKey [%s] <= value [%s] not matched",
                        this, previousKey, valueKey).toString());
            }
            if (compare != 0) {
                shiftKeyProvider.getPreviousKeysCache().put(valueKey, previousKey);
                shiftKeyProvider.getNextKeysCache().put(previousKey, valueKey);
                queryCore.putPrevious(previousKey, value, valueKey);
                if (!putListenersFast.isEmpty()) {
                    final WeakReference[] array = putListenersFast.asArray(WeakReference.class);
                    for (int i = 0, fastIndex = 0; i < array.length; i++, fastIndex++) {
                        final IHistoricalCachePutListener l = (IHistoricalCachePutListener) array[i].get();
                        if (l == null) {
                            putListenersFast.remove(fastIndex);
                            fastIndex--;
                        } else {
                            l.putPreviousKey(previousKey, valueKey);
                        }
                    }
                }
            }
        }

        private void putNext(final FDate nextKey, final V value, final FDate valueKey) {
            final int compare = nextKey.compareTo(valueKey);
            if (!(compare >= 0)) {
                throw new IllegalArgumentException(
                        new TextDescription("%s: nextKey [%s] >= value [%s] not matched", this, nextKey, valueKey)
                                .toString());
            }
            if (compare != 0) {
                shiftKeyProvider.getNextKeysCache().put(valueKey, nextKey);
                shiftKeyProvider.getPreviousKeysCache().put(nextKey, valueKey);
            }
        }

        @Override
        public Set<IHistoricalCachePutListener> getPutListeners() {
            return Collections.unmodifiableSet(putListeners);
        }

        @Override
        public boolean registerPutListener(final IHistoricalCachePutListener l) {
            if (putListeners.add(l)) {
                putListenersFast.add(new WeakReference<IHistoricalCachePutListener>(l));
                return true;
            } else {
                return false;
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean unregisterPutListener(final IHistoricalCachePutListener l) {
            if (putListeners.remove(l)) {
                final WeakReference[] array = putListenersFast.asArray(WeakReference.class);
                for (int i = 0; i < array.length; i++) {
                    final IHistoricalCachePutListener existing = (IHistoricalCachePutListener) array[i].get();
                    if (existing == l) {
                        putListenersFast.remove(i);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void putPreviousKey(final FDate previousKey, final FDate valueKey) {
        queryCore.putPreviousKey(previousKey, valueKey);
    }

}
