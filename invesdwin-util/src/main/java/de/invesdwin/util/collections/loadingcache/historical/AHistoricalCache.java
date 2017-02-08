package de.invesdwin.util.collections.loadingcache.historical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import org.assertj.core.description.TextDescription;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ADelegateLoadingCache;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.HistoricalCacheRangeQueryInterceptorSupport;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.key.IHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.DelegateHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheExtractKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.key.internal.IHistoricalCacheShiftKeyProvider;
import de.invesdwin.util.collections.loadingcache.historical.listener.IHistoricalCacheOnValueLoadedListener;
import de.invesdwin.util.collections.loadingcache.historical.query.IHistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.HistoricalCacheQuery;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.IHistoricalCacheInternalMethods;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.CachedHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.query.internal.core.IHistoricalCacheQueryCore;
import de.invesdwin.util.collections.loadingcache.historical.refresh.HistoricalCacheRefreshManager;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public abstract class AHistoricalCache<V> {

    public static final Integer DISABLED_MAXIMUM_SIZE = 0;
    public static final Integer UNLIMITED_MAXIMUM_SIZE = null;
    /**
     * 10k is normally sufficient for daily bars of stocks and also fast enough for intraday ticks to load.
     */
    public static final Integer DEFAULT_MAXIMUM_SIZE = 1000;
    protected final IHistoricalCacheInternalMethods<V> internalMethods = new HistoricalCacheInternalMethods();

    private final List<ALoadingCache<?, ?>> increaseMaximumSizeListeners = new ArrayList<ALoadingCache<?, ?>>();

    private final IHistoricalCacheQueryCore<V> queryCore = newHistoricalCacheQueryCore();
    private IHistoricalCacheAdjustKeyProvider adjustKeyProvider = new InnerHistoricalCacheAdjustKeyProvider();
    private IHistoricalCacheOnValueLoadedListener<V> onValueLoadedListener = new InnerHistoricalCacheOnValueLoadedListener();

    private volatile FDate lastRefresh = HistoricalCacheRefreshManager.getLastRefresh();
    private final Integer initialMaximumSize = newInitialMaximumSize();
    private boolean isPutDisabled = initialMaximumSize != null && initialMaximumSize == 0;
    private volatile Integer maximumSize = initialMaximumSize;
    private IHistoricalCacheShiftKeyProvider shiftKeyProvider = new InnerHistoricalCacheShiftKeyProvider();
    private IHistoricalCacheExtractKeyProvider<V> extractKeyProvider = new InnerHistoricalCacheExtractKeyProvider();
    private final ILoadingCache<FDate, V> valuesMap = new ADelegateLoadingCache<FDate, V>() {

        @Override
        public V get(final FDate key) {
            final FDate adjKey = adjustKey(key);
            return super.get(adjKey);
        }

        @Override
        protected ILoadingCache<FDate, V> createDelegate() {
            return newLoadingCacheProvider(new Function<FDate, V>() {

                @Override
                public V apply(final FDate key) {
                    final V value = AHistoricalCache.this.loadValue(key);
                    onValueLoadedListener.onValueLoaded(key, value);
                    return value;
                }

            }, getMaximumSize());
        }
    };

    /**
     * null means unlimited and 0 means no caching at all.
     */
    protected Integer newInitialMaximumSize() {
        return DEFAULT_MAXIMUM_SIZE;
    }

    public final Integer getMaximumSize() {
        return maximumSize;
    }

    public final Integer getInitialMaximumSize() {
        return initialMaximumSize;
    }

    public synchronized void increaseMaximumSize(final int maximumSize) {
        final Integer existingMaximumSize = this.maximumSize;
        if (existingMaximumSize == null || existingMaximumSize < maximumSize) {
            for (final ALoadingCache<?, ?> l : increaseMaximumSizeListeners) {
                l.increaseMaximumSize(maximumSize);
            }
            queryCore.increaseMaximumSize(maximumSize);
            this.maximumSize = maximumSize;
        }
    }

    protected IHistoricalCacheQueryCore<V> newHistoricalCacheQueryCore() {
        /*
         * always use lookback cache to make getPreviousXyz faster even though this instance might not cache anything in
         * the values map
         */
        return new CachedHistoricalCacheQueryCore<V>(internalMethods);
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
            this.extractKeyProvider = new DelegateHistoricalCacheExtractKeyProvider<V>(
                    (AHistoricalCache<Object>) shiftKeyDelegate);
        }
        isPutDisabled = false;
    }

    protected FDate adjustKey(final FDate key) {
        final FDate lastRefreshFromManager = HistoricalCacheRefreshManager.getLastRefresh();
        if (lastRefresh.isBefore(lastRefreshFromManager)) {
            lastRefresh = new FDate();
            maybeRefresh();
        }
        return adjustKeyProvider.adjustKey(key);
    }

    protected boolean maybeRefresh() {
        clear();
        return true;
    }

    protected abstract V loadValue(FDate key);

    protected <T> ILoadingCache<FDate, T> newLoadingCacheProvider(final Function<FDate, T> loadValue,
            final Integer maximumSize) {
        final ALoadingCache<FDate, T> loadingCache = new ALoadingCache<FDate, T>() {

            @Override
            protected Integer newInitialMaximumSize() {
                return maximumSize;
            }

            @Override
            protected T loadValue(final FDate key) {
                return loadValue.apply(key);
            }

        };
        increaseMaximumSizeListeners.add(loadingCache);
        return loadingCache;
    }

    /**
     * Should return the key if the value does not contain a key itself.
     */
    public final FDate extractKey(final FDate key, final V value) {
        return extractKeyProvider.extractKey(key, value);
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
        return shiftKeyProvider.calculatePreviousKey(key);
    }

    protected final FDate calculateNextKey(final FDate key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null!");
        }
        return shiftKeyProvider.calculateNextKey(key);
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
        return queryCore.newQuery();
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

    private void putPrevious(final FDate previousKey, final V value, final FDate valueKey) {
        final int compare = previousKey.compareTo(valueKey);
        if (!(compare <= 0)) {
            throw new IllegalArgumentException(
                    new TextDescription("%s: previousKey [%s] <= value [%s] not matched", this, previousKey, valueKey)
                            .toString());
        }
        if (compare != 0) {
            shiftKeyProvider.getPreviousKeysCache().put(valueKey, previousKey);
            shiftKeyProvider.getNextKeysCache().put(previousKey, valueKey);
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
        lastRefresh = HistoricalCacheRefreshManager.getLastRefresh();
    }

    protected IHistoricalCacheRangeQueryInterceptor<V> getQueryInterceptor() {
        return new HistoricalCacheRangeQueryInterceptorSupport<V>();
    }

    public void setOnValueLoadedListener(final IHistoricalCacheOnValueLoadedListener<V> onValueLoadedListener) {
        Assertions.assertThat(onValueLoadedListener)
                .as("%s can only be set once, maybe you should chain them?",
                        IHistoricalCacheOnValueLoadedListener.class.getSimpleName())
                .isInstanceOf(InnerHistoricalCacheOnValueLoadedListener.class);
        this.onValueLoadedListener = onValueLoadedListener;
    }

    public IHistoricalCacheOnValueLoadedListener<V> getOnValueLoadedListener() {
        return onValueLoadedListener;
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
            return AHistoricalCache.this.getQueryInterceptor();
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
        public Integer getInitialMaximumSize() {
            return AHistoricalCache.this.getInitialMaximumSize();
        }

        @Override
        public void increaseMaximumSize(final int maximumSize) {
            AHistoricalCache.this.increaseMaximumSize(maximumSize);
        }

        @Override
        public HistoricalCacheQuery<?> newKeysQueryInterceptor() {
            return (HistoricalCacheQuery<?>) AHistoricalCache.this.getShiftKeyProvider().newKeysQueryInterceptor();
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
                final FDate adjKey = adjustKey(key);
                return super.get(adjKey);
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
            public void put(final FDate key, final FDate value) {
                //don't cache null values to prevent moving time issues of the underlying source (e.g. JForexTickCache getNextValue)
                if (value != null && !key.equals(value)) {
                    super.put(key, value);
                }
            }

            @Override
            public FDate get(final FDate key) {
                final FDate adjKey = adjustKey(key);
                return super.get(adjKey);
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

    }

    private final class InnerHistoricalCacheOnValueLoadedListener implements IHistoricalCacheOnValueLoadedListener<V> {

        @Override
        public void onValueLoaded(final FDate key, final V value) {}

    }

}
