package de.invesdwin.util.collections.loadingcache.historical.listener;

public interface IHistoricalCacheIncreaseMaximumSizeListener {

    void increaseMaximumSize(int maximumSize, String reason);

}
