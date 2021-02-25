package de.invesdwin.util.collections.loadingcache.historical.listener;

public interface IHistoricalCacheIncreaseMaximumSizeListener {

    void increaseMaximumSize(int maximumSize, String reason);

    /**
     * This might be a positive number even if caching is disabled, check the flag as well.
     */
    Integer getMaximumSize();

    default Integer getMaximumSizeIfCachingEnabled() {
        if (isCachingEnabled()) {
            return getMaximumSize();
        } else {
            return 0;
        }
    }

    int getMaximumSizeLimit();

    boolean isCachingEnabled();

}
