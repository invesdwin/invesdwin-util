package de.invesdwin.util.collections.loadingcache.historical;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.eviction.EvictionMode;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateList;
import de.invesdwin.util.collections.loadingcache.ADelegateLoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.DelegateLoadingCache;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.RangeHistoricalCacheNextQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.DisabledHistoricalCacheNextQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.DisabledHistoricalCachePreviousKeysQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.DisabledHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheNextQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCachePreviousKeysQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.key.APullingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCachePutProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheIncreaseMaximumSizeListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnClearListener;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCachePutListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQueryWithFuture;
import de.invesdwin.util.collections.loadingcache.historical.query.index.IndexedFDate;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.AFilteringDelegateHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.CachedHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.TrailingHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.filter.FilteringHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.refresh.HistoricalCacheRefreshManager;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.IFDateProvider;

@ThreadSafe
public abstract class AHistoricalCache<V> implements IHistoricalCache<V> {

    public static final Integer DISABLED_MAXIMUM_SIZE = 0;
    public static final Integer UNLIMITED_MAXIMUM_SIZE = null;
    /**
     * 10k is normally sufficient for daily bars of stocks and also fast enough for intraday ticks to load.
     */
    public static final Integer DEFAULT_MAXIMUM_SIZE = 100;
    public static final int DEFAULT_MAXIMUM_SIZE_LIMIT = 10_000;
    public static final EvictionMode EVICTION_MODE = EvictionMode.LeastRecentlyAdded;
    public static final boolean DEFAULT_ALIGN_KEYS = false;

    private static final WeakReference[] WEAKREFERENCE_EMPTY_ARRAY = new WeakReference[0];
    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AHistoricalCache.class);
    private static boolean debugAutomaticReoptimization = false;

    protected final IHistoricalCacheInternalMethods<V> internalMethods = new HistoricalCacheInternalMethods();

    private IHistoricalCacheQueryCore<V> queryCore = newQueryCore();
    private final AFilteringDelegateHistoricalCacheQueryCore<V> filteringQueryCore = new AFilteringDelegateHistoricalCacheQueryCore<V>() {
        @Override
        protected IHistoricalCacheQueryCore<V> getDelegate() {
            return queryCore;
        }
    };
    private IHistoricalCacheAdjustKeyProvider adjustKeyProvider = new InnerHistoricalCacheAdjustKeyProvider();
    private final Set<IHistoricalCacheOnClearListener> onClearListeners = newListenerSet();
    private final Set<IHistoricalCacheIncreaseMaximumSizeListener> increaseMaximumSizeListeners = newListenerSet();

    private IHistoricalCachePutProvider<V> putProvider = new InnerHistoricalCachePutProvider();
    private boolean isPutDisabled = getMaximumSize() != null && getMaximumSize() == 0;

    private volatile long lastRefreshMillis = HistoricalCacheRefreshManager.getLastRefresh().millisValue();
    private volatile Integer maximumSize = getInitialMaximumSize();
    /*
     * need to remember this, so that valuesMap lazy initialization uses the correct impl, since actual maximumSize
     * might increase afterwards before lazy init
     */
    private final Integer initialMaximumSize = maximumSize;
    private IHistoricalCacheShiftKeyProvider<V> shiftKeyProvider = new InnerHistoricalCacheShiftKeyProvider();
    private IHistoricalCacheExtractKeyProvider<V> extractKeyProvider = new InnerHistoricalCacheExtractKeyProvider();
    @GuardedBy("this only during initialization")
    private IValuesMap<V> valuesMap = new LazyValuesMap();
    private boolean alignKeys = DEFAULT_ALIGN_KEYS;
    @GuardedBy("none for performance")
    private IHistoricalCacheRangeQueryInterceptor<V> rangeQueryInterceptor;
    @GuardedBy("none for performance")
    private IHistoricalCachePreviousKeysQueryInterceptor previousKeysQueryInterceptor;
    @GuardedBy("none for performance")
    private IHistoricalCacheNextQueryInterceptor<V> nextQueryInterceptor;

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

    /**
     * null means unlimited and 0 means no caching at all.
     */
    protected Integer getInitialMaximumSize() {
        return DEFAULT_MAXIMUM_SIZE;
    }

    /**
     * Return false to get a faster implementation
     */
    @Override
    public boolean isThreadSafe() {
        return true;
    }

    @Override
    public final Integer getMaximumSize() {
        return maximumSize;
    }

    @Override
    public boolean isCachingEnabled() {
        return !isPutDisabled;
    }

    @Override
    public final void increaseMaximumSize(final int maximumSize, final String reason) {
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
            if (getMaximumSize() != null && getMaximumSize() > 0 && LOG.isDebugEnabled()) {
                LOG.debug(this + ": Increasing maximum size from [" + this.maximumSize + "] to [" + maximumSize
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

    /**
     * Use a different type of query core that works faster with limited unstable recursive queries, but minimally
     * slower with normal queries.
     */
    @Override
    public void enableTrailingQueryCore() {
        if (!(queryCore instanceof TrailingHistoricalCacheQueryCore)) {
            queryCore = new TrailingHistoricalCacheQueryCore<>(internalMethods);
            if (getShiftKeyProvider().getParent() != this) {
                getShiftKeyProvider().getParent().enableTrailingQueryCore();
            }
        }
    }

    protected IHistoricalCacheQueryCore<V> newQueryCore() {
        /*
         * always use lookback cache to make getPreviousXyz faster even though this instance might not cache anything in
         * the values map
         */
        return new CachedHistoricalCacheQueryCore<V>(internalMethods);
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

    protected void setShiftKeyDelegate(final IHistoricalCache<?> shiftKeyDelegate, final boolean alsoExtractKey) {
        if (isSetShiftKeyDelegateNeeded(shiftKeyDelegate)) {
            Assertions.assertThat(shiftKeyDelegate).as("Use null instead of this").isNotSameAs(this);
            Assertions.assertThat(this.shiftKeyProvider)
                    .as("%s can only be set once", IHistoricalCacheShiftKeyProvider.class.getSimpleName())
                    .isInstanceOf(InnerHistoricalCacheShiftKeyProvider.class);
            this.shiftKeyProvider = DelegateHistoricalCacheShiftKeyProvider.maybeWrap(internalMethods,
                    shiftKeyDelegate);
            //propagate the maximum size setting downwards without risking an endless recursion
            registerIncreaseMaximumSizeListener(shiftKeyDelegate);
            //and upwards
            shiftKeyDelegate.registerIncreaseMaximumSizeListener(this);
            isPutDisabled = false;
            if (alsoExtractKey && isSetExtractKeyDelegateNeeded(shiftKeyDelegate)) {
                this.extractKeyProvider = DelegateHistoricalCacheExtractKeyProvider.maybeWrap(shiftKeyDelegate);
            }
        } else {
            final boolean alsoExtractKeyBefore = !isSetExtractKeyDelegateNeeded(shiftKeyDelegate);
            Assertions.assertThat(alsoExtractKey)
                    .as("Same shiftKeyDelegate was already set with alsoExtractKeyBefore=%s and now it is requested again with alsoExtractKey=%s",
                            alsoExtractKeyBefore, alsoExtractKey)
                    .isEqualTo(alsoExtractKeyBefore);
        }
    }

    private boolean isSetExtractKeyDelegateNeeded(final IHistoricalCache<?> shiftKeyDelegate) {
        if (this.extractKeyProvider instanceof DelegateHistoricalCacheExtractKeyProvider) {
            final DelegateHistoricalCacheExtractKeyProvider<V> cShiftKeyProvider = (DelegateHistoricalCacheExtractKeyProvider<V>) this.extractKeyProvider;
            if (cShiftKeyProvider.getDelegate() == shiftKeyDelegate) {
                return false;
            }
        }
        return true;
    }

    private boolean isSetShiftKeyDelegateNeeded(final IHistoricalCache<?> shiftKeyDelegate) {
        if (this.shiftKeyProvider instanceof DelegateHistoricalCacheShiftKeyProvider) {
            final DelegateHistoricalCacheShiftKeyProvider<V> cShiftKeyProvider = (DelegateHistoricalCacheShiftKeyProvider<V>) this.shiftKeyProvider;
            if (cShiftKeyProvider.getParent() == shiftKeyDelegate) {
                return false;
            }
        }
        return true;
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

    @Override
    public final IHistoricalCachePutProvider<V> getPutProvider() {
        return putProvider;
    }

    @Override
    public IHistoricalCacheExtractKeyProvider<V> getExtractKeyProvider() {
        return extractKeyProvider;
    }

    protected FDate adjustKey(final FDate key) {
        return adjustKeyProvider.maybeAdjustKey(key);
    }

    protected FDate getHighestAllowedKey() {
        return adjustKeyProvider.getHighestAllowedKey();
    }

    protected boolean isAdjustedKey(final FDate key) {
        return adjustKeyProvider.isAdjustedKey(key);
    }

    /**
     * Keys should be aligned when cache misses are especially expensive. E.g. for recursive queries.
     */
    @Override
    public void setAlignKeys(final boolean alignKeys) {
        this.alignKeys = alignKeys;
    }

    @Override
    public boolean isAlignKeys() {
        return alignKeys;
    }

    @Override
    public long getLastRefreshMillis() {
        return lastRefreshMillis;
    }

    protected void invokeRefreshIfRequested() {
        final long lastRefreshMillisFromManager = HistoricalCacheRefreshManager.getLastRefreshMillis();
        if (lastRefreshMillis < lastRefreshMillisFromManager) {
            clear();
        }
    }

    protected abstract IEvaluateGenericFDate<V> newLoadValue();

    protected <T> ILoadingCache<FDate, T> newLoadingCacheProvider(final Function<FDate, T> loadValue,
            final Integer maximumSize, final boolean threadSafe) {
        final ALoadingCache<FDate, T> loadingCache = new ALoadingCache<FDate, T>() {

            @Override
            protected Integer getInitialMaximumSize() {
                return maximumSize;
            }

            @Override
            protected boolean isThreadSafe() {
                return threadSafe;
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

            @Override
            public boolean isCachingEnabled() {
                return AHistoricalCache.this.isCachingEnabled();
            }
        });
        return loadingCache;
    }

    /**
     * Should return the key if the value does not contain a key itself. The time should be the end time for bars.
     */
    @Override
    public final FDate extractKey(final IFDateProvider key, final V value) {
        if (key == null) {
            final FDate extractedKey;
            if (value instanceof IHistoricalEntry) {
                final IHistoricalEntry<?> cValue = (IHistoricalEntry<?>) value;
                extractedKey = cValue.getKey();
            } else if (value instanceof IHistoricalValue) {
                final IHistoricalValue<?> cValue = (IHistoricalValue<?>) value;
                extractedKey = cValue.asHistoricalEntry().getKey();
            } else {
                extractedKey = extractKeyProvider.extractKey(key, value);
            }
            return adjustKeyProvider.newAlreadyAdjustedKey(extractedKey);
        } else {
            final IndexedFDate indexedKey = IndexedFDate.maybeWrap(key);
            final FDate extractedKey = indexedKey.maybeExtractKey(extractKeyProvider, adjustKeyProvider, value);
            return extractedKey;
        }
    }

    /**
     * This is only for internal purposes, use extractKey instead.
     */
    protected FDate innerExtractKey(final V value) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    protected final FDate calculatePreviousKey(final FDate key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null!");
        }
        final FDate prevKey = shiftKeyProvider.calculatePreviousKey(key);
        return prevKey;
    }

    protected final FDate calculateNextKey(final FDate key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null!");
        }
        final FDate nextKey = shiftKeyProvider.calculateNextKey(key);
        return nextKey;
    }

    @Override
    public IHistoricalCacheShiftKeyProvider<V> getShiftKeyProvider() {
        return shiftKeyProvider;
    }

    @Override
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
    @Override
    public final IHistoricalCacheQuery<V> query() {
        return new FilteringHistoricalCacheQuery<V>(internalMethods, adjustKeyProvider.newQuery(internalMethods));
    }

    @Override
    public boolean containsKey(final FDate key) {
        return getValuesMap().containsKey(key);
    }

    /**
     * For internal use only. Removal is only required in very special cases.
     */
    @Deprecated
    public void remove(final FDate key) {
        getValuesMap().remove(key);
    }

    @Override
    public void clear() {
        if (valuesMap != null) {
            if (!valuesMap.isEmpty()) {
                valuesMap.clear();
            }
        }
        //when clearing other caches they might become inconsistent...
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
        lastRefreshMillis = HistoricalCacheRefreshManager.getLastRefreshMillis();
    }

    @Override
    public Set<IHistoricalCacheOnClearListener> getOnClearListeners() {
        return Collections.unmodifiableSet(onClearListeners);
    }

    @Override
    public boolean registerOnClearListener(final IHistoricalCacheOnClearListener l) {
        return onClearListeners.add(l);
    }

    @Override
    public boolean unregisterOnClearListener(final IHistoricalCacheOnClearListener l) {
        return onClearListeners.remove(l);
    }

    @Override
    public Set<IHistoricalCacheIncreaseMaximumSizeListener> getIncreaseMaximumSizeListeners() {
        return Collections.unmodifiableSet(increaseMaximumSizeListeners);
    }

    @Override
    public boolean registerIncreaseMaximumSizeListener(final IHistoricalCacheIncreaseMaximumSizeListener l) {
        if (l == this) {
            return false;
        }
        if (increaseMaximumSizeListeners.add(l)) {
            // propagate setting directly (does not cause an endless loop here)
            l.increaseMaximumSize(getMaximumSize(), "setShiftKeyDelegate");
            increaseMaximumSize(l.getMaximumSize(), "setShiftKeyDelegate");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unregisterIncreaseMaximumSizeListener(final IHistoricalCacheIncreaseMaximumSizeListener l) {
        return increaseMaximumSizeListeners.remove(l);
    }

    protected final IHistoricalCacheRangeQueryInterceptor<V> getRangeQueryInterceptor() {
        if (rangeQueryInterceptor == null) {
            rangeQueryInterceptor = newRangeQueryInterceptor();
        }
        return rangeQueryInterceptor;
    }

    protected IHistoricalCacheRangeQueryInterceptor<V> newRangeQueryInterceptor() {
        return DisabledHistoricalCacheRangeQueryInterceptor.getInstance();
    }

    protected final IHistoricalCachePreviousKeysQueryInterceptor getPreviousKeysQueryInterceptor() {
        if (previousKeysQueryInterceptor == null) {
            previousKeysQueryInterceptor = newPreviousKeysQueryInterceptor();
        }
        return previousKeysQueryInterceptor;
    }

    protected IHistoricalCachePreviousKeysQueryInterceptor newPreviousKeysQueryInterceptor() {
        return DisabledHistoricalCachePreviousKeysQueryInterceptor.getInstance();
    }

    protected final IHistoricalCacheNextQueryInterceptor<V> getNextQueryInterceptor() {
        if (nextQueryInterceptor == null) {
            nextQueryInterceptor = newNextQueryInterceptor();
        }
        return nextQueryInterceptor;
    }

    protected IHistoricalCacheNextQueryInterceptor<V> newNextQueryInterceptor() {
        final IHistoricalCacheRangeQueryInterceptor<V> rangeQueryInterceptor = getRangeQueryInterceptor();
        if (rangeQueryInterceptor instanceof DisabledHistoricalCacheRangeQueryInterceptor) {
            return DisabledHistoricalCacheNextQueryInterceptor.getInstance();
        } else {
            return new RangeHistoricalCacheNextQueryInterceptor<V>(rangeQueryInterceptor);
        }
    }

    protected IValuesMap<V> getValuesMap() {
        return valuesMap;
    }

    protected final FDate minKey() {
        return FDates.MIN_DATE;
    }

    protected final FDate maxKey() {
        return FDates.MAX_DATE;
    }

    private final class LazyValuesMap extends DelegateLoadingCache<FDate, IHistoricalEntry<V>>
            implements IValuesMap<V> {

        private LazyValuesMap() {
            super(null);
        }

        @Override
        protected IValuesMap<V> getDelegate() {
            /*
             * initialize lazy, so that caches that are never used occupy less memory and so that newLoadValue is called
             * only after constructor
             */
            if (valuesMap == this) {
                synchronized (AHistoricalCache.this) {
                    if (valuesMap == this) {
                        valuesMap = newValuesMap();
                        if (maximumSize != null && maximumSize != initialMaximumSize) {
                            innerIncreaseMaximumSize(maximumSize, "innerLoadCache lazy init");
                        }
                    }
                }
            }
            return valuesMap;
        }

        @Override
        public void putDirectly(final FDate key, final IHistoricalEntry<V> value) {
            getDelegate().putDirectly(key, value);
        }

        @Override
        public boolean isEmpty() {
            if (valuesMap == this) {
                return true;
            }
            return super.isEmpty();
        }

        @Override
        public void clear() {
            if (valuesMap == this) {
                return;
            }
            super.clear();
        }
    }

    public class ValuesMap extends ADelegateLoadingCache<FDate, IHistoricalEntry<V>> implements IValuesMap<V> {

        @Override
        public IHistoricalEntry<V> get(final FDate key) {
            invokeRefreshIfRequested();
            return super.get(key);
        }

        protected IHistoricalEntry<V> superGet(final FDate key) {
            return super.get(key);
        }

        @Override
        protected ILoadingCache<FDate, IHistoricalEntry<V>> newDelegate() {
            final Integer size = initialMaximumSize;
            final boolean threadSafe = isThreadSafe();
            final IEvaluateGenericFDate<V> loadValueF = internalMethods.newLoadValue();
            return newLoadingCacheProvider(key -> {
                try {
                    final V value = loadValueF.evaluateGeneric(key);
                    return shiftKeyProvider.maybeWrap(key, value);
                } catch (final Throwable t) {
                    throw new RuntimeException("At: " + AHistoricalCache.this.toString(), t);
                }
            }, size, threadSafe);
        }

        @Override
        public void put(final FDate key, final IHistoricalEntry<V> value) {
            shiftKeyProvider.put(key, value);
        }

        @Override
        public void putDirectly(final FDate key, final IHistoricalEntry<V> value) {
            super.put(key, value);
        }
    }

    private final class HistoricalCacheInternalMethods implements IHistoricalCacheInternalMethods<V> {

        private IEvaluateGenericFDate<V> loadValueF;
        private IEvaluateGenericFDate<IHistoricalEntry<V>> computeEntryF;

        @Override
        public IHistoricalCacheRangeQueryInterceptor<V> getRangeQueryInterceptor() {
            return AHistoricalCache.this.getRangeQueryInterceptor();
        }

        @Override
        public IHistoricalCachePreviousKeysQueryInterceptor getPreviousKeysQueryInterceptor() {
            return AHistoricalCache.this.getPreviousKeysQueryInterceptor();
        }

        @Override
        public IHistoricalCacheNextQueryInterceptor<V> getNextQueryInterceptor() {
            return AHistoricalCache.this.getNextQueryInterceptor();
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
        public IValuesMap<V> getValuesMap() {
            return AHistoricalCache.this.getValuesMap();
        }

        @Override
        public FDate adjustKey(final FDate key) {
            return AHistoricalCache.this.adjustKey(key);
        }

        @Override
        public FDate getHighestAllowedKey() {
            return AHistoricalCache.this.getHighestAllowedKey();
        }

        @Override
        public boolean isAdjustedKey(final FDate key) {
            return AHistoricalCache.this.isAdjustedKey(key);
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
        public boolean isThreadSafe() {
            return AHistoricalCache.this.isThreadSafe();
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
        public IEvaluateGenericFDate<IHistoricalEntry<V>> newComputeEntry() {
            if (computeEntryF == null) {
                final IEvaluateGenericFDate<V> loadValueF = newLoadValue();
                computeEntryF = (key) -> {
                    final V value = loadValueF.evaluateGeneric(key);
                    return ImmutableHistoricalEntry.maybeExtractKey(AHistoricalCache.this, key, value);
                };
            }
            return computeEntryF;
        }

        @Override
        public IHistoricalCachePutProvider<V> getPutProvider() {
            return AHistoricalCache.this.getPutProvider();
        }

        @Override
        public IHistoricalCacheQueryCore<V> getQueryCore() {
            return AHistoricalCache.this.filteringQueryCore;
        }

        @Override
        public IEvaluateGenericFDate<V> newLoadValue() {
            if (loadValueF == null) {
                loadValueF = AHistoricalCache.this.newLoadValue();
            }
            return loadValueF;
        }

        @Override
        public FDate innerCalculatePreviousKey(final FDate key) {
            return AHistoricalCache.this.innerCalculatePreviousKey(key);
        }

        @Override
        public FDate innerCalculateNextKey(final FDate key) {
            return AHistoricalCache.this.innerCalculateNextKey(key);
        }

        @Override
        public IHistoricalCacheAdjustKeyProvider getAdjustKeyProvider() {
            return AHistoricalCache.this.adjustKeyProvider;
        }

        @Override
        public void invokeRefreshIfRequested() {
            AHistoricalCache.this.invokeRefreshIfRequested();
        }

        @Override
        public void putDirectly(final FDate key, final IHistoricalEntry<V> value) {
            getValuesMap().putDirectly(key, value);
        }

        @Override
        public AHistoricalCache<V> getParent() {
            return AHistoricalCache.this;
        }

        @Override
        public boolean isAlignKeys() {
            return AHistoricalCache.this.isAlignKeys();
        }

    }

    private final class InnerHistoricalCacheExtractKeyProvider implements IHistoricalCacheExtractKeyProvider<V> {

        private final int hashCode = super.hashCode();

        @Override
        public FDate extractKey(final IFDateProvider key, final V value) {
            final FDate innerExtractKey = innerExtractKey(value);
            final IndexedFDate indexed = IndexedFDate.maybeWrap(innerExtractKey);
            indexed.putExtractedKey(extractKeyProvider, adjustKeyProvider);
            return indexed;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj.hashCode() == hashCode;
        }

    }

    private final class InnerHistoricalCacheShiftKeyProvider implements IHistoricalCacheShiftKeyProvider<V> {

        private final Function<FDate, IHistoricalEntry<V>> computeEmpty = new Function<FDate, IHistoricalEntry<V>>() {
            @Override
            public IHistoricalEntry<V> apply(final FDate t) {
                return new IndexedHistoricalEntry<>(internalMethods, t);
            }
        };

        @Override
        public FDate calculatePreviousKey(final FDate key) {
            final IndexedHistoricalEntry<V> entry = (IndexedHistoricalEntry<V>) getValuesMap().computeIfAbsent(key,
                    computeEmpty);
            return entry.getPrevKey();
        }

        @Override
        public FDate calculateNextKey(final FDate key) {
            final IndexedHistoricalEntry<V> entry = (IndexedHistoricalEntry<V>) getValuesMap().computeIfAbsent(key,
                    computeEmpty);
            return entry.getNextKey();
        }

        @Override
        public void clear() {}

        @Override
        public AHistoricalCache<?> getParent() {
            return AHistoricalCache.this;
        }

        @Override
        public IHistoricalCacheQuery<?> newKeysQueryInterceptor() {
            return null;
        }

        @Override
        public IHistoricalEntry<V> maybeWrap(final FDate key, final V value) {
            return IndexedHistoricalEntry.maybeExtractKey(internalMethods, key, value);
        }

        @Override
        public IHistoricalEntry<V> maybeWrap(final FDate key, final IHistoricalEntry<V> value) {
            return IndexedHistoricalEntry.maybeExtractKey(internalMethods, key, value);
        }

        @Override
        public IHistoricalEntry<V> put(final FDate previousKey, final FDate valueKey, final V value,
                final IHistoricalEntry<V> shiftKeyValueEntry, final FDate nextKey) {
            final IndexedHistoricalEntry<V> entry;
            if (shiftKeyValueEntry != null) {
                //we can skip setting the value again here
                entry = (IndexedHistoricalEntry<V>) shiftKeyValueEntry;
            } else {
                entry = (IndexedHistoricalEntry<V>) getValuesMap().computeIfAbsent(valueKey, computeEmpty);
                if (value != null) {
                    entry.setValue(valueKey, value);
                }
            }
            if (previousKey != null) {
                entry.setPrevKey(previousKey);
            }
            if (nextKey != null) {
                entry.setNextKey(nextKey);
            }
            return entry;
        }

        @Override
        public IHistoricalEntry<V> put(final FDate key, final IHistoricalEntry<V> value) {
            final IndexedHistoricalEntry<V> entry = (IndexedHistoricalEntry<V>) getValuesMap().computeIfAbsent(key,
                    computeEmpty);
            final V valueIfPresent = value.getValueIfPresent();
            if (valueIfPresent != null) {
                entry.setValue(key, valueIfPresent);
            }
            return entry;
        }

        @Override
        public IHistoricalEntry<V> put(final FDate key, final V value) {
            final IndexedHistoricalEntry<V> entry = (IndexedHistoricalEntry<V>) getValuesMap().computeIfAbsent(key,
                    computeEmpty);
            if (value != null) {
                entry.setValue(key, value);
            }
            return entry;
        }

    }

    private final class InnerHistoricalCacheAdjustKeyProvider implements IHistoricalCacheAdjustKeyProvider {

        @Override
        public FDate adjustKey(final FDate key) {
            return key;
        }

        @Override
        public boolean isAdjustedKey(final FDate key) {
            return true;
        }

        @Override
        public FDate maybeAdjustKey(final FDate key) {
            return key;
        }

        @Override
        public FDate newAlreadyAdjustedKey(final FDate key) {
            return key;
        }

        @Override
        public void clear() {}

        @Override
        public FDate getHighestAllowedKey() {
            return null;
        }

        @Override
        public FDate getPreviousHighestAllowedKey() {
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
        public <T> IHistoricalCacheQuery<T> newQuery(final IHistoricalCacheInternalMethods<T> internalMethods) {
            return new HistoricalCacheQuery<T>(internalMethods);
        }

        @Override
        public boolean isAlreadyAdjustingKey() {
            return APullingHistoricalCacheAdjustKeyProvider.isGlobalAlreadyAdjustingKey();
        }

    }

    private final class InnerHistoricalCachePutProvider implements IHistoricalCachePutProvider<V> {

        private final Set<IHistoricalCachePutListener> putListeners = newListenerSet();
        @SuppressWarnings("rawtypes")
        private final ASynchronizedFastIterableDelegateList<WeakReference> putListenersFast = new ASynchronizedFastIterableDelegateList<WeakReference>() {
            @Override
            protected List<WeakReference> newDelegate() {
                return new ArrayList<>();
            }
        };

        @Override
        public void put(final FDate newKey, final V newValue, final FDate prevKey, final V prevValue,
                final boolean notifyPutListeners) {
            if (isPutDisabled) {
                return;
            }
            if (newValue != null) {
                if (prevValue != null) {
                    putPrevAndNext(newKey, prevKey, prevValue, null, false);
                    //notifyPutListeners only relevant for putPrevious
                    putPrevAndNext(null, newKey, newValue, prevKey, notifyPutListeners);
                } else {
                    putPrevAndNext(null, newKey, newValue, null, false);
                }
            }
        }

        @Override
        public void put(final V newValue, final V prevValue, final boolean notifyPutListeners) {
            if (isPutDisabled) {
                return;
            }
            if (newValue != null) {
                final FDate newKey = extractKey(null, newValue);
                if (prevValue != null) {
                    final FDate prevKey = extractKey(null, prevValue);
                    putPrevAndNext(newKey, prevKey, prevValue, null, false);
                    //notifyPutListeners only relevant for putPrevious
                    putPrevAndNext(null, newKey, newValue, prevKey, notifyPutListeners);
                } else {
                    putPrevAndNext(null, newKey, newValue, null, false);
                }
            }
        }

        @Override
        public void put(final Entry<FDate, V> newEntry, final Entry<FDate, V> prevEntry,
                final boolean notifyPutListeners) {
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
                        putPrevAndNext(newKey, prevKey, prevValue, null, false);
                        //notifyPutListeners only relevant for putPrevious
                        putPrevAndNext(null, newKey, newValue, prevKey, notifyPutListeners);
                    } else {
                        putPrevAndNext(null, newKey, newValue, null, false);
                    }
                }
            }
        }

        private void putPrevAndNext(final FDate nextKey, final FDate valueKey, final V value, final FDate previousKey,
                final boolean notifyPutListeners) {
            if (previousKey != null && nextKey != null) {
                if (!(previousKey.compareTo(nextKey) <= 0)) {
                    throw new IllegalArgumentException(TextDescription
                            .format("%s: previousKey [%s] <= nextKey [%s] not matched", this, previousKey, nextKey));
                }
            }
            IHistoricalEntry<V> shiftKeyValueEntry = null;
            if (previousKey != null) {
                shiftKeyValueEntry = putPrevious(previousKey, value, valueKey, notifyPutListeners);
            }
            if (nextKey != null) {
                putNext(nextKey, value, valueKey, shiftKeyValueEntry);
            }
            if (previousKey == null && nextKey == null) {
                //set value only if not already done by putPrevious or putNext
                shiftKeyProvider.put(valueKey, value);
            }
        }

        @SuppressWarnings("rawtypes")
        private IHistoricalEntry<V> putPrevious(final FDate previousKey, final V value, final FDate valueKey,
                final boolean notifyPutListeners) {
            final int compare = previousKey.compareTo(valueKey);
            if (!(compare <= 0)) {
                throw new IllegalArgumentException(TextDescription
                        .format("%s: previousKey [%s] <= value [%s] not matched", this, previousKey, valueKey));
            }
            IHistoricalEntry<V> newShiftKeyValueEntry = null;
            if (compare != 0) {
                //from value to previous backward
                newShiftKeyValueEntry = shiftKeyProvider.put(previousKey, valueKey, value, null, null);
                //from previous to value forward
                shiftKeyProvider.put(null, previousKey, null, null, valueKey);
                if (notifyPutListeners) {
                    queryCore.putPrevious(previousKey, value, valueKey);
                    if (!putListenersFast.isEmpty()) {
                        final WeakReference[] array = putListenersFast.asArray(WEAKREFERENCE_EMPTY_ARRAY);
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
            return newShiftKeyValueEntry;
        }

        private void putNext(final FDate nextKey, final V value, final FDate valueKey,
                final IHistoricalEntry<V> shiftKeyValueEntry) {
            final int compare = nextKey.compareTo(valueKey);
            if (!(compare >= 0)) {
                throw new IllegalArgumentException(
                        TextDescription.format("%s: nextKey [%s] >= value [%s] not matched", this, nextKey, valueKey));
            }
            if (compare != 0) {
                //from value to next forward
                shiftKeyProvider.put(null, valueKey, value, shiftKeyValueEntry, nextKey);
                //from next to value backward
                shiftKeyProvider.put(valueKey, nextKey, null, null, null);
            }
        }

        @Override
        public Set<IHistoricalCachePutListener> getPutListeners() {
            return Collections.unmodifiableSet(putListeners);
        }

        @Override
        public boolean registerPutListener(final IHistoricalCachePutListener l) {
            if (l == AHistoricalCache.this) {
                return false;
            }
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
                final WeakReference[] array = putListenersFast.asArray(WEAKREFERENCE_EMPTY_ARRAY);
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

    protected IValuesMap<V> newValuesMap() {
        return new ValuesMap();
    }

    @Override
    public void preloadData(final ExecutorService executor) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final IHistoricalCacheQueryWithFuture<V> query = query().setFutureEnabled();
                query.getValue(FDates.MAX_DATE);
                query.getValue(FDates.MIN_DATE);
            }
        });
    }

    @Override
    public int size() {
        return valuesMap.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return null;
        }
    }

}
