package de.invesdwin.util.collections.loadingcache.historical.listener;

public interface IHistoricalCacheIncreaseMaximumSizeListener {

    void increaseMaximumSize(int maximumSize, String reason);

    /**
     * This might be a positive number even if caching is disabled, check the flag as well.
     */
    Integer getMaximumSize();

    int getMaximumSizeLimit();

    boolean isCachingEnabled();

}
